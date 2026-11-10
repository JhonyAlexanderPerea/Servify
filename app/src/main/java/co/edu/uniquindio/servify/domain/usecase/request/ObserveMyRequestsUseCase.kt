package co.edu.uniquindio.servify.domain.usecase.request

import co.edu.uniquindio.servify.domain.model.ServiceRequest
import co.edu.uniquindio.servify.domain.repository.PublicationRepository
import co.edu.uniquindio.servify.domain.repository.ServiceRequestRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

/** Una solicitud enviada junto con los datos de la publicación a la que apunta. */
data class MyRequest(
    val request: ServiceRequest,
    val publicationTitle: String,
    val providerName: String
)

/** Solicitudes enviadas por el usuario, las más recientes primero. */
class ObserveMyRequestsUseCase @Inject constructor(
    private val serviceRequestRepository: ServiceRequestRepository,
    private val publicationRepository: PublicationRepository
) {
    operator fun invoke(clientId: String): Flow<List<MyRequest>> =
        combine(
            serviceRequestRepository.observeRequests(),
            publicationRepository.observePublications()
        ) { requests, publications ->
            val byId = publications.associateBy { it.id }
            requests
                .filter { it.clientId == clientId }
                .sortedByDescending { it.createdAt }
                .map { request ->
                    val publication = byId[request.publicationId]
                    MyRequest(
                        request = request,
                        publicationTitle = publication?.title ?: "Servicio no disponible",
                        providerName = publication?.provider?.name ?: "Proveedor"
                    )
                }
        }
}
