package co.edu.uniquindio.servify.data.repository.firestore

import android.util.Log
import co.edu.uniquindio.servify.data.model.dto.PublicationDto
import co.edu.uniquindio.servify.data.remote.user.authStateFlow
import co.edu.uniquindio.servify.domain.model.Publication
import co.edu.uniquindio.servify.domain.model.PublicationStatus
import co.edu.uniquindio.servify.domain.repository.PublicationRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementación de la capa de datos para publicaciones utilizando Firebase Firestore.
 */
@Singleton
class PublicationFirestoreRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
) : PublicationRepository {

    private val collection = firestore.collection("publications")
    private val currentUserId: String? get() = auth.currentUser?.uid

    override val publications: Flow<List<Publication>> = observePublications()

    /**
     * Escuchador en tiempo real (`addSnapshotListener`) para las publicaciones.
     * Reacciona tanto a cambios en Firestore como al cambio de usuario autenticado
     * para reevaluar correctamente `isFavorite` de cada publicación.
     */
    @OptIn(ExperimentalCoroutinesApi::class)
    override fun observePublications(): Flow<List<Publication>> =
        auth.authStateFlow().flatMapLatest { firebaseUser ->
            val uid = firebaseUser?.uid
            observePublicationsInternal(uid)
        }

    private fun observePublicationsInternal(userId: String?): Flow<List<Publication>> = callbackFlow {
        val subscription = collection
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.w("PublicationFirestoreRepo", "Error observando publicaciones: ${error.message}")
                    trySend(emptyList())
                    return@addSnapshotListener
                }
                if (snapshot != null) {
                    val list = snapshot.documents.mapNotNull { doc ->
                        try {
                            doc.toObject(PublicationDto::class.java)?.copy(id = doc.id)?.toDomain(userId)
                        } catch (_: Exception) {
                            null
                        }
                    }.sortedByDescending { it.createdAt }
                    trySend(list)
                }
            }
        awaitClose { subscription.remove() }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun observeByAuthor(authorId: String): Flow<List<Publication>> =
        auth.authStateFlow().flatMapLatest { firebaseUser ->
            val uid = firebaseUser?.uid
            callbackFlow {
                val subscription = collection
                    .whereEqualTo("authorId", authorId)
                    .addSnapshotListener { snapshot, error ->
                        if (error != null) {
                            Log.w("PublicationFirestoreRepo", "Error observando publicaciones por autor: ${error.message}")
                            trySend(emptyList())
                            return@addSnapshotListener
                        }
                        if (snapshot != null) {
                            val list = snapshot.documents.mapNotNull { doc ->
                                try {
                                    doc.toObject(PublicationDto::class.java)?.copy(id = doc.id)?.toDomain(uid)
                                } catch (_: Exception) {
                                    null
                                }
                            }.sortedByDescending { it.createdAt }
                            trySend(list)
                        }
                    }
                awaitClose { subscription.remove() }
            }
        }

    /**
     * Búsqueda y filtrado dinámico sobre Firestore.
     */
    fun searchAndFilterPublications(
        categoryId: String? = null,
        query: String? = null
    ): Flow<List<Publication>> = callbackFlow {
        val subscription = collection.addSnapshotListener { snapshot, error ->
            if (error != null) {
                Log.w("PublicationFirestoreRepo", "Error filtrando publicaciones: ${error.message}")
                trySend(emptyList())
                return@addSnapshotListener
            }
            if (snapshot != null) {
                var list = snapshot.documents.mapNotNull { doc ->
                    try {
                        doc.toObject(PublicationDto::class.java)?.copy(id = doc.id)?.toDomain(currentUserId)
                    } catch (_: Exception) {
                        null
                    }
                }.filter { pub ->
                    pub.status == PublicationStatus.APPROVED
                }

                if (!categoryId.isNullOrBlank()) {
                    list = list.filter { it.categoryId == categoryId }
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
                doc.toObject(PublicationDto::class.java)?.copy(id = doc.id)?.toDomain(currentUserId)
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
                    doc.toObject(PublicationDto::class.java)?.copy(id = doc.id)?.toDomain(currentUserId)
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
        try {
            val docId = publication.id.ifBlank { collection.document().id }
            val pubWithId = publication.copy(id = docId)
            val dto = PublicationDto.fromDomain(pubWithId)
            collection.document(docId).set(dto.toDocument()).await()
        } catch (e: Exception) {
            Log.w("PublicationFirestoreRepo", "Error al crear publicación: ${e.message}")
        }
    }

    /**
     * Actualiza una publicación existente en Firestore utilizando `SetOptions.merge()`.
     */
    override suspend fun updatePublication(publication: Publication) {
        try {
            val dto = PublicationDto.fromDomain(publication)
            collection.document(publication.id).set(dto.toDocument(), SetOptions.merge()).await()
        } catch (e: Exception) {
            Log.w("PublicationFirestoreRepo", "Error al actualizar publicación: ${e.message}")
        }
    }

    override suspend fun update(id: String, transform: (Publication) -> Publication): Publication? {
        val current = getPublicationById(id) ?: return null
        val updated = transform(current)
        updatePublication(updated)
        return updated
    }

    override suspend fun toggleInterest(publicationId: String, userId: String): Boolean? {
        val reference = collection.document(publicationId)
        return try {
            firestore.runTransaction { transaction ->
                val snapshot = transaction.get(reference)
                if (!snapshot.exists()) return@runTransaction null
                val ids = (snapshot.get("interestedUserIds") as? List<*>)
                    ?.filterIsInstance<String>()
                    ?.toMutableSet()
                    ?: mutableSetOf()
                val nowInterested = if (userId in ids) {
                    ids.remove(userId)
                    false
                } else {
                    ids.add(userId)
                    true
                }
                transaction.update(
                    reference,
                    mapOf(
                        "interestedUserIds" to ids.toList(),
                        "interestedCount" to ids.size
                    )
                )
                nowInterested
            }.await()
        } catch (e: Exception) {
            Log.w("PublicationFirestoreRepo", "Error al alternar interés: ${e.message}")
            null
        }
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
