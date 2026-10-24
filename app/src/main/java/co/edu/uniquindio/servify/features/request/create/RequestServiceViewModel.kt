package co.edu.uniquindio.servify.features.request.create

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.edu.uniquindio.servify.domain.model.Publication
import co.edu.uniquindio.servify.domain.usecase.publication.GetPublicationByIdUseCase
import co.edu.uniquindio.servify.domain.usecase.request.CreateServiceRequestUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class RequestServiceUiState(
    val publication: Publication? = null,
    val scheduledDateMillis: Long = System.currentTimeMillis() + 86400000L,
    val selectedTimeSlot: String = "02:00 PM",
    val note: String = "",
    val noteError: String? = null,
    val totalAmount: Double = 80000.0,
    val isSubmitting: Boolean = false,
    val createdRequestId: String? = null,
    val isSuccess: Boolean = false,
    val userMessage: String? = null
)

@HiltViewModel
class RequestServiceViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getPublicationByIdUseCase: GetPublicationByIdUseCase,
    private val createServiceRequestUseCase: CreateServiceRequestUseCase
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
                _uiState.update {
                    it.copy(
                        publication = pub,
                        totalAmount = pub.price
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
            val request = createServiceRequestUseCase(
                publicationId = serviceId,
                scheduledDateTime = state.scheduledDateMillis,
                note = state.note.ifBlank { null },
                totalAmount = state.totalAmount
            )

            _uiState.update {
                it.copy(
                    isSubmitting = false,
                    createdRequestId = request.id,
                    isSuccess = true
                )
            }
        }
    }
}
