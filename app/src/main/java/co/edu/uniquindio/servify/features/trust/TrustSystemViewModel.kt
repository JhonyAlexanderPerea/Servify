package co.edu.uniquindio.servify.features.trust

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.edu.uniquindio.servify.domain.usecase.trust.CalculateTrustIndexUseCase
import co.edu.uniquindio.servify.domain.usecase.trust.TrustIndexDetails
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class TrustSystemUiState(
    val details: TrustIndexDetails? = null,
    val isLoading: Boolean = false,
    val userMessage: String? = null
)

@HiltViewModel
class TrustSystemViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val calculateTrustIndexUseCase: CalculateTrustIndexUseCase
) : ViewModel() {

    val providerId: String = savedStateHandle.get<String>("providerId") ?: "u_juan"

    private val _uiState = MutableStateFlow(TrustSystemUiState(isLoading = true))
    val uiState: StateFlow<TrustSystemUiState> = _uiState.asStateFlow()

    init {
        loadTrustDetails()
    }

    fun loadTrustDetails() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val details = calculateTrustIndexUseCase(providerId)
            _uiState.update {
                it.copy(
                    details = details,
                    isLoading = false
                )
            }
        }
    }
}
