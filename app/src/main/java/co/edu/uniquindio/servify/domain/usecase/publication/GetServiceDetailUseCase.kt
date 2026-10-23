package co.edu.uniquindio.servify.domain.usecase.publication

import co.edu.uniquindio.servify.domain.model.Publication
import co.edu.uniquindio.servify.domain.repository.PublicationRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

/**
 * Caso de uso para obtener los detalles de una publicación por su ID.
 */
class GetServiceDetailUseCase @Inject constructor(
    private val publicationRepository: PublicationRepository
) {
    suspend operator fun invoke(serviceId: String): Publication? = withContext(Dispatchers.IO) {
        publicationRepository.getPublicationById(serviceId)
    }
}
