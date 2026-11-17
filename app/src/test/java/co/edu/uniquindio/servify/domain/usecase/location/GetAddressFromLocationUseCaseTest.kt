package co.edu.uniquindio.servify.domain.usecase.location

import co.edu.uniquindio.servify.data.repository.memory.InMemoryGeocodingRepository
import co.edu.uniquindio.servify.domain.model.Location
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class GetAddressFromLocationUseCaseTest {

    private val repository = InMemoryGeocodingRepository()
    private val useCase = GetAddressFromLocationUseCase(repository)

    @Test
    fun invoke_returnsAddressFromRepository() = runTest {
        val location = Location(4.5339, -75.6811)
        val address = useCase(location)
        assertEquals("Dirección de prueba", address)
    }
}
