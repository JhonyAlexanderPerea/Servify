package co.edu.uniquindio.servify.domain.usecase.publication

import co.edu.uniquindio.servify.domain.model.PublicationActionResult
import co.edu.uniquindio.servify.domain.repository.PublicationRepository
import co.edu.uniquindio.servify.domain.rules.PublicationRules
import javax.inject.Inject

/** El autor elimina una publicación suya. Nadie más puede eliminarla. */
class DeletePublicationUseCase @Inject constructor(
    private val publicationRepository: PublicationRepository
) {
    operator fun invoke(userId: String, publicationId: String): PublicationActionResult {
        val publication = publicationRepository.getById(publicationId)
            ?: return PublicationActionResult.NotFound
        if (publication.authorId != userId) return PublicationActionResult.NotOwner
        if (!PublicationRules.canDelete(publication.status)) {
            return PublicationActionResult.NotAllowed(publication.status)
        }
        publicationRepository.delete(publicationId)
        return PublicationActionResult.Success
    }
}
