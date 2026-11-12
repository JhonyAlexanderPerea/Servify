package co.edu.uniquindio.servify.features.resetpassword

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.edu.uniquindio.servify.domain.model.ConfirmPasswordResetResult
import co.edu.uniquindio.servify.domain.rules.CredentialRules
import co.edu.uniquindio.servify.domain.usecase.auth.ConfirmPasswordResetUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/** Restablece la contraseña con el código del enlace de recuperación. */
@HiltViewModel
class ResetPasswordViewModel @Inject constructor(
    private val confirmPasswordResetUseCase: ConfirmPasswordResetUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(ResetPasswordUiState())

    val uiState: StateFlow<ResetPasswordUiState> = _uiState.asStateFlow()

    fun onPasswordChange(value: String) {
        _uiState.update { state ->
            val confirmationError =
                if (state.confirmPassword.isNotEmpty() && state.confirmPassword != value)
                    "Las contraseñas no coinciden"
                else
                    null

            state.copy(
                password = value,
                passwordError =
                    if (!CredentialRules.isStrongEnough(value))
                        "Mínimo ${CredentialRules.MIN_PASSWORD_LENGTH} caracteres"
                    else
                        null,
                confirmPasswordError = confirmationError,
                message = null
            )
        }
    }

    fun onConfirmPasswordChange(value: String) {
        _uiState.update { state ->
            state.copy(
                confirmPassword = value,
                confirmPasswordError =
                    if (value.isNotEmpty() && value != state.password)
                        "Las contraseñas no coinciden"
                    else
                        null,
                message = null
            )
        }
    }

    fun togglePasswordVisibility() {
        _uiState.update {
            it.copy(showPassword = !it.showPassword)
        }
    }

    fun onSubmit(oobCode: String) {
        val state = _uiState.value
        if (!state.isValid || state.isLoading) return

        _uiState.update {
            it.copy(
                isLoading = true,
                message = null,
                success = false,
                invalidCode = false
            )
        }

        viewModelScope.launch {
            runCatching { confirmPasswordResetUseCase(oobCode, state.password) }
                .fold(
                    onSuccess = { result ->
                        when (result) {
                            ConfirmPasswordResetResult.SUCCESS -> _uiState.update {
                                it.copy(isLoading = false, success = true)
                            }

                            ConfirmPasswordResetResult.INVALID_OR_EXPIRED_CODE -> _uiState.update {
                                // TODO(i18n): mover a strings.xml
                                it.copy(
                                    isLoading = false,
                                    invalidCode = true,
                                    message = "El enlace no es válido o ya venció. Solicita uno nuevo."
                                )
                            }

                            ConfirmPasswordResetResult.WEAK_PASSWORD -> _uiState.update {
                                it.copy(
                                    isLoading = false,
                                    passwordError = "La contraseña debe tener mínimo ${CredentialRules.MIN_PASSWORD_LENGTH} caracteres"
                                )
                            }
                        }
                    },
                    onFailure = {
                        // TODO(i18n): mover a strings.xml
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                message = "No se pudo restablecer la contraseña. Revisa tu conexión e inténtalo de nuevo."
                            )
                        }
                    }
                )
        }
    }
}
