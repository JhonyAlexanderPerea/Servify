package co.edu.uniquindio.servify.data.repository.firestore

import co.edu.uniquindio.servify.data.model.dto.PublicationDto
import co.edu.uniquindio.servify.domain.model.Publication
import co.edu.uniquindio.servify.domain.model.PublicationStatus
import co.edu.uniquindio.servify.domain.repository.PublicationRepository
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementación de la capa de datos para publicaciones utilizando Firebase Firestore.
 */
@Singleton
class PublicationFirestoreRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : PublicationRepository {

    private val collection = firestore.collection("publications")

    override val publications: Flow<List<Publication>> = observePublications()

    /**
     * Escuchador en tiempo real (`addSnapshotListener`) para las publicaciones con status APPROVED.
     * Ordena cronológicamente por `createdAt` descendente.
     */
    override fun observePublications(): Flow<List<Publication>> = callbackFlow {
        val subscription = collection
            .whereEqualTo("status", PublicationStatus.APPROVED.name)
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                if (snapshot != null) {
                    val list = snapshot.documents.mapNotNull { doc ->
                        try {
                            doc.toObject(PublicationDto::class.java)?.copy(id = doc.id)?.toDomain()
                        } catch (_: Exception) {
                            null
                        }
                    }
                    trySend(list)
                }
            }
        awaitClose { subscription.remove() }
    }

    override fun observeByAuthor(authorId: String): Flow<List<Publication>> = callbackFlow {
        val subscription = collection
            .whereEqualTo("authorId", authorId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                if (snapshot != null) {
                    val list = snapshot.documents.mapNotNull { doc ->
                        try {
                            doc.toObject(PublicationDto::class.java)?.copy(id = doc.id)?.toDomain()
                        } catch (_: Exception) {
                            null
                        }
                    }.sortedByDescending { it.createdAt }
                    trySend(list)
                }
            }
        awaitClose { subscription.remove() }
    }

    /**
     * Búsqueda y filtrado dinámico sobre Firestore.
     */
    fun searchAndFilterPublications(
        categoryId: String? = null,
        query: String? = null
    ): Flow<List<Publication>> = callbackFlow {
        var baseQuery: Query = collection.whereEqualTo("status", PublicationStatus.APPROVED.name)

        if (!categoryId.isNullOrBlank()) {
            baseQuery = baseQuery.whereEqualTo("categoryId", categoryId)
        }

        val subscription = baseQuery.addSnapshotListener { snapshot, error ->
            if (error != null) {
                close(error)
                return@addSnapshotListener
            }
            if (snapshot != null) {
                var list = snapshot.documents.mapNotNull { doc ->
                    try {
                        doc.toObject(PublicationDto::class.java)?.copy(id = doc.id)?.toDomain()
                    } catch (_: Exception) {
                        null
                    }
                }

                if (!query.isNullOrBlank()) {
                    val q = query.trim().lowercase()
                    list = list.filter { pub ->
                        pub.title.lowercase().contains(q) ||
                            pub.description.lowercase().contains(q) ||
                            pub.category.lowercase().contains(q) ||
                            pub.subcategory.lowercase().contains(q)
                    }
                }

                list = list.sortedByDescending { it.createdAt }
                trySend(list)
            }
        }
        awaitClose { subscription.remove() }
    }

    override suspend fun getById(id: String): Publication? {
        return getPublicationById(id)
    }

    /**
     * Obtiene una publicación por su ID desde Firestore usando suspend/await().
     */
    override suspend fun getPublicationById(id: String): Publication? {
        return try {
            val doc = collection.document(id).get().await()
            if (doc.exists()) {
                doc.toObject(PublicationDto::class.java)?.copy(id = doc.id)?.toDomain()
            } else {
                null
            }
        } catch (_: Exception) {
            null
        }
    }

    override fun snapshot(): List<Publication> {
        return runBlocking {
            try {
                val snapshot = collection.get().await()
                snapshot.documents.mapNotNull { doc ->
                    doc.toObject(PublicationDto::class.java)?.copy(id = doc.id)?.toDomain()
                }
            } catch (_: Exception) {
                emptyList()
            }
        }
    }

    /**
     * Crea una nueva publicación en Firestore con ID autogenerado si es necesario.
     */
    override suspend fun createPublication(publication: Publication) {
        add(publication)
    }

    override suspend fun add(publication: Publication) {
        val docId = publication.id.ifBlank { collection.document().id }
        val pubWithId = publication.copy(id = docId)
        val dto = PublicationDto.fromDomain(pubWithId)
        collection.document(docId).set(dto.toDocument()).await()
    }

    /**
     * Actualiza una publicación existente en Firestore utilizando `SetOptions.merge()`.
     */
    override suspend fun updatePublication(publication: Publication) {
        val dto = PublicationDto.fromDomain(publication)
        collection.document(publication.id).set(dto.toDocument(), SetOptions.merge()).await()
    }

    override suspend fun update(id: String, transform: (Publication) -> Publication): Publication? {
        val current = getPublicationById(id) ?: return null
        val updated = transform(current)
        updatePublication(updated)
        return updated
    }

    override suspend fun deletePublication(id: String): Boolean {
        return delete(id)
    }

    override suspend fun delete(id: String): Boolean {
        return try {
            collection.document(id).delete().await()
            true
        } catch (_: Exception) {
            false
        }
    }
}
