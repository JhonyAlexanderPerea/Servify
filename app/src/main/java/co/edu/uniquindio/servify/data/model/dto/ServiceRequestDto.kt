package co.edu.uniquindio.servify.data.model.dto

import androidx.annotation.Keep
import co.edu.uniquindio.servify.domain.model.Location
import co.edu.uniquindio.servify.domain.model.RequestStatus
import co.edu.uniquindio.servify.domain.model.ServiceRequest

/**
 * Data Transfer Object (DTO) para la colección "service_requests" en Firebase Firestore.
 */
@Keep
data class ServiceRequestDto(
    val id: String = "",
    val publicationId: String = "",
    val publicationTitle: String = "",
    val clientId: String = "",
    val clientName: String = "",
    val providerId: String = "",
    val providerName: String = "",
    val scheduledDateTime: Long = 0L,
    val status: String = "PENDING",
    val note: String? = null,
    val latitude: Double? = null,
    val longitude: Double? = null,
    val address: String? = null,
    val totalAmount: Double = 0.0,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
) {
    fun toDomain(): ServiceRequest {
        val resolvedLocation = if (latitude != null && longitude != null) {
            Location(latitude = latitude, longitude = longitude, address = address)
        } else if (!address.isNullOrBlank()) {
            Location(latitude = 0.0, longitude = 0.0, address = address)
        } else null

        return ServiceRequest(
            id = id,
            publicationId = publicationId,
            clientId = clientId,
            providerId = providerId,
            scheduledDateTime = scheduledDateTime,
            status = runCatching { RequestStatus.valueOf(status) }.getOrDefault(RequestStatus.PENDING),
            note = note,
            location = resolvedLocation,
            totalAmount = totalAmount,
            createdAt = createdAt
        )
    }

    fun toDocument(): Map<String, Any?> = mapOf(
        "id" to id,
        "publicationId" to publicationId,
        "publicationTitle" to publicationTitle,
        "clientId" to clientId,
        "clientName" to clientName,
        "providerId" to providerId,
        "providerName" to providerName,
        "scheduledDateTime" to scheduledDateTime,
        "status" to status,
        "note" to note,
        "latitude" to latitude,
        "longitude" to longitude,
        "address" to address,
        "totalAmount" to totalAmount,
        "createdAt" to createdAt,
        "updatedAt" to updatedAt
    )

    companion object {
        fun fromDomain(
            request: ServiceRequest,
            publicationTitle: String = "",
            clientName: String = "",
            providerName: String = ""
        ): ServiceRequestDto = ServiceRequestDto(
            id = request.id,
            publicationId = request.publicationId,
            publicationTitle = publicationTitle,
            clientId = request.clientId,
            clientName = clientName,
            providerId = request.providerId,
            providerName = providerName,
            scheduledDateTime = request.scheduledDateTime,
            status = request.status.name,
            note = request.note,
            latitude = request.location?.latitude,
            longitude = request.location?.longitude,
            address = request.location?.address,
            totalAmount = request.totalAmount,
            createdAt = request.createdAt,
            updatedAt = System.currentTimeMillis()
        )
    }
}
