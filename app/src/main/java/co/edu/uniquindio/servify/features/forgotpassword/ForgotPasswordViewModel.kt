package co.edu.uniquindio.servify.features.forgotpassword

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.edu.uniquindio.servify.domain.model.PasswordResetResult
import co.edu.uniquindio.servify.domain.model.UserRole
import co.edu.uniquindio.servify.domain.rules.CredentialRules
import co.edu.uniquindio.servify.domain.usecase.auth.SendPasswordResetUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Recuperación de contraseña, compartida por usuario y moderador: el rol llega como argumento de
 * navegación ([ARG_ROLE]) y se pasa al caso de uso, que limita la recuperación a cuentas de ese rol.
 */
@HiltViewModel
class ForgotPasswordViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val sendPasswordResetUseCase: SendPasswordResetUseCase
) : ViewModel() {

    private val role: UserRole = savedStateHandle.get<String>(ARG_ROLE)
        ?.let { name -> UserRole.entries.firstOrNull { it.name == name } }
        ?: UserRole.USER

    private val _uiState = MutableStateFlow(ForgotPasswordUiState(role = role))

    val uiState: StateFlow<ForgotPasswordUiState> = _uiState.asStateFlow()

    fun onEmailChange(value: String) {
        val error = when {
            value.isBlank() -> "El correo es obligatorio"
            !CredentialRules.isValidEmail(value) -> "Correo electrónico inválido"
            else -> null
        }
        _uiState.update { it.copy(email = value, error = error) }
    }

    fun sendRecoveryEmail() = send(isResend = false)

    /** Reenvía el enlace al mismo correo sin salir de la pantalla de "Correo enviado". */
    fun resend() = send(isResend = true)

    private fun send(isResend: Boolean) {
        val state = _uiState.value
        if (!state.canSend) return

        _uiState.update { it.copy(isLoading = true, error = null, resent = false) }

        viewModelScope.launch {
            when (sendPasswordResetUseCase(state.email, role)) {
                PasswordResetResult.Sent -> _uiState.update {
                    it.copy(isLoading = false, sent = true, resent = isResend)
                }

                PasswordResetResult.InvalidEmail -> _uiState.update {
                    it.copy(isLoading = false, error = "Correo electrónico inválido")
                }
            }
        }
    }

    companion object {
        /** Nombre del argumento de navegación con el rol (USER | MODERATOR). */
        const val ARG_ROLE = "role"
    }
}
