package co.edu.uniquindio.servify.data.repository.firestore

import android.util.Log
import co.edu.uniquindio.servify.data.model.dto.NotificationDto
import co.edu.uniquindio.servify.domain.model.Notification
import co.edu.uniquindio.servify.domain.repository.NotificationRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementación de la capa de datos para notificaciones en Firebase Firestore.
 * Sincroniza las notificaciones de los usuarios en tiempo real en la colección "notifications".
 */
@Singleton
class NotificationFirestoreRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
) : NotificationRepository {

    private val collection = firestore.collection("notifications")
    private val currentUserId: String? get() = auth.currentUser?.uid

    override fun observeNotifications(): Flow<List<Notification>> {
        val userId = currentUserId ?: return flowOf(emptyList())
        return callbackFlow {
            val subscription = collection
                .whereEqualTo("userId", userId)
                .addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        Log.w("NotificationFirestoreRepo", "Error observando notificaciones: ${error.message}")
                        trySend(emptyList())
                        return@addSnapshotListener
                    }
                    if (snapshot != null) {
                        val list = snapshot.documents.mapNotNull { doc ->
                            try {
                                doc.toObject(NotificationDto::class.java)?.copy(id = doc.id)?.toDomain()
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

    override suspend fun addNotification(notification: Notification) {
        val docId = notification.id.ifBlank { collection.document().id }
        val notifWithId = notification.copy(id = docId)
        val dto = NotificationDto.fromDomain(notifWithId)
        collection.document(docId).set(dto.toDocument()).await()
    }

    override suspend fun markAsRead(id: String) {
        try {
            val updates = mapOf<String, Any>("isRead" to true, "read" to true)
            collection.document(id).update(updates).await()
        } catch (e: Exception) {
            Log.w("NotificationFirestoreRepo", "Error al marcar notificación como leída $id: ${e.message}")
            runCatching {
                val updates = mapOf<String, Any>("isRead" to true, "read" to true)
                collection.document(id).set(updates, com.google.firebase.firestore.SetOptions.merge()).await()
            }
        }
    }

    override suspend fun markAllAsRead() {
        val userId = currentUserId ?: return
        markAllAsReadForUser(userId)
    }

    override suspend fun markAllAsReadForUser(userId: String) {
        try {
            val snapshot = collection
                .whereEqualTo("userId", userId)
                .get()
                .await()
            if (snapshot.isEmpty) return
            val batch = firestore.batch()
            val updates = mapOf<String, Any>("isRead" to true, "read" to true)
            for (doc in snapshot.documents) {
                batch.update(doc.reference, updates)
            }
            batch.commit().await()
        } catch (e: Exception) {
            Log.w("NotificationFirestoreRepo", "Error en markAllAsReadForUser batch: ${e.message}")
            runCatching {
                val snapshot = collection
                    .whereEqualTo("userId", userId)
                    .get()
                    .await()
                val updates = mapOf<String, Any>("isRead" to true, "read" to true)
                for (doc in snapshot.documents) {
                    runCatching { doc.reference.update(updates).await() }
                }
            }
        }
    }
}
