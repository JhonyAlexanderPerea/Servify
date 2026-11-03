package co.edu.uniquindio.servify.domain.usecase.publication

import co.edu.uniquindio.servify.domain.model.PublicationStatus
import co.edu.uniquindio.servify.domain.repository.PublicationRepository
import co.edu.uniquindio.servify.domain.usecase.notification.GenerateNotificationUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

/**
 * Caso de uso para moderar y aprobar o rechazar publicaciones de servicios.
 * Emite una notificación [MODERATION_UPDATE] al usuario que publicó.
 */
class ModeratePublicationUseCase @Inject constructor(
    private val publicationRepository: PublicationRepository,
    private val generateNotificationUseCase: GenerateNotificationUseCase
) {

    suspend operator fun invoke(
        publicationId: String,
        newStatus: PublicationStatus
    ) = withContext(Dispatchers.IO) {
        val publication = publicationRepository.getPublicationById(publicationId)
            ?: return@withContext

        val updatedPublication = publication.copy(status = newStatus)
        publicationRepository.updatePublication(updatedPublication)

        val isApproved = (newStatus == PublicationStatus.APPROVED)
        generateNotificationUseCase.generateModerationUpdateNotification(
            userId = publication.provider.id,
            serviceTitle = publication.title,
            isApproved = isApproved,
            publicationId = publicationId
        )
    }
}
