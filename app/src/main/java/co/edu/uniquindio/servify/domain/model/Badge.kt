package co.edu.uniquindio.servify.domain.model

/**
 * Modelo de dominio que representa una insignia o logro alcanzable en el sistema de gamificación.
 */
data class Badge(
    val id: String,
    val name: String,
    val description: String,
    val iconUrl: String,
    val unlockedAt: Long? = null
)
