package co.edu.uniquindio.servify.domain.usecase.publication

import co.edu.uniquindio.servify.domain.model.Notification
import co.edu.uniquindio.servify.domain.model.NotificationType
import co.edu.uniquindio.servify.domain.model.Publication
import co.edu.uniquindio.servify.domain.model.PublicationStatus
import co.edu.uniquindio.servify.domain.repository.PublicationRepository
import co.edu.uniquindio.servify.domain.usecase.notification.GenerateNotificationUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.UUID
import javax.inject.Inject

/**
 * Caso de uso para crear y guardar una nueva publicación en el repositorio.
 * Genera automáticamente la notificación correspondiente para el usuario.
 */
class CreatePublicationUseCase @Inject constructor(
    private val publicationRepository: PublicationRepository,
    private val generateNotificationUseCase: GenerateNotificationUseCase
) {
    suspend operator fun invoke(publication: Publication) = withContext(Dispatchers.IO) {
        publicationRepository.createPublication(publication)

        val authorId = publication.authorId.ifBlank { publication.provider.id }
        val alreadyApproved = publication.status == PublicationStatus.APPROVED ||
            publication.status == PublicationStatus.VERIFICADA

        if (alreadyApproved) {
            generateNotificationUseCase.generateModerationUpdateNotification(
                userId = authorId,
                serviceTitle = publication.title,
                isApproved = true,
                publicationId = publication.id
            )
        } else {
            generateNotificationUseCase(
                Notification(
                    id = "notif_${UUID.randomUUID()}",
                    userId = authorId,
                    title = "Publicación enviada a revisión",
                    message = "Tu servicio '${publication.title}' fue registrado con éxito y se encuentra en revisión por un moderador.",
                    type = NotificationType.MODERATION_UPDATE,
                    isRead = false,
                    createdAt = System.currentTimeMillis(),
                    targetId = publication.id
                )
            )
        }
    }
}
