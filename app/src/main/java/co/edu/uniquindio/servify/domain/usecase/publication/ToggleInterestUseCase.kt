package co.edu.uniquindio.servify.domain.usecase.publication

import co.edu.uniquindio.servify.domain.repository.PublicationRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

/**
 * Caso de uso para alternar el estado de "Me interesa" (favorito/guardar) en un servicio.
 */
class ToggleInterestUseCase @Inject constructor(
    private val publicationRepository: PublicationRepository
) {
    suspend operator fun invoke(publicationId: String) = withContext(Dispatchers.IO) {
        val publication = publicationRepository.getPublicationById(publicationId)
            ?: return@withContext

        val newFavState = !publication.isFavorite
        val diff = if (newFavState) 1 else -1
        val updated = publication.copy(
            isFavorite = newFavState,
            interestedCount = (publication.interestedCount + diff).coerceAtLeast(0)
        )

        publicationRepository.updatePublication(updated)
    }
}
