package co.edu.uniquindio.servify.domain.usecase.notification

import co.edu.uniquindio.servify.domain.model.Notification
import co.edu.uniquindio.servify.domain.model.NotificationType
import co.edu.uniquindio.servify.domain.repository.NotificationRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.UUID
import javax.inject.Inject

/**
 * Caso de uso responsable de generar e insertar notificaciones internas por eventos
 * de negocio de forma hilo-segura en Dispatchers.IO.
 */
class GenerateNotificationUseCase @Inject constructor(
    private val notificationRepository: NotificationRepository
) {

    /**
     * Inserta directamente un objeto [Notification] en el repositorio.
     */
    suspend operator fun invoke(notification: Notification) = withContext(Dispatchers.IO) {
        notificationRepository.addNotification(notification)
    }

    /**
     * Genera la notificación para el proveedor cuando un cliente crea una solicitud de servicio.
     */
    suspend fun generateRequestReceivedNotification(
        providerId: String,
        clientName: String,
        serviceTitle: String,
        requestId: String
    ) = withContext(Dispatchers.IO) {
        val notification = Notification(
            id = "notif_${UUID.randomUUID()}",
            userId = providerId,
            title = "Nueva solicitud de servicio",
            message = "El cliente $clientName ha solicitado tu servicio '$serviceTitle'.",
            type = NotificationType.REQUEST_RECEIVED,
            isRead = false,
            createdAt = System.currentTimeMillis(),
            targetId = requestId
        )
        notificationRepository.addNotification(notification)
    }

    /**
     * Genera la notificación para el cliente cuando el proveedor acepta su solicitud de servicio.
     */
    suspend fun generateRequestAcceptedNotification(
        clientId: String,
        providerName: String,
        serviceTitle: String,
        requestId: String
    ) = withContext(Dispatchers.IO) {
        val notification = Notification(
            id = "notif_${UUID.randomUUID()}",
            userId = clientId,
            title = "¡Solicitud Aceptada!",
            message = "El proveedor $providerName ha aceptado tu solicitud del servicio '$serviceTitle'.",
            type = NotificationType.REQUEST_ACCEPTED,
            isRead = false,
            createdAt = System.currentTimeMillis(),
            targetId = requestId
        )
        notificationRepository.addNotification(notification)
    }

    /**
     * Genera la notificación para el usuario cuando una publicación cambia de estado en moderación.
     */
    suspend fun generateModerationUpdateNotification(
        userId: String,
        serviceTitle: String,
        isApproved: Boolean,
        publicationId: String
    ) = withContext(Dispatchers.IO) {
        val messageText = if (isApproved) {
            "Tu publicación '$serviceTitle' ha sido aprobada y ya está visible en el marketplace."
        } else {
            "Tu publicación '$serviceTitle' requiere cambios o no cumple las políticas."
        }

        val notification = Notification(
            id = "notif_${UUID.randomUUID()}",
            userId = userId,
            title = "Estado de publicación actualizado",
            message = messageText,
            type = NotificationType.MODERATION_UPDATE,
            isRead = false,
            createdAt = System.currentTimeMillis(),
            targetId = publicationId
        )
        notificationRepository.addNotification(notification)
    }

    /**
     * Genera la notificación cuando el usuario desbloquea un nuevo logro o insignia.
     */
    suspend fun generateAchievementUnlockedNotification(
        userId: String,
        badgeName: String,
        badgeId: String
    ) = withContext(Dispatchers.IO) {
        val notification = Notification(
            id = "notif_${UUID.randomUUID()}",
            userId = userId,
            title = "¡Nuevo logro desbloqueado!",
            message = "Has obtenido la insignia '$badgeName'.",
            type = NotificationType.ACHIEVEMENT_UNLOCKED,
            isRead = false,
            createdAt = System.currentTimeMillis(),
            targetId = badgeId
        )
        notificationRepository.addNotification(notification)
    }
}
