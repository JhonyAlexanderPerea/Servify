package co.edu.uniquindio.servify.domain.usecase.request

import co.edu.uniquindio.servify.domain.model.RequestStatus
import co.edu.uniquindio.servify.domain.repository.PublicationRepository
import co.edu.uniquindio.servify.domain.repository.ServiceRequestRepository
import co.edu.uniquindio.servify.domain.repository.UserRepository
import co.edu.uniquindio.servify.domain.usecase.notification.GenerateNotificationUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import javax.inject.Inject

/**
 * Caso de uso para cambiar el estado de una solicitud/cita.
 * Si el estado cambia a ACCEPTED, se emite automáticamente una notificación [REQUEST_ACCEPTED] para el cliente.
 */
class UpdateRequestStatusUseCase @Inject constructor(
    private val serviceRequestRepository: ServiceRequestRepository,
    private val publicationRepository: PublicationRepository,
    private val userRepository: UserRepository,
    private val generateNotificationUseCase: GenerateNotificationUseCase
) {

    suspend operator fun invoke(
        requestId: String,
        newStatus: RequestStatus
    ) = withContext(Dispatchers.IO) {
        serviceRequestRepository.updateRequestStatus(requestId, newStatus)

        if (newStatus == RequestStatus.ACCEPTED) {
            val requests = serviceRequestRepository.observeRequests().first()
            val request = requests.find { it.id == requestId }
            if (request != null) {
                val publication = publicationRepository.getPublicationById(request.publicationId)
                val provider = userRepository.observeCurrentUser().first()
                val serviceTitle = publication?.title ?: "Servicio contratado"

                generateNotificationUseCase.generateRequestAcceptedNotification(
                    clientId = request.clientId,
                    providerName = provider.name,
                    serviceTitle = serviceTitle,
                    requestId = requestId
                )
            }
        }
    }
}
