package co.edu.uniquindio.servify.features.request.create

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.edu.uniquindio.servify.domain.model.Publication
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
    val note: String = "",
    val noteError: String? = null,
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
    private val userRepository: UserRepository
) : ViewModel() {

    val serviceId: String = savedStateHandle.get<String>("serviceId") ?: "pub_1"

    private val _uiState = MutableStateFlow(RequestServiceUiState())
    val uiState: StateFlow<RequestServiceUiState> = _uiState.asStateFlow()

    init {
        loadPublication()
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

    fun onDateSelected(dateMillis: Long) {
        _uiState.update { it.copy(scheduledDateMillis = dateMillis) }
    }

    fun onTimeSlotSelected(timeSlot: String) {
        _uiState.update { it.copy(selectedTimeSlot = timeSlot) }
    }

    fun onNoteChanged(newNote: String) {
        _uiState.update { it.copy(note = newNote) }
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
            runCatching {
                createServiceRequestUseCase(
                    publicationId = serviceId,
                    scheduledDateTime = combinedDateTime,
                    note = state.note.ifBlank { null },
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

private fun combineDateAndTime(dateMillis: Long, timeSlot: String): Long {
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
