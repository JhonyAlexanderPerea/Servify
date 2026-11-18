package co.edu.uniquindio.servify.data.model.dto

import androidx.annotation.Keep
import co.edu.uniquindio.servify.domain.model.Notification
import co.edu.uniquindio.servify.domain.model.NotificationType
import com.google.firebase.firestore.PropertyName

/**
 * Data Transfer Object (DTO) para la colección "notifications" en Cloud Firestore.
 */
@Keep
data class NotificationDto(
    val id: String = "",
    val userId: String = "",
    val title: String = "",
    val message: String = "",
    val type: String = NotificationType.MODERATION_UPDATE.name,
    @get:PropertyName("isRead") @field:PropertyName("isRead")
    val isRead: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),
    val targetId: String? = null
) {
    fun toDomain(): Notification {
        val notifType = runCatching { NotificationType.valueOf(type) }
            .getOrDefault(NotificationType.MODERATION_UPDATE)
        return Notification(
            id = id,
            userId = userId,
            title = title,
            message = message,
            type = notifType,
            isRead = isRead,
            createdAt = createdAt,
            targetId = targetId
        )
    }

    fun toDocument(): Map<String, Any?> = mapOf(
        "id" to id,
        "userId" to userId,
        "title" to title,
        "message" to message,
        "type" to type,
        "isRead" to isRead,
        "read" to isRead,
        "createdAt" to createdAt,
        "targetId" to targetId
    )

    companion object {
        fun fromDomain(n: Notification): NotificationDto = NotificationDto(
            id = n.id,
            userId = n.userId,
            title = n.title,
            message = n.message,
            type = n.type.name,
            isRead = n.isRead,
            createdAt = n.createdAt,
            targetId = n.targetId
        )
    }
}
