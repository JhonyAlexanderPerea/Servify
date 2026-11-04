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

        // Una publicación nueva nace PENDIENTE: el moderador notificará al decidir (aprobar/rechazar).
        // Solo se notifica aquí si por algún flujo llega ya verificada.
        val alreadyApproved = publication.status == PublicationStatus.APPROVED ||
            publication.status == PublicationStatus.VERIFICADA
        if (alreadyApproved) {
            generateNotificationUseCase.generateModerationUpdateNotification(
                userId = publication.provider.id,
                serviceTitle = publication.title,
                isApproved = true,
                publicationId = publication.id
            )
        }
    }
}
