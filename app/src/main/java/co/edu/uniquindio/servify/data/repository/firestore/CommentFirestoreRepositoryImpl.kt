package co.edu.uniquindio.servify.data.repository.firestore

import android.util.Log
import co.edu.uniquindio.servify.data.model.dto.CommentDto
import co.edu.uniquindio.servify.domain.model.Comment
import co.edu.uniquindio.servify.domain.repository.CommentRepository
import co.edu.uniquindio.servify.domain.usecase.comment.ReviewRatingCalculator
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
        val publicationRef = firestore.collection("publications").document(comment.publicationId)
        val commentRef = collection.document(comment.id.ifBlank { collection.document().id })
        val dto = CommentDto.fromDomain(comment.copy(id = commentRef.id))

        // El comentario y el resumen de valoraciones se escriben en una sola transacción.
        // No hay try-catch: cualquier fallo (sin conexión, permisos, publicación inexistente)
        // se propaga al caso de uso y desde ahí a la UI.
        firestore.runTransaction { transaction ->
            // Firestore exige que TODAS las lecturas vayan antes de cualquier escritura.
            val pubSnapshot = transaction.get(publicationRef)
            if (!pubSnapshot.exists()) throw IllegalStateException("Publication not found")

            transaction.set(commentRef, dto.toDocument())

            // Una consulta sin calificación no altera el promedio de la publicación.
            if (comment.rating in 1..5) {
                val currentCount = (pubSnapshot.getLong("reviewsCount") ?: 0L).toInt()
                val currentAverage = pubSnapshot.getDouble("ratingAverage") ?: 0.0
                val totals = ReviewRatingCalculator.calculate(
                    currentAverage = currentAverage,
                    currentCount = currentCount,
                    newRating = comment.rating
                )
                transaction.update(
                    publicationRef,
                    mapOf(
                        "reviewsCount" to totals.reviewsCount,
                        "ratingAverage" to totals.ratingAverage
                    )
                )
            }
            // runTransaction necesita un valor de retorno; el resultado no se usa.
            null
        }.await()
    }
}
