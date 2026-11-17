package co.edu.uniquindio.servify.data.repository.firestore

import android.util.Log
import co.edu.uniquindio.servify.data.model.dto.ServiceRequestDto
import co.edu.uniquindio.servify.domain.model.RequestStatus
import co.edu.uniquindio.servify.domain.model.ServiceRequest
import co.edu.uniquindio.servify.domain.repository.ServiceRequestRepository
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementación de la capa de datos para solicitudes de servicio/citas en Firebase Firestore.
 * Maneja la sincronización en tiempo real entre cliente y proveedor y registra la trazabilidad
 * de estados en la subcolección `status_history`.
 */
@Singleton
class ServiceRequestFirestoreRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : ServiceRequestRepository {

    private val collection = firestore.collection("service_requests")

    /**
     * Observa todas las solicitudes de servicio ordenadas cronológicamente por `createdAt` descendente.
     */
    override fun observeRequests(): Flow<List<ServiceRequest>> = callbackFlow {
        val subscription = collection
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.w("ServiceRequestFirestoreRepo", "Error observando solicitudes: ${error.message}")
                    trySend(emptyList())
                    return@addSnapshotListener
                }
                if (snapshot != null) {
                    val list = snapshot.documents.mapNotNull { doc ->
                        try {
                            doc.toObject(ServiceRequestDto::class.java)?.copy(id = doc.id)?.toDomain()
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
     * Observabilidad en tiempo real por rol:
     * - `isProvider == false`: Solicitudes enviadas por el cliente (`clientId == userId`).
     * - `isProvider == true`: Solicitudes recibidas por el proveedor (`providerId == userId`).
     */
    override fun observeRequestsForUser(
        userId: String,
        isProvider: Boolean
    ): Flow<List<ServiceRequest>> = callbackFlow {
        val field = if (isProvider) "providerId" else "clientId"
        val subscription = collection
            .whereEqualTo(field, userId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.w("ServiceRequestFirestoreRepo", "Error observando solicitudes por usuario: ${error.message}")
                    trySend(emptyList())
                    return@addSnapshotListener
                }
                if (snapshot != null) {
                    val list = snapshot.documents.mapNotNull { doc ->
                        try {
                            doc.toObject(ServiceRequestDto::class.java)?.copy(id = doc.id)?.toDomain()
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
     * Filtrado en tiempo real de solicitudes por estado (ej: PENDING/ACCEPTED vs COMPLETED/CANCELLED/REJECTED).
     */
    override fun observeRequestsByStatus(
        userId: String,
        statuses: List<RequestStatus>
    ): Flow<List<ServiceRequest>> = callbackFlow {
        val statusNames = statuses.map { it.name }
        if (statusNames.isEmpty()) {
            trySend(emptyList())
            return@callbackFlow
        }

        val subscription = collection
            .whereEqualTo("clientId", userId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.w("ServiceRequestFirestoreRepo", "Error observando solicitudes por estado: ${error.message}")
                    trySend(emptyList())
                    return@addSnapshotListener
                }
                if (snapshot != null) {
                    val list = snapshot.documents.mapNotNull { doc ->
                        try {
                            doc.toObject(ServiceRequestDto::class.java)?.copy(id = doc.id)?.toDomain()
                        } catch (_: Exception) {
                            null
                        }
                    }.filter { req ->
                        req.status.name in statusNames
                    }.sortedByDescending { it.createdAt }
                    trySend(list)
                }
            }
        awaitClose { subscription.remove() }
    }

    /**
     * Obtiene una solicitud de servicio por su ID desde Firestore.
     */
    override suspend fun getById(id: String): ServiceRequest? {
        return try {
            val doc = collection.document(id).get().await()
            if (doc.exists()) {
                doc.toObject(ServiceRequestDto::class.java)?.copy(id = doc.id)?.toDomain()
            } else {
                null
            }
        } catch (_: Exception) {
            null
        }
    }

    /**
     * Crea una nueva solicitud de servicio en Firestore con estado inicial PENDING.
     * Si ya existe un documento activo para la misma fecha y hora del servicio, se rechaza la creación.
     */
    override suspend fun createRequest(request: ServiceRequest) {
        val docId = request.id.ifBlank { collection.document().id }
        val docRef = collection.document(docId)

        val snapshot = docRef.get().await()
        if (snapshot.exists()) {
            val existingStatus = snapshot.getString("status")
            if (existingStatus != null && existingStatus != "CANCELLED" && existingStatus != "REJECTED") {
                throw IllegalStateException("Este servicio ya cuenta con una reserva activa para la fecha y hora seleccionadas.")
            }
        }

        val reqWithId = request.copy(id = docId, status = RequestStatus.PENDING)
        val dto = ServiceRequestDto.fromDomain(reqWithId)
        docRef.set(dto.toDocument()).await()
    }

    override suspend fun updateRequestStatus(requestId: String, newStatus: RequestStatus) {
        updateRequestStatus(
            requestId = requestId,
            newStatus = newStatus,
            changedByUserId = null,
            reason = null
        )
    }

    /**
     * Actualización transaccional de estado: actualiza el estado principal y registra
     * la entrada correspondiente en la subcolección `status_history`.
     */
    override suspend fun updateRequestStatus(
        requestId: String,
        newStatus: RequestStatus,
        changedByUserId: String?,
        reason: String?
    ) {
        try {
            val requestRef = collection.document(requestId)
            val historyRef = requestRef.collection("status_history").document()
            val now = System.currentTimeMillis()

            firestore.runTransaction { transaction ->
                transaction.update(
                    requestRef,
                    mapOf(
                        "status" to newStatus.name,
                        "updatedAt" to now
                    )
                )
                val historyData = mapOf(
                    "id" to historyRef.id,
                    "status" to newStatus.name,
                    "changedByUserId" to (changedByUserId ?: ""),
                    "timestamp" to now,
                    "reason" to reason
                )
                transaction.set(historyRef, historyData)
            }.await()
        } catch (e: Exception) {
            Log.w("ServiceRequestFirestoreRepo", "Error al actualizar estado de solicitud: ${e.message}")
        }
    }
}
