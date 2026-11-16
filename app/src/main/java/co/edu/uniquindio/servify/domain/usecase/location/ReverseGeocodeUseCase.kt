package co.edu.uniquindio.servify.domain.usecase.location

import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.os.Build
import co.edu.uniquindio.servify.domain.model.Location
import co.edu.uniquindio.servify.domain.repository.GeocodingRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import java.util.Locale
import javax.inject.Inject
import kotlin.coroutines.resume

/**
 * Caso de uso para obtener la dirección legible a partir de latitud y longitud.
 * Utiliza android.location.Geocoder (asíncrono para API 33+ con fallback para APIs anteriores en Dispatchers.IO)
 * y realiza fallback al repositorio de geocodificación de Mapbox si Geocoder falla.
 */
class ReverseGeocodeUseCase @Inject constructor(
    @param:ApplicationContext private val context: Context,
    private val geocodingRepository: GeocodingRepository
) {
    suspend operator fun invoke(latitude: Double, longitude: Double): String = withContext(Dispatchers.IO) {
        val geocoderAddress = getAddressFromGeocoder(context, latitude, longitude)
        if (!geocoderAddress.isNullOrBlank()) {
            return@withContext geocoderAddress
        }

        val mapboxAddress = runCatching {
            geocodingRepository.reverse(Location(latitude, longitude))
        }.getOrNull()

        if (!mapboxAddress.isNullOrBlank()) {
            return@withContext mapboxAddress
        }

        String.format(Locale.US, "%.5f, %.5f", latitude, longitude)
    }

    private suspend fun getAddressFromGeocoder(
        context: Context,
        latitude: Double,
        longitude: Double
    ): String? {
        if (!Geocoder.isPresent()) return null

        val geocoder = Geocoder(context, Locale.getDefault())

        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            suspendCancellableCoroutine { continuation ->
                try {
                    geocoder.getFromLocation(
                        latitude,
                        longitude,
                        1,
                        object : Geocoder.GeocodeListener {
                            override fun onGeocode(addresses: MutableList<Address>) {
                                val addr = addresses.firstOrNull()?.formatAddress()
                                if (continuation.isActive) continuation.resume(addr)
                            }

                            override fun onError(errorMessage: String?) {
                                if (continuation.isActive) continuation.resume(null)
                            }
                        }
                    )
                } catch (_: Exception) {
                    if (continuation.isActive) continuation.resume(null)
                }
            }
        } else {
            try {
                @Suppress("DEPRECATION")
                val addresses = geocoder.getFromLocation(latitude, longitude, 1)
                addresses?.firstOrNull()?.formatAddress()
            } catch (_: Exception) {
                null
            }
        }
    }

    private fun Address.formatAddress(): String {
        val line = getAddressLine(0)
        if (!line.isNullOrBlank()) return line
        val parts = listOfNotNull(subLocality ?: featureName, locality ?: subAdminArea, adminArea)
        return if (parts.isNotEmpty()) parts.joinToString(", ") else String.format(Locale.US, "%.5f, %.5f", latitude, longitude)
    }
}
