package co.edu.uniquindio.servify.data.repository.memory

import co.edu.uniquindio.servify.domain.model.Publication
import co.edu.uniquindio.servify.domain.repository.PublicationRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PublicationRepositoryImpl @Inject constructor() : PublicationRepository {

    private val _publications = MutableStateFlow(SampleData.publicationsList)

    override fun observePublications(): Flow<List<Publication>> = _publications.asStateFlow()

    override suspend fun getPublicationById(id: String): Publication? {
        return _publications.value.find { it.id == id }
    }

    override suspend fun createPublication(publication: Publication) {
        _publications.update { listOf(publication) + it }
    }

    override suspend fun updatePublication(publication: Publication) {
        _publications.update { list ->
            list.map { if (it.id == publication.id) publication else it }
        }
    }

    override suspend fun deletePublication(id: String) {
        _publications.update { list ->
            list.filterNot { it.id == id }
        }
    }
}
