package co.edu.uniquindio.servify.features.request.create

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.edu.uniquindio.servify.domain.model.Location
import co.edu.uniquindio.servify.domain.model.Publication
import co.edu.uniquindio.servify.domain.model.RequestStatus
import co.edu.uniquindio.servify.domain.model.ServiceRequest
import co.edu.uniquindio.servify.domain.repository.ServiceRequestRepository
import co.edu.uniquindio.servify.domain.repository.UserRepository
import co.edu.uniquindio.servify.domain.usecase.publication.GetPublicationByIdUseCase
import co.edu.uniquindio.servify.domain.usecase.request.CannotRequestOwnServiceException
import co.edu.uniquindio.servify.domain.usecase.request.CreateServiceRequestUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.ZoneId
import javax.inject.Inject

data class RequestServiceUiState(
    val publication: Publication? = null,
    val scheduledDateMillis: Long = System.currentTimeMillis() + 86400000L,
    val selectedTimeSlot: String = "02:00 PM",
    val occupiedTimeSlots: Set<String> = emptySet(),
    val note: String = "",
    val noteError: String? = null,
    val address: String = "",
    val latitude: Double? = null,
    val longitude: Double? = null,
    val totalAmount: Double = 80000.0,
    val isSubmitting: Boolean = false,
    val isOwnService: Boolean = false,
    val createdRequestId: String? = null,
    val isSuccess: Boolean = false,
    val userMessage: String? = null
)

@HiltViewModel
class RequestServiceViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getPublicationByIdUseCase: GetPublicationByIdUseCase,
    private val createServiceRequestUseCase: CreateServiceRequestUseCase,
    private val serviceRequestRepository: ServiceRequestRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    val serviceId: String = savedStateHandle.get<String>("serviceId") ?: "pub_1"

    private val _uiState = MutableStateFlow(RequestServiceUiState())
    val uiState: StateFlow<RequestServiceUiState> = _uiState.asStateFlow()

    init {
        loadPublication()
        observeOccupiedSlots()
    }

    private fun loadPublication() {
        viewModelScope.launch {
            val pub = getPublicationByIdUseCase(serviceId)
            if (pub != null) {
                val currentUser = userRepository.observeCurrentUser().first()
                val isOwnService = pub.belongsTo(currentUser.id, currentUser.email)
                _uiState.update {
                    it.copy(
                        publication = pub,
                        totalAmount = pub.price,
                        isOwnService = isOwnService,
                        userMessage = if (isOwnService) {
                            "No puedes solicitar un servicio que tú mismo publicaste"
                        } else {
                            it.userMessage
                        }
                    )
                }
            }
        }
    }

    private fun observeOccupiedSlots() {
        viewModelScope.launch {
            serviceRequestRepository.observeRequests().collect { requests ->
                updateOccupiedSlots(requests)
            }
        }
    }

    private fun updateOccupiedSlots(requests: List<ServiceRequest>) {
        val state = _uiState.value
        val nowMillis = System.currentTimeMillis()

        val activeOnDate = requests.filter { req ->
            req.publicationId == serviceId &&
                    req.status != RequestStatus.CANCELLED &&
                    req.status != RequestStatus.REJECTED &&
                    isSameDay(req.scheduledDateTime, state.scheduledDateMillis)
        }
        val occupiedFromRequests = activeOnDate.map { req ->
            formatTimeSlot(req.scheduledDateTime)
        }.toSet()

        val availableSlots = listOf("08:00 AM", "10:00 AM", "02:00 PM", "04:00 PM", "06:00 PM")
        val pastSlots = availableSlots.filter { slot ->
            val slotMillis = combineDateAndTime(state.scheduledDateMillis, slot)
            slotMillis <= nowMillis
        }.toSet()

        val unavailable = occupiedFromRequests + pastSlots

        _uiState.update { current ->
            val newSelectedSlot = if (unavailable.contains(current.selectedTimeSlot)) {
                availableSlots.firstOrNull { !unavailable.contains(it) } ?: current.selectedTimeSlot
            } else {
                current.selectedTimeSlot
            }
            current.copy(occupiedTimeSlots = unavailable, selectedTimeSlot = newSelectedSlot)
        }
    }

    fun onDateSelected(dateMillis: Long) {
        _uiState.update { it.copy(scheduledDateMillis = dateMillis) }
        viewModelScope.launch {
            val requests = serviceRequestRepository.observeRequests().first()
            updateOccupiedSlots(requests)
        }
    }

    fun onTimeSlotSelected(timeSlot: String) {
        _uiState.update { it.copy(selectedTimeSlot = timeSlot) }
    }

    fun onNoteChanged(newNote: String) {
        _uiState.update { it.copy(note = newNote) }
    }

    fun onLocationPicked(latitude: Double, longitude: Double, address: String) {
        _uiState.update {
            it.copy(
                address = address,
                latitude = latitude,
                longitude = longitude
            )
        }
    }

    fun onSubmitRequest() {
        viewModelScope.launch {
            _uiState.update { it.copy(isSubmitting = true) }

            val state = _uiState.value
            if (state.isOwnService) {
                _uiState.update {
                    it.copy(
                        isSubmitting = false,
                        userMessage = "No puedes solicitar un servicio que tú mismo publicaste"
                    )
                }
                return@launch
            }

            val combinedDateTime = combineDateAndTime(state.scheduledDateMillis, state.selectedTimeSlot)

            if (combinedDateTime <= System.currentTimeMillis()) {
                _uiState.update {
                    it.copy(
                        isSubmitting = false,
                        userMessage = "No puedes reservar en un horario que ya transcurrió. Selecciona una hora futura."
                    )
                }
                return@launch
            }

            if (state.occupiedTimeSlots.contains(state.selectedTimeSlot)) {
                _uiState.update {
                    it.copy(
                        isSubmitting = false,
                        userMessage = "Este horario ya se encuentra ocupado. Por favor selecciona otro."
                    )
                }
                return@launch
            }

            val locationObj = if (state.latitude != null && state.longitude != null) {
                Location(latitude = state.latitude, longitude = state.longitude, address = state.address.ifBlank { null })
            } else if (state.address.isNotBlank()) {
                Location(latitude = 0.0, longitude = 0.0, address = state.address)
            } else null

            runCatching {
                createServiceRequestUseCase(
                    publicationId = serviceId,
                    scheduledDateTime = combinedDateTime,
                    note = state.note.ifBlank { null },
                    location = locationObj,
                    totalAmount = state.totalAmount
                )
            }.onSuccess { request ->
                _uiState.update {
                    it.copy(
                        isSubmitting = false,
                        createdRequestId = request.id,
                        isSuccess = true
                    )
                }
            }.onFailure { error ->
                val message = if (error is CannotRequestOwnServiceException) {
                    error.message
                } else {
                    error.message ?: "No se pudo enviar la solicitud. Intenta nuevamente."
                }
                _uiState.update {
                    it.copy(
                        isSubmitting = false,
                        isOwnService = error is CannotRequestOwnServiceException,
                        userMessage = message
                    )
                }
            }
        }
    }

    fun onMessageShown() {
        _uiState.update { it.copy(userMessage = null) }
    }
}

internal fun combineDateAndTime(dateMillis: Long, timeSlot: String): Long {
    val zone = ZoneId.systemDefault()
    val localDate = Instant.ofEpochMilli(dateMillis).atZone(zone).toLocalDate()
    val hour = when (timeSlot) {
        "08:00 AM" -> 8
        "10:00 AM" -> 10
        "02:00 PM" -> 14
        "04:00 PM" -> 16
        "06:00 PM" -> 18
        else -> 14
    }
    return localDate.atTime(hour, 0).atZone(zone).toInstant().toEpochMilli()
}

private fun formatTimeSlot(scheduledDateTime: Long): String {
    val zone = ZoneId.systemDefault()
    val hour = Instant.ofEpochMilli(scheduledDateTime).atZone(zone).hour
    return when (hour) {
        8 -> "08:00 AM"
        10 -> "10:00 AM"
        14 -> "02:00 PM"
        16 -> "04:00 PM"
        18 -> "06:00 PM"
        else -> "%02d:00".format(hour)
    }
}

private fun isSameDay(millis1: Long, millis2: Long): Boolean {
    val zone = ZoneId.systemDefault()
    val date1 = Instant.ofEpochMilli(millis1).atZone(zone).toLocalDate()
    val date2 = Instant.ofEpochMilli(millis2).atZone(zone).toLocalDate()
    return date1 == date2
}
