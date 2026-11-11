package co.edu.uniquindio.servify.domain.model

data class UserSession(
    val userId: String,
    val role: UserRole
)
