package co.edu.uniquindio.servify.features.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.edu.uniquindio.servify.di.AppContainer
import co.edu.uniquindio.servify.domain.model.AuthResult
import co.edu.uniquindio.servify.domain.repository.AuthRepository
import co.edu.uniquindio.servify.features.auth.toMessage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class LoginViewModel(
    private val authRepository: AuthRepository = AppContainer.authRepository
) : ViewModel() {

    private val _uiState =
        MutableStateFlow(LoginUiState())

    val uiState: StateFlow<LoginUiState> =
        _uiState.asStateFlow()

    fun onEmailChange(value: String) {

        val error = when {
            value.isBlank() ->
                "El correo es obligatorio"

            !android.util.Patterns.EMAIL_ADDRESS
                .matcher(value)
                .matches() ->
                "Correo electrónico inválido"

            else -> null
        }

        _uiState.update {
            it.copy(
                email = value,
                emailError = error,
                loginError = null
            )
        }
    }

    fun onPasswordChange(value: String) {

        _uiState.update {
            it.copy(
                password = value,
                loginError = null,
                passwordError =
                    if (value.length < 8)
                        "Mínimo 8 caracteres"
                    else
                        null
            )
        }
    }

    fun togglePasswordVisibility() {

        _uiState.update {
            it.copy(
                showPassword = !it.showPassword
            )
        }
    }

    fun toggleRememberMe() {

        _uiState.update {
            it.copy(
                rememberMe = !it.rememberMe
            )
        }
    }

    fun login() {

        val state = _uiState.value
        if (!state.isValid || state.isLoading) return

        _uiState.update { it.copy(isLoading = true, loginError = null) }

        viewModelScope.launch {
            when (val result = authRepository.login(state.email, state.password)) {
                is AuthResult.Success -> _uiState.update {
                    it.copy(isLoading = false, loggedInRole = result.user.role)
                }

                is AuthResult.Failure -> _uiState.update {
                    it.copy(isLoading = false, loginError = result.error.toMessage())
                }
            }
        }
    }

    /** El acceso con Google llega con Firebase Auth (fase 3). Por ahora se informa en pantalla. */
    fun onGoogleClick() {
        _uiState.update {
            it.copy(loginError = "El acceso con Google estará disponible próximamente")
        }
    }
}
