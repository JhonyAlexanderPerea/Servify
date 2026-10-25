package co.edu.uniquindio.servify.features.login

import co.edu.uniquindio.servify.domain.model.UserRole

data class LoginUiState(
    val email: String = "",
    val password: String = "",
    val showPassword: Boolean = false,
    val rememberMe: Boolean = false,
    val isLoading: Boolean = false,
    val emailError: String? = null,
    val passwordError: String? = null,
    /** Error de autenticación (credenciales incorrectas, etc.). */
    val loginError: String? = null,
    /** Se llena al iniciar sesión con éxito; la pantalla navega según el rol. */
    val loggedInRole: UserRole? = null
) {

    val isValid: Boolean
        get() =
            email.isNotBlank() &&
                    password.isNotBlank() &&
                    emailError == null &&
                    passwordError == null
}