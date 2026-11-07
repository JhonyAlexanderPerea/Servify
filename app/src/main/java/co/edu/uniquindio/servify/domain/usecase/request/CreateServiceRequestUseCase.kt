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

/** Se lanza cuando el usuario intenta solicitar un servicio que él mismo publicó. */
class CannotRequestOwnServiceException :
    IllegalArgumentException("No puedes solicitar tu propio servicio")

/**
 * Caso de uso para la creación de solicitudes de servicio/citas.
 * Genera automáticamente una notificación [REQUEST_RECEIVED] para el proveedor.
 *
 * Un usuario no puede solicitar su propia publicación: el cliente y el proveedor serían la
 * misma persona y la solicitud quedaría sin un tercero que preste el servicio.
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

        if (publication.belongsTo(currentUser.id, currentUser.email)) {
            throw CannotRequestOwnServiceException()
        }

        val providerId = publication.authorId.ifBlank { publication.provider.id }
        val newRequest = ServiceRequest(
            id = "req_${System.currentTimeMillis()}",
            publicationId = publicationId,
            clientId = currentUser.id,
            providerId = providerId,
            scheduledDateTime = scheduledDateTime,
            note = note,
            totalAmount = totalAmount
        )

        serviceRequestRepository.createRequest(newRequest)

        // Generar notificación para el proveedor
        generateNotificationUseCase.generateRequestReceivedNotification(
            providerId = providerId,
            clientName = currentUser.name,
            serviceTitle = publication.title,
            requestId = newRequest.id
        )

        newRequest
    }
}
