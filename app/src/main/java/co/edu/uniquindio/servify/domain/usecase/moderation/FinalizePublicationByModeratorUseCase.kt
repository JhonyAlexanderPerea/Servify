package co.edu.uniquindio.servify.domain.usecase.moderation

import co.edu.uniquindio.servify.domain.model.ModerationResult
import co.edu.uniquindio.servify.domain.model.PublicationStatus
import co.edu.uniquindio.servify.domain.repository.PublicationRepository
import co.edu.uniquindio.servify.domain.rules.ModerationRules
import co.edu.uniquindio.servify.domain.util.TimeProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

/** El moderador marca cualquier publicación como Resuelta/Finalizada. */
class FinalizePublicationByModeratorUseCase @Inject constructor(
    private val publicationRepository: PublicationRepository,
    private val timeProvider: TimeProvider
) {
    suspend operator fun invoke(publicationId: String): ModerationResult = withContext(Dispatchers.IO) {
        val publication = publicationRepository.getById(publicationId)
            ?: return@withContext ModerationResult.NotFound
        if (!ModerationRules.canFinalize(publication.status)) {
            return@withContext ModerationResult.NotAllowed(publication.status)
        }
        val now = timeProvider.now()
        publicationRepository.update(publicationId) {
            it.copy(status = PublicationStatus.FINALIZADA, finalizedAtMillis = now)
        }
        ModerationResult.Success
    }
}
