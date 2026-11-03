package co.edu.uniquindio.servify.domain.usecase.publication

import co.edu.uniquindio.servify.domain.model.Publication
import co.edu.uniquindio.servify.domain.repository.PublicationRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

/**
 * Caso de uso para actualizar una publicación de servicio existente.
 */
class UpdatePublicationUseCase @Inject constructor(
    private val publicationRepository: PublicationRepository
) {
    suspend operator fun invoke(publication: Publication) = withContext(Dispatchers.IO) {
        publicationRepository.updatePublication(publication)
    }
}
