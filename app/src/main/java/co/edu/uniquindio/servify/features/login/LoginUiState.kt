package co.edu.uniquindio.servify.features.login

data class LoginUiState(
    val email: String = "jhony.ramirez@gmail.com",
    val password: String = "12345678",
    val showPassword: Boolean = false,
    val rememberMe: Boolean = true,
    val isLoading: Boolean = false,
    val emailError: String? = null,
    val passwordError: String? = null
) {

    val isValid: Boolean
        get() =
            email.isNotBlank() &&
                    password.isNotBlank() &&
                    emailError == null &&
                    passwordError == null
}