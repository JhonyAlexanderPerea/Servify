package co.edu.uniquindio.servify.data.repository.firestore

import android.util.Log
import co.edu.uniquindio.servify.data.model.dto.CommentDto
import co.edu.uniquindio.servify.domain.model.Comment
import co.edu.uniquindio.servify.domain.repository.CommentRepository
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementación de la capa de datos para comentarios en Firebase Firestore.
 * Sincroniza las opiniones y valoraciones en tiempo real en la colección "comments".
 */
@Singleton
class CommentFirestoreRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : CommentRepository {

    private val collection = firestore.collection("comments")

    override fun observeCommentsForPublication(publicationId: String): Flow<List<Comment>> = callbackFlow {
        val subscription = collection
            .whereEqualTo("publicationId", publicationId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.w("CommentFirestoreRepo", "Error observando comentarios: ${error.message}")
                    trySend(emptyList())
                    return@addSnapshotListener
                }
                if (snapshot != null) {
                    val list = snapshot.documents.mapNotNull { doc ->
                        try {
                            doc.toObject(CommentDto::class.java)?.copy(id = doc.id)?.toDomain()
                        } catch (_: Exception) {
                            null
                        }
                    }.sortedByDescending { it.createdAt }
                    trySend(list)
                }
            }
        awaitClose { subscription.remove() }
    }

    override fun snapshot(): List<Comment> {
        return runBlocking {
            try {
                val snapshot = collection.get().await()
                snapshot.documents.mapNotNull { doc ->
                    doc.toObject(CommentDto::class.java)?.copy(id = doc.id)?.toDomain()
                }
            } catch (_: Exception) {
                emptyList()
            }
        }
    }

    override suspend fun addComment(comment: Comment) {
        try {
            val docId = comment.id.ifBlank { collection.document().id }
            val commentWithId = comment.copy(id = docId)
            val dto = CommentDto.fromDomain(commentWithId)
            collection.document(docId).set(dto.toDocument()).await()
        } catch (e: Exception) {
            Log.w("CommentFirestoreRepo", "Error al guardar comentario: ${e.message}")
        }
    }
}
