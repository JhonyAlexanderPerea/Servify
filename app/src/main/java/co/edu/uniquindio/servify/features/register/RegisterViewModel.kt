package co.edu.uniquindio.servify.features.register

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class RegisterViewModel : ViewModel() {

    private val _uiState =
        MutableStateFlow(RegisterUiState())

    val uiState: StateFlow<RegisterUiState> =
        _uiState.asStateFlow()

    fun onNameChange(value: String) {
        _uiState.update {
            it.copy(name = value)
        }
    }

    fun onLastNameChange(value: String) {
        _uiState.update {
            it.copy(lastName = value)
        }
    }

    fun onEmailChange(value: String) {
        _uiState.update {
            it.copy(email = value)
        }
    }

    fun onPasswordChange(value: String) {
        _uiState.update {
            it.copy(password = value)
        }
    }

    fun onConfirmPasswordChange(value: String) {
        _uiState.update {
            it.copy(confirmPassword = value)
        }
    }

    fun onCityChange(value: String) {
        _uiState.update {
            it.copy(city = value)
        }
    }

    fun toggleTerms() {
        _uiState.update {
            it.copy(
                termsAccepted = !it.termsAccepted
            )
        }
    }

    fun nextStep() {

        if (_uiState.value.firstStepValid) {

            _uiState.update {
                it.copy(step = 2)
            }
        }
    }

    fun previousStep() {

        _uiState.update {
            it.copy(step = 1)
        }
    }

    fun register() {
        // Posteriormente conectaremos Repository/API.
    }
}