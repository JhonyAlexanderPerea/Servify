package co.edu.uniquindio.servify.domain.repository

import co.edu.uniquindio.servify.domain.model.RequestStatus
import co.edu.uniquindio.servify.domain.model.ServiceRequest
import kotlinx.coroutines.flow.Flow

/**
 * Contrato de repositorio para la gestión de solicitudes de servicio y citas.
 */
interface ServiceRequestRepository {
    fun observeRequests(): Flow<List<ServiceRequest>>
    suspend fun createRequest(request: ServiceRequest)
    suspend fun updateRequestStatus(requestId: String, newStatus: RequestStatus)
}
