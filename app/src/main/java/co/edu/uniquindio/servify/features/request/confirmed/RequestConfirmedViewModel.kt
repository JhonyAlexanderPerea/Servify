package co.edu.uniquindio.servify.features.request.confirmed

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.edu.uniquindio.servify.domain.model.Publication
import co.edu.uniquindio.servify.domain.model.ServiceRequest
import co.edu.uniquindio.servify.domain.repository.ServiceRequestRepository
import co.edu.uniquindio.servify.domain.usecase.publication.GetPublicationByIdUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class RequestConfirmedUiState(
    val request: ServiceRequest? = null,
    val publication: Publication? = null,
    val isLoading: Boolean = true
)

@HiltViewModel
class RequestConfirmedViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val serviceRequestRepository: ServiceRequestRepository,
    private val getPublicationByIdUseCase: GetPublicationByIdUseCase
) : ViewModel() {

    val requestId: String = savedStateHandle.get<String>("requestId").orEmpty()

    private val _uiState = MutableStateFlow(RequestConfirmedUiState())
    val uiState: StateFlow<RequestConfirmedUiState> = _uiState.asStateFlow()

    init {
        if (requestId.isNotBlank()) {
            viewModelScope.launch {
                val req = serviceRequestRepository.getById(requestId)
                val pub = req?.let { getPublicationByIdUseCase(it.publicationId) }
                _uiState.value = RequestConfirmedUiState(
                    request = req,
                    publication = pub,
                    isLoading = false
                )
            }
        } else {
            _uiState.value = RequestConfirmedUiState(isLoading = false)
        }
    }
}
