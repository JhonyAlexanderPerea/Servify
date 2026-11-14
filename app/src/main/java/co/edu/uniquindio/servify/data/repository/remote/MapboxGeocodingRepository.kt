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
}
