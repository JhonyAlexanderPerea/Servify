package co.edu.uniquindio.servify.data.repository.remote

import android.content.Context
import co.edu.uniquindio.servify.R
import co.edu.uniquindio.servify.data.remote.geocoding.MapboxGeocodingApi
import co.edu.uniquindio.servify.domain.model.Location
import co.edu.uniquindio.servify.domain.repository.GeocodingRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MapboxGeocodingRepository @Inject constructor(
    @param:ApplicationContext private val context: Context,
    private val api: MapboxGeocodingApi
) : GeocodingRepository {

    override suspend fun reverse(location: Location): String? {
        val accessToken = context.getString(R.string.mapbox_access_token)
        if (accessToken.isBlank()) {
            throw IllegalStateException("Token de Mapbox no configurado")
        }
        val response = api.reverse(
            longitude = location.longitude,
            latitude = location.latitude,
            accessToken = accessToken
        )
        val properties = response.features?.firstOrNull()?.properties ?: return null
        return properties.fullAddress ?: properties.placeFormatted ?: properties.name
    }

    override suspend fun forward(address: String): Location? {
        val accessToken = context.getString(R.string.mapbox_access_token)
        if (accessToken.isBlank() || address.isBlank()) return defaultLocationFor(address)

        return try {
            val response = api.forward(
                query = address,
                accessToken = accessToken
            )
            val coords = response.features?.firstOrNull()?.geometry?.coordinates
            if (coords != null && coords.size >= 2) {
                Location(latitude = coords[1], longitude = coords[0])
            } else {
                defaultLocationFor(address)
            }
        } catch (_: Exception) {
            defaultLocationFor(address)
        }
    }

    private fun defaultLocationFor(address: String): Location {
        val clean = address.lowercase()
        return when {
            clean.contains("pereira") || clean.contains("dosquebradas") -> Location(4.81333, -75.69611)
            clean.contains("manizales") || clean.contains("caldas") -> Location(5.06889, -75.51738)
            else -> Location(4.53389, -75.68111) // Armenia, Quindío
        }
    }
}
