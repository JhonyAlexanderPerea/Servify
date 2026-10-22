package co.edu.uniquindio.servify.domain.model

/**
 * Modelo de dominio que representa a un usuario en Servify (Cliente, Proveedor o Moderador).
 */
data class User(
    val id: String,
    val name: String,
    val email: String,
    val phone: String,
    val avatarUrl: String? = null,
    val role: UserRole = UserRole.CLIENT,
    val isVerified: Boolean = false,
    val trustIndexScore: Int = 0,
    val reputationLevel: ReputationLevel = ReputationLevel.BRONZE,
    val currentPoints: Int = 0,
    val completedJobsCount: Int = 0
)
