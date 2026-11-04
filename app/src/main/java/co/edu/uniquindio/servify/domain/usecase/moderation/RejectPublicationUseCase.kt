package co.edu.uniquindio.servify.domain.usecase.moderation

import co.edu.uniquindio.servify.domain.model.ModerationResult
import co.edu.uniquindio.servify.domain.model.PublicationStatus
import co.edu.uniquindio.servify.domain.repository.PublicationRepository
import co.edu.uniquindio.servify.domain.rules.ModerationRules
import co.edu.uniquindio.servify.domain.usecase.notification.GenerateNotificationUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

/**
 * Rechaza una publicación pendiente. El motivo escrito es obligatorio: la publicación queda
 * RECHAZADA (se oculta del feed público) y el autor recibe una notificación con el motivo.
 */
class RejectPublicationUseCase @Inject constructor(
    private val publicationRepository: PublicationRepository,
    private val generateNotificationUseCase: GenerateNotificationUseCase
) {
    suspend operator fun invoke(publicationId: String, reason: String): ModerationResult =
        withContext(Dispatchers.IO) {
            if (!ModerationRules.isValidRejectionReason(reason)) {
                return@withContext ModerationResult.ReasonRequired
            }
            val publication = publicationRepository.getById(publicationId)
                ?: return@withContext ModerationResult.NotFound
            if (!ModerationRules.canReview(publication.status)) {
                return@withContext ModerationResult.NotAllowed(publication.status)
            }

            val cleanReason = reason.trim()
            publicationRepository.update(publicationId) {
                it.copy(status = PublicationStatus.RECHAZADA, rejectionReason = cleanReason)
            }

            generateNotificationUseCase.generateRejectionNotification(
                userId = publication.authorId.ifBlank { publication.provider.id },
                serviceTitle = publication.title,
                reason = cleanReason,
                publicationId = publicationId
            )
            ModerationResult.Success
        }
}
