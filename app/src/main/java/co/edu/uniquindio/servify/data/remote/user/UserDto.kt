package co.edu.uniquindio.servify.data.remote.user

import androidx.annotation.Keep
import co.edu.uniquindio.servify.domain.model.Badge
import co.edu.uniquindio.servify.domain.model.ReputationLevel
import co.edu.uniquindio.servify.domain.model.User
import co.edu.uniquindio.servify.domain.model.UserRole

// Representación de un User tal como se guarda en Firestore.
// Todos los campos tienen valor por defecto para garantizar el constructor vacío que Firestore
// necesita al convertir un documento con toObject(). Los nombres NO deben empezar con "is":
// Firestore mapea mal los booleanos con ese prefijo (por eso "verified" y no "isVerified").
@Keep
data class UserDto(
    val id: String = "",
    val name: String = "",
    val lastName: String = "",
    val email: String = "",
    val phone: String = "",
    val city: String = "",
    val avatarUrl: String = "",
    val role: String = UserRole.USER.name,
    val verified: Boolean = false,
    val trustIndexScore: Int = 0,
    val reputationLevel: String = ReputationLevel.BRONZE.name,
    val points: Int = 0,
    val completedJobsCount: Int = 0,
    val badges: List<String> = emptyList(),
    val memberSinceMillis: Long = 0L
) {
    // Convierte el DTO (datos de Firestore) al modelo de dominio
    fun toDomain(): User = User(
        id = id,
        name = name,
        lastName = lastName,
        email = email,
        phone = phone,
        city = city,
        avatarUrl = avatarUrl.ifBlank { null },
        // Si llega un valor desconocido se usa un valor por defecto en lugar de romper la aplicación
        role = runCatching { UserRole.valueOf(role) }.getOrDefault(UserRole.USER),
        isVerified = verified,
        trustIndexScore = trustIndexScore,
        reputationLevel = runCatching { ReputationLevel.valueOf(reputationLevel) }
            .getOrDefault(ReputationLevel.BRONZE),
        currentPoints = points,
        points = points,
        completedJobsCount = completedJobsCount,
        badges = badges.mapNotNull { name -> runCatching { Badge.valueOf(name) }.getOrNull() }.toSet(),
        memberSinceMillis = memberSinceMillis
    )

    companion object {
        // Convierte el modelo de dominio al DTO que se guardará en Firestore
        fun fromDomain(user: User): UserDto = UserDto(
            id = user.id,
            name = user.name,
            lastName = user.lastName,
            email = user.email,
            phone = user.phone,
            city = user.city,
            avatarUrl = user.avatarUrl.orEmpty(),
            role = user.role.name,
            verified = user.isVerified,
            trustIndexScore = user.trustIndexScore,
            reputationLevel = user.reputationLevel.name,
            points = user.points.takeIf { it > 0 } ?: user.currentPoints,
            completedJobsCount = user.completedJobsCount,
            badges = user.badges.map { it.name },
            memberSinceMillis = user.memberSinceMillis
        )
    }
}
