package co.edu.uniquindio.servify.domain.usecase.request

import co.edu.uniquindio.servify.domain.model.ServiceRequest
import co.edu.uniquindio.servify.domain.repository.PublicationRepository
import co.edu.uniquindio.servify.domain.repository.ServiceRequestRepository
import co.edu.uniquindio.servify.domain.repository.UserRepository
import co.edu.uniquindio.servify.domain.usecase.notification.GenerateNotificationUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import javax.inject.Inject

/**
 * Caso de uso para la creación de solicitudes de servicio/citas.
 * Genera automáticamente una notificación [REQUEST_RECEIVED] para el proveedor.
 */
class CreateServiceRequestUseCase @Inject constructor(
    private val serviceRequestRepository: ServiceRequestRepository,
    private val publicationRepository: PublicationRepository,
    private val userRepository: UserRepository,
    private val generateNotificationUseCase: GenerateNotificationUseCase
) {

    suspend operator fun invoke(
        publicationId: String,
        scheduledDateTime: Long,
        note: String?,
        totalAmount: Double
    ): ServiceRequest = withContext(Dispatchers.IO) {
        val currentUser = userRepository.observeCurrentUser().first()
        val publication = publicationRepository.getPublicationById(publicationId)
            ?: throw IllegalArgumentException("La publicación $publicationId no existe")

        val newRequest = ServiceRequest(
            id = "req_${System.currentTimeMillis()}",
            publicationId = publicationId,
            clientId = currentUser.id,
            providerId = publication.provider.id,
            scheduledDateTime = scheduledDateTime,
            note = note,
            totalAmount = totalAmount
        )

        serviceRequestRepository.createRequest(newRequest)

        // Generar notificación para el proveedor
        generateNotificationUseCase.generateRequestReceivedNotification(
            providerId = publication.provider.id,
            clientName = currentUser.name,
            serviceTitle = publication.title,
            requestId = newRequest.id
        )

        newRequest
    }
}
