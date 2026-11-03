package co.edu.uniquindio.servify.domain.model

/**
 * Modelo de dominio que representa una solicitud de contratación o reserva de cita.
 */
data class ServiceRequest(
    val id: String,
    val publicationId: String,
    val clientId: String,
    val providerId: String,
    val scheduledDateTime: Long,
    val status: RequestStatus = RequestStatus.PENDING,
    val note: String? = null,
    val totalAmount: Double,
    val createdAt: Long = System.currentTimeMillis()
)
