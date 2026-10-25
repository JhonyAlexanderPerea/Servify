package co.edu.uniquindio.servify.domain.model

/**
 * Modelo de dominio que representa a un usuario en Servify (Cliente, Proveedor o Moderador).
 */
data class User(
    val id: String,
    val name: String,
    val lastName: String = "",
    val email: String,
    val phone: String = "",
    val city: String = "",
    val avatarUrl: String? = null,
    val role: UserRole = UserRole.USER,
    val isVerified: Boolean = false,
    val trustIndexScore: Int = 0,
    val reputationLevel: ReputationLevel = ReputationLevel.BRONZE,
    val currentPoints: Int = 0,
    val points: Int = 0,
    val completedJobsCount: Int = 0,
    val badges: Set<Badge> = emptySet(),
    val memberSinceMillis: Long = 0L
) {
    val fullName: String get() = if (lastName.isNotBlank()) "$name $lastName".trim() else name.trim()

    val initials: String
        get() = listOf(name, lastName)
            .filter { it.isNotBlank() }
            .mapNotNull { it.trim().firstOrNull()?.uppercaseChar() }
            .joinToString("")

    val level: Level get() = Level.fromPoints(points.takeIf { it > 0 } ?: currentPoints)

    val isModerator: Boolean get() = role == UserRole.MODERATOR
}
