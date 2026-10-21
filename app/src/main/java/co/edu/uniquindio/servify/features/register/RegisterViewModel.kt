package co.edu.uniquindio.servify.features.register

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.edu.uniquindio.servify.di.AppContainer
import co.edu.uniquindio.servify.domain.model.AuthError
import co.edu.uniquindio.servify.domain.model.AuthResult
import co.edu.uniquindio.servify.domain.repository.AuthRepository
import co.edu.uniquindio.servify.features.auth.toMessage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class RegisterViewModel(
    private val authRepository: AuthRepository = AppContainer.authRepository
) : ViewModel() {

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
            it.copy(email = value, emailError = null)
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
                termsAccepted = !it.termsAccepted,
                registerError = null
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

        val state = _uiState.value
        if (!state.secondStepValid || state.isLoading) return

        _uiState.update { it.copy(isLoading = true, registerError = null) }

        viewModelScope.launch {
            val result = authRepository.register(
                name = state.name,
                lastName = state.lastName,
                email = state.email,
                password = state.password,
                city = state.city
            )

            when (result) {
                is AuthResult.Success -> _uiState.update {
                    it.copy(isLoading = false, registered = true)
                }

                is AuthResult.Failure -> _uiState.update {
                    // El email se captura en el paso 1: si falla por él, se vuelve a ese paso.
                    if (result.error == AuthError.EMAIL_ALREADY_REGISTERED ||
                        result.error == AuthError.INVALID_EMAIL
                    ) {
                        it.copy(isLoading = false, step = 1, emailError = result.error.toMessage())
                    } else {
                        it.copy(isLoading = false, registerError = result.error.toMessage())
                    }
                }
            }
        }
    }
}
