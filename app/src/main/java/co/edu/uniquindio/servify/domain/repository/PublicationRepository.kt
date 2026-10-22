package co.edu.uniquindio.servify.domain.repository

import co.edu.uniquindio.servify.domain.model.Publication
import kotlinx.coroutines.flow.Flow

/**
 * Contrato de repositorio para la gestión reactiva de ofertas y publicaciones de servicios.
 */
interface PublicationRepository {
    fun observePublications(): Flow<List<Publication>>
    suspend fun getPublicationById(id: String): Publication?
    suspend fun createPublication(publication: Publication)
    suspend fun updatePublication(publication: Publication)
    suspend fun deletePublication(id: String)
}
