package co.edu.uniquindio.servify.data.repository.memory

import co.edu.uniquindio.servify.domain.model.Location
import co.edu.uniquindio.servify.domain.repository.GeocodingRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class InMemoryGeocodingRepository @Inject constructor() : GeocodingRepository {
    override suspend fun reverse(location: Location): String? {
        return "Dirección de prueba"
    }
}
