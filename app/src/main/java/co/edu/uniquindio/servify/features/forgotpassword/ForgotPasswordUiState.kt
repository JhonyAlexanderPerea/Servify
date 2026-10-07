package co.edu.uniquindio.servify.features.forgotpassword

data class ForgotPasswordUiState(
    val email: String = "",
    val sent: Boolean = false,
    val error: String? = null
)