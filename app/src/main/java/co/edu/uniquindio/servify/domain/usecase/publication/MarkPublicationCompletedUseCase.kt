package co.edu.uniquindio.servify.domain.usecase.publication

import co.edu.uniquindio.servify.domain.model.PublicationActionResult
import co.edu.uniquindio.servify.domain.model.PublicationStatus
import co.edu.uniquindio.servify.domain.repository.PublicationRepository
import co.edu.uniquindio.servify.domain.rules.PublicationRules
import co.edu.uniquindio.servify.domain.util.TimeProvider
import javax.inject.Inject

/**
 * El autor marca su publicación como Finalizada. Solo desde VERIFICADA (ver [PublicationRules]).
 * La restricción vive aquí y no en la UI: ocultar un botón nunca es seguridad.
 */
class MarkPublicationCompletedUseCase @Inject constructor(
    private val publicationRepository: PublicationRepository,
    private val timeProvider: TimeProvider
) {
    operator fun invoke(userId: String, publicationId: String): PublicationActionResult {
        val publication = publicationRepository.getById(publicationId)
            ?: return PublicationActionResult.NotFound
        if (publication.authorId != userId) return PublicationActionResult.NotOwner
        if (!PublicationRules.canFinalize(publication.status)) {
            return PublicationActionResult.NotAllowed(publication.status)
        }
        val now = timeProvider.now()
        publicationRepository.update(publicationId) {
            it.copy(status = PublicationStatus.FINALIZADA, finalizedAtMillis = now)
        }
        return PublicationActionResult.Success
    }
}
