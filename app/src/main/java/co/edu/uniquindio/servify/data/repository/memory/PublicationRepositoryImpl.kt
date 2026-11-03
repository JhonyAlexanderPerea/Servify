package co.edu.uniquindio.servify.data.repository.memory

import co.edu.uniquindio.servify.domain.model.Publication
import co.edu.uniquindio.servify.domain.repository.PublicationRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PublicationRepositoryImpl @Inject constructor() : PublicationRepository {

    private val _publications = MutableStateFlow(SampleData.publicationsList)

    override val publications: Flow<List<Publication>> = _publications.asStateFlow()

    override fun observePublications(): Flow<List<Publication>> = publications

    override fun observeByAuthor(authorId: String): Flow<List<Publication>> {
        return _publications.map { list ->
            list.filter { publication ->
                publication.authorId == authorId || publication.provider?.id == authorId
            }
        }
    }

    override fun getById(id: String): Publication? {
        return _publications.value.find { it.id == id }
    }

    override  fun getPublicationById(id: String): Publication? = getById(id)

    override fun snapshot(): List<Publication> = _publications.value

    override fun add(publication: Publication) {
        _publications.update { listOf(publication) + it }
    }

    override suspend fun createPublication(publication: Publication) {
        add(publication)
    }

    override fun update(id: String, transform: (Publication) -> Publication): Publication? {
        var updatedPublication: Publication? = null
        _publications.update { list ->
            list.map { publication ->
                if (publication.id == id) {
                    val transformed = transform(publication)
                    updatedPublication = transformed
                    transformed
                } else {
                    publication
                }
            }
        }
        return updatedPublication
    }

    override suspend fun updatePublication(publication: Publication) {
        update(publication.id) { publication }
    }

    override fun delete(id: String): Boolean {
        val exists = _publications.value.any { it.id == id }
        if (!exists) return false
        _publications.update { list -> list.filterNot { it.id == id } }
        return true
    }

    override suspend fun deletePublication(id: String): Boolean = delete(id)
}
