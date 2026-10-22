package co.edu.uniquindio.servify.domain.model

/** Usuario de la app. La contraseña NO vive aquí (la guarda el AuthRepository). */
data class User(
    val id: String,
    val name: String,
    val lastName: String,
    val email: String,
    val city: String,
    val phone: String = "",
    val role: UserRole = UserRole.USER,
    val points: Int = 0,
    val badges: Set<Badge> = emptySet(),
    val memberSinceMillis: Long = 0L
) {
    val fullName: String get() = "$name $lastName".trim()

    val initials: String
        get() = listOf(name, lastName)
            .mapNotNull { it.trim().firstOrNull()?.uppercaseChar() }
            .joinToString("")

    val level: Level get() = Level.fromPoints(points)

    val isModerator: Boolean get() = role == UserRole.MODERATOR
}
