package co.edu.uniquindio.servify.domain.usecase.request

import co.edu.uniquindio.servify.domain.model.ServiceRequest
import co.edu.uniquindio.servify.domain.repository.PublicationRepository
import co.edu.uniquindio.servify.domain.repository.ServiceRequestRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

/** Una solicitud enviada o recibida junto con los datos de la publicación a la que apunta. */
data class MyRequest(
    val request: ServiceRequest,
    val publicationTitle: String,
    val providerName: String,
    val isReceivedAsProvider: Boolean = false
)

/** Solicitudes enviadas (como cliente) y recibidas (como proveedor), las más recientes primero. */
class ObserveMyRequestsUseCase @Inject constructor(
    private val serviceRequestRepository: ServiceRequestRepository,
    private val publicationRepository: PublicationRepository
) {
    operator fun invoke(userId: String): Flow<List<MyRequest>> =
        combine(
            serviceRequestRepository.observeRequestsForUser(userId, isProvider = false),
            serviceRequestRepository.observeRequestsForUser(userId, isProvider = true),
            publicationRepository.observePublications()
        ) { clientRequests, providerRequests, publications ->
            val byId = publications.associateBy { it.id }
            val clientItems = clientRequests.map { req ->
                val pub = byId[req.publicationId]
                MyRequest(
                    request = req,
                    publicationTitle = pub?.title ?: "Servicio contratado",
                    providerName = pub?.provider?.name ?: "Proveedor",
                    isReceivedAsProvider = false
                )
            }
            val providerItems = providerRequests.map { req ->
                val pub = byId[req.publicationId]
                MyRequest(
                    request = req,
                    publicationTitle = pub?.title ?: "Servicio ofrecido",
                    providerName = "Solicitud recibida de cliente",
                    isReceivedAsProvider = true
                )
            }
            (clientItems + providerItems)
                .distinctBy { it.request.id }
                .sortedByDescending { it.request.createdAt }
        }
}
