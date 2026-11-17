package co.edu.uniquindio.servify.domain.usecase.request

import co.edu.uniquindio.servify.domain.model.Location
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
        totalAmount: Double,
        location: Location? = null
    ): ServiceRequest = withContext(Dispatchers.IO) {
        val currentUser = userRepository.observeCurrentUser().first()
        val publication = publicationRepository.getPublicationById(publicationId)
            ?: throw IllegalArgumentException("La publicación $publicationId no existe")

        if (publication.belongsTo(currentUser.id, currentUser.email)) {
            throw CannotRequestOwnServiceException()
        }

        val providerId = publication.authorId.ifBlank { publication.provider.id }

        // El id depende del servicio y del horario: dos clientes que intenten reservar el mismo
        // cupo escriben el mismo documento. La regla de Firestore solo permite crear (no
        // actualizar) solicitudes de otros usuarios, así que la segunda escritura falla sin
        // exponer las solicitudes de terceros al cliente.
        val newRequest = ServiceRequest(
            id = requestIdFor(publicationId, scheduledDateTime),
            publicationId = publicationId,
            clientId = currentUser.id,
            providerId = providerId,
            scheduledDateTime = scheduledDateTime,
            note = note,
            location = location,
            totalAmount = totalAmount
        )

        try {
            serviceRequestRepository.createRequest(newRequest)
        } catch (e: Exception) {
            throw IllegalArgumentException(
                "Ya existe una solicitud o reserva para este servicio en la misma fecha y hora",
                e
            )
        }

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

private fun requestIdFor(publicationId: String, scheduledDateTime: Long): String {
    val safePublicationId = publicationId.replace(Regex("[^A-Za-z0-9_-]"), "_")
    return "req_${safePublicationId}_$scheduledDateTime"
}
