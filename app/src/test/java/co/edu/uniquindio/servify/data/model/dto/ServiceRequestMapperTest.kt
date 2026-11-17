package co.edu.uniquindio.servify.data.model.dto

import co.edu.uniquindio.servify.domain.model.Location
import co.edu.uniquindio.servify.domain.model.RequestStatus
import co.edu.uniquindio.servify.domain.model.ServiceRequest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test

class ServiceRequestMapperTest {

    @Test
    fun `fromDomain y toDocument serializan correctamente la ubicacion`() {
        val location = Location(latitude = 4.53389, longitude = -75.68111, address = "Barrio Granada, Calle 12 # 14-20")
        val request = ServiceRequest(
            id = "req_test_1",
            publicationId = "pub_1",
            clientId = "client_1",
            providerId = "provider_1",
            scheduledDateTime = 1700000000000L,
            status = RequestStatus.PENDING,
            note = "Por favor traer herramientas",
            location = location,
            totalAmount = 80000.0,
            createdAt = 1700000000000L
        )

        val dto = ServiceRequestDto.fromDomain(
            request = request,
            publicationTitle = "Plomería",
            clientName = "Juan",
            providerName = "Pedro"
        )

        assertEquals(4.53389, dto.latitude!!, 0.0001)
        assertEquals(-75.68111, dto.longitude!!, 0.0001)
        assertEquals("Barrio Granada, Calle 12 # 14-20", dto.address)

        val doc = dto.toDocument()
        assertEquals(4.53389, doc["latitude"])
        assertEquals(-75.68111, doc["longitude"])
        assertEquals("Barrio Granada, Calle 12 # 14-20", doc["address"])
    }

    @Test
    fun `toDomain reconstruye la ubicacion intacta cuando existen datos de latitude y longitude`() {
        val dto = ServiceRequestDto(
            id = "req_test_2",
            publicationId = "pub_1",
            clientId = "client_1",
            providerId = "provider_1",
            scheduledDateTime = 1700000000000L,
            status = "PENDING",
            note = "Nota simple",
            latitude = 4.81333,
            longitude = -75.69611,
            address = "Pereira Centro",
            totalAmount = 100000.0
        )

        val domain = dto.toDomain()
        assertNotNull(domain.location)
        assertEquals(4.81333, domain.location!!.latitude, 0.0001)
        assertEquals(-75.69611, domain.location!!.longitude, 0.0001)
        assertEquals("Pereira Centro", domain.location!!.address)
    }

    @Test
    fun `toDomain retorna null para location cuando el documento de Firestore no incluye ubicacion`() {
        val dto = ServiceRequestDto(
            id = "req_legacy_1",
            publicationId = "pub_1",
            clientId = "client_1",
            providerId = "provider_1",
            scheduledDateTime = 1700000000000L,
            status = "PENDING",
            note = "Sin coordenadas",
            latitude = null,
            longitude = null,
            address = null,
            totalAmount = 50000.0
        )

        val domain = dto.toDomain()
        assertNull(domain.location)
        assertEquals("Sin coordenadas", domain.note)
    }
}
