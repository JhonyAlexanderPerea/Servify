package co.edu.uniquindio.servify.features.forgotpassword

import co.edu.uniquindio.servify.domain.model.UserRole

data class ForgotPasswordUiState(
    /** Flujo desde el que se abrió la pantalla: define el texto y qué cuentas pueden recuperarse. */
    val role: UserRole = UserRole.USER,
    val email: String = "",
    val isLoading: Boolean = false,
    val sent: Boolean = false,
    /** true cuando el usuario pidió reenviar el enlace estando en la pantalla de "Correo enviado". */
    val resent: Boolean = false,
    val error: String? = null
) {
    val canSend: Boolean get() = email.isNotBlank() && error == null && !isLoading
}
