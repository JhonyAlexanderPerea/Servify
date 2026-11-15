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

    override suspend fun forward(address: String): Location? {
        val clean = address.lowercase()
        return when {
            clean.contains("pereira") || clean.contains("dosquebradas") -> Location(4.81333, -75.69611)
            clean.contains("manizales") || clean.contains("caldas") -> Location(5.06889, -75.51738)
            else -> Location(4.53389, -75.68111) // Armenia, Quindío
        }
    }
}
