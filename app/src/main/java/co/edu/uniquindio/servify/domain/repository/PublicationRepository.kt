package co.edu.uniquindio.servify.domain.repository

import co.edu.uniquindio.servify.domain.model.Publication
import kotlinx.coroutines.flow.Flow

interface PublicationRepository {
    val publications: Flow<List<Publication>>

    fun observePublications(): Flow<List<Publication>> = publications

    fun observeByAuthor(authorId: String): Flow<List<Publication>>

    suspend fun getPublicationById(id: String): Publication? = getById(id)

    suspend fun getById(id: String): Publication?

    fun snapshot(): List<Publication>

    suspend fun createPublication(publication: Publication) {
        add(publication)
    }

    suspend fun add(publication: Publication)

    suspend fun updatePublication(publication: Publication) {
        update(publication.id) { publication }
    }

    suspend fun update(id: String, transform: (Publication) -> Publication): Publication?

    /**
     * Da o retira "Me interesa" para [userId] y devuelve el nuevo estado.
     * Las implementaciones remotas deben actualizar solo los campos de interés.
     */
    suspend fun toggleInterest(publicationId: String, userId: String): Boolean? {
        val publication = getById(publicationId) ?: return null
        val updatedIds = if (userId in publication.interestedUserIds) {
            publication.interestedUserIds - userId
        } else {
            publication.interestedUserIds + userId
        }
        val nowInterested = userId in updatedIds
        updatePublication(
            publication.copy(
                interestedUserIds = updatedIds,
                interestedCount = updatedIds.size,
                isFavorite = nowInterested
            )
        )
        return nowInterested
    }

    suspend fun deletePublication(id: String): Boolean = delete(id)

    suspend fun delete(id: String): Boolean
}
