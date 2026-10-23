package co.edu.uniquindio.servify.domain.usecase.publication

import co.edu.uniquindio.servify.domain.model.Publication
import co.edu.uniquindio.servify.domain.model.PublicationStatus
import co.edu.uniquindio.servify.domain.repository.PublicationRepository
import co.edu.uniquindio.servify.domain.usecase.notification.GenerateNotificationUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

/**
 * Caso de uso para crear y guardar una nueva publicación en el repositorio en memoria.
 * Genera automáticamente la notificación de moderación para el usuario.
 */
class CreatePublicationUseCase @Inject constructor(
    private val publicationRepository: PublicationRepository,
    private val generateNotificationUseCase: GenerateNotificationUseCase
) {
    suspend operator fun invoke(publication: Publication) = withContext(Dispatchers.IO) {
        publicationRepository.createPublication(publication)

        // Simular notificación de moderación/aprobación
        val isApproved = (publication.status == PublicationStatus.APPROVED)
        generateNotificationUseCase.generateModerationUpdateNotification(
            userId = publication.provider.id,
            serviceTitle = publication.title,
            isApproved = isApproved,
            publicationId = publication.id
        )
    }
}
