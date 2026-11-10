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

    suspend fun deletePublication(id: String): Boolean = delete(id)

    suspend fun delete(id: String): Boolean
}
