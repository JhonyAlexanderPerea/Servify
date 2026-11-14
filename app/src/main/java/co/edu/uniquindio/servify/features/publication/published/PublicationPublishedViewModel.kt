package co.edu.uniquindio.servify.features.publication.published

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.edu.uniquindio.servify.domain.model.Publication
import co.edu.uniquindio.servify.domain.usecase.publication.GetPublicationByIdUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class PublicationPublishedUiState(
    val publication: Publication? = null,
    val isLoading: Boolean = true
)

@HiltViewModel
class PublicationPublishedViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getPublicationByIdUseCase: GetPublicationByIdUseCase
) : ViewModel() {

    val publicationId: String = savedStateHandle.get<String>("publicationId").orEmpty()

    private val _uiState = MutableStateFlow(PublicationPublishedUiState())
    val uiState: StateFlow<PublicationPublishedUiState> = _uiState.asStateFlow()

    init {
        if (publicationId.isNotBlank()) {
            viewModelScope.launch {
                val found = getPublicationByIdUseCase(publicationId)
                _uiState.value = PublicationPublishedUiState(publication = found, isLoading = false)
            }
        } else {
            _uiState.value = PublicationPublishedUiState(isLoading = false)
        }
    }
}
