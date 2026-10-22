package co.edu.uniquindio.servify.data.repository.memory

import co.edu.uniquindio.servify.domain.model.RequestStatus
import co.edu.uniquindio.servify.domain.model.ServiceRequest
import co.edu.uniquindio.servify.domain.repository.ServiceRequestRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ServiceRequestRepositoryImpl @Inject constructor() : ServiceRequestRepository {

    private val _requests = MutableStateFlow(SampleData.requestsList)

    override fun observeRequests(): Flow<List<ServiceRequest>> = _requests.asStateFlow()

    override suspend fun createRequest(request: ServiceRequest) {
        _requests.update { listOf(request) + it }
    }

    override suspend fun updateRequestStatus(requestId: String, newStatus: RequestStatus) {
        _requests.update { list ->
            list.map { if (it.id == requestId) it.copy(status = newStatus) else it }
        }
    }
}
