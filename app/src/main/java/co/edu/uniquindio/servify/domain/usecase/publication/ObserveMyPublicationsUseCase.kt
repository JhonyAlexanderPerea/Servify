package co.edu.uniquindio.servify.domain.usecase.publication

import co.edu.uniquindio.servify.domain.model.Publication
import co.edu.uniquindio.servify.domain.repository.PublicationRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

/** Observa las publicaciones del autor, las más recientes primero. */
class ObserveMyPublicationsUseCase @Inject constructor(
    private val publicationRepository: PublicationRepository
) {
    operator fun invoke(authorId: String): Flow<List<Publication>> =
        publicationRepository.observeByAuthor(authorId)
            .map { list -> list.sortedByDescending { it.createdAtMillis } }
}
