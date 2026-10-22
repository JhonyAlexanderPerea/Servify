package co.edu.uniquindio.servify.domain.model

/**
 * Modelo de dominio que representa una notificación enviada a un usuario.
 */
data class Notification(
    val id: String,
    val userId: String,
    val title: String,
    val message: String,
    val type: NotificationType,
    val isRead: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),
    val targetId: String? = null
)
