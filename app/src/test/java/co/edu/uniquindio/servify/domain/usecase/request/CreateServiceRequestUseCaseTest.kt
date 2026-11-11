package co.edu.uniquindio.servify.domain.usecase.request

import co.edu.uniquindio.servify.domain.model.Publication
import co.edu.uniquindio.servify.domain.model.User
import co.edu.uniquindio.servify.testutil.TestEnvironment
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class CreateServiceRequestUseCaseTest {

    private val env = TestEnvironment()

    @Test
    fun `no permite solicitar una publicacion propia`() = runBlocking {
        env.loginAs(TestEnvironment.JHONY_EMAIL)

        val result = runCatching {
            env.createServiceRequest(
                publicationId = "pub_jhony_plomeria",
                scheduledDateTime = env.nowMillis,
                note = null,
                totalAmount = 80_000.0
            )
        }

        assertTrue(result.exceptionOrNull() is CannotRequestOwnServiceException)
        assertTrue(
            env.serviceRequests.observeRequests().first().none {
                it.publicationId == "pub_jhony_plomeria" && it.clientId == "user_jhony"
            }
        )
    }

    @Test
    fun `detecta la publicacion propia aunque el id del proveedor sea distinto`() = runBlocking {
        env.loginAs(TestEnvironment.JHONY_EMAIL)
        env.addPublication(
            Publication(
                id = "pub_alias_jhony",
                title = "Servicio con id de proveedor distinto",
                description = "Datos de demostración con el mismo correo y otro id.",
                authorId = "otro_id",
                provider = User(
                    id = "otro_id",
                    name = "Jhony Ramírez",
                    email = TestEnvironment.JHONY_EMAIL
                )
            )
        )

        val result = runCatching {
            env.createServiceRequest(
                publicationId = "pub_alias_jhony",
                scheduledDateTime = env.nowMillis,
                note = null,
                totalAmount = 50_000.0
            )
        }

        assertTrue(result.exceptionOrNull() is CannotRequestOwnServiceException)
    }

    @Test
    fun `permite solicitar el servicio de otro proveedor`() = runBlocking {
        env.loginAs(TestEnvironment.JHONY_EMAIL)

        val request = env.createServiceRequest(
            publicationId = "pub_juan_mudanzas",
            scheduledDateTime = env.nowMillis,
            note = "Mudanza el sábado",
            totalAmount = 250_000.0
        )

        assertEquals("pub_juan_mudanzas", request.publicationId)
        assertEquals("user_jhony", request.clientId)
        assertEquals("user_juan", request.providerId)

        val stored = env.serviceRequests.observeRequests().first().first { it.id == request.id }
        assertEquals(request, stored)
    }
}
