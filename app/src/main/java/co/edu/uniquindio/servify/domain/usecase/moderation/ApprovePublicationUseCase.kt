package co.edu.uniquindio.servify.domain.usecase.moderation

import co.edu.uniquindio.servify.domain.model.ModerationResult
import co.edu.uniquindio.servify.domain.model.PublicationStatus
import co.edu.uniquindio.servify.domain.repository.PublicationRepository
import co.edu.uniquindio.servify.domain.rules.ModerationRules
import co.edu.uniquindio.servify.domain.rules.PointEvent
import co.edu.uniquindio.servify.domain.usecase.notification.GenerateNotificationUseCase
import co.edu.uniquindio.servify.domain.usecase.reputation.AwardPointsUseCase
import co.edu.uniquindio.servify.domain.util.TimeProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

/**
 * Verifica una publicación pendiente: pasa a VERIFICADA, notifica al autor y le suma los puntos
 * de [PointEvent.PUBLICATION_VERIFIED] (que además reevalúa sus insignias).
 *
 * La restricción de estado vive aquí y no en la UI: ocultar un botón nunca es seguridad.
 */
class ApprovePublicationUseCase @Inject constructor(
    private val publicationRepository: PublicationRepository,
    private val generateNotificationUseCase: GenerateNotificationUseCase,
    private val awardPointsUseCase: AwardPointsUseCase,
    private val timeProvider: TimeProvider
) {
    suspend operator fun invoke(publicationId: String): ModerationResult = withContext(Dispatchers.IO) {
        val publication = publicationRepository.getById(publicationId)
            ?: return@withContext ModerationResult.NotFound
        if (!ModerationRules.canReview(publication.status)) {
            return@withContext ModerationResult.NotAllowed(publication.status)
        }

        val now = timeProvider.now()
        publicationRepository.update(publicationId) {
            it.copy(status = PublicationStatus.VERIFICADA, verifiedAtMillis = now, rejectionReason = null)
        }

        val authorId = publication.authorId.ifBlank { publication.provider.id }
        generateNotificationUseCase.generateModerationUpdateNotification(
            userId = authorId,
            serviceTitle = publication.title,
            isApproved = true,
            publicationId = publicationId
        )
        awardPointsUseCase(authorId, PointEvent.PUBLICATION_VERIFIED)
        ModerationResult.Success
    }
}
