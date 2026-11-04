package co.edu.uniquindio.servify.domain.usecase.moderation

import co.edu.uniquindio.servify.domain.model.Publication
import co.edu.uniquindio.servify.domain.repository.PublicationRepository
import co.edu.uniquindio.servify.domain.rules.ModerationRules
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

/** Cola de publicaciones pendientes de revisión, por orden de llegada. */
class ObservePendingPublicationsUseCase @Inject constructor(
    private val publicationRepository: PublicationRepository
) {
    operator fun invoke(): Flow<List<Publication>> =
        publicationRepository.observePublications().map(ModerationRules::pendingQueue)
}
