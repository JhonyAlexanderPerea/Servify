package co.edu.uniquindio.servify.features.login

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class LoginViewModel : ViewModel() {

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
                emailError = error
            )
        }
    }

    fun onPasswordChange(value: String) {

        _uiState.update {
            it.copy(
                password = value,
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
        // Más adelante se conectará al Repository/API.
    }
}