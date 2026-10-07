package co.edu.uniquindio.servify.features.forgotpassword

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class ForgotPasswordViewModel : ViewModel() {

    private val _uiState =
        MutableStateFlow(ForgotPasswordUiState())

    val uiState: StateFlow<ForgotPasswordUiState> =
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
                error = error
            )
        }
    }

    fun sendRecoveryEmail() {

        val state = _uiState.value

        if (
            state.email.isBlank() ||
            state.error != null
        ) {
            return
        }

        _uiState.update {
            it.copy(
                sent = true
            )
        }
    }

    fun resend() {

        _uiState.update {
            it.copy(
                sent = false
            )
        }
    }
}