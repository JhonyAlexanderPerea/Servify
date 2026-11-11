package co.edu.uniquindio.servify.data.repository

import co.edu.uniquindio.servify.domain.model.Publication
import co.edu.uniquindio.servify.domain.repository.PublicationRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update

/**
 * Publicaciones en memoria. Única implementación en memoria: la app la crea con los datos de demostración
 * (ver di/RepositoryModule) y las pruebas con [SeedPublications][co.edu.uniquindio.servify.data.seed.SeedPublications].
 * El mapa es inmutable por snapshot: cada cambio emite una lista nueva. Las nuevas publicaciones van primero.
 */
class InMemoryPublicationRepository(seed: List<Publication> = emptyList()) : PublicationRepository {

    private val state = MutableStateFlow(seed.associateBy { it.id })

    override val publications: Flow<List<Publication>> = state.map { it.values.toList() }

    override fun observeByAuthor(authorId: String): Flow<List<Publication>> =
        publications.map { list ->
            list.filter { it.authorId == authorId || it.provider.id == authorId }
        }

    override fun getById(id: String): Publication? = state.value[id]

    override fun snapshot(): List<Publication> = state.value.values.toList()

    override fun add(publication: Publication) {
        state.update { current -> mapOf(publication.id to publication) + (current - publication.id) }
    }

    override fun update(id: String, transform: (Publication) -> Publication): Publication? {
        var result: Publication? = null
        state.update { current ->
            val existing = current[id] ?: return@update current
            val updated = transform(existing)
            result = updated
            current + (id to updated)
        }
        return result
    }

    override fun delete(id: String): Boolean {
        var removed = false
        state.update { current ->
            if (id !in current) return@update current
            removed = true
            current - id
        }
        return removed
    }
}
