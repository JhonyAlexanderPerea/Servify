package co.edu.uniquindio.servify.ui.format

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import co.edu.uniquindio.servify.R
import kotlin.math.roundToInt

/**
 * Formatea una distancia en kilómetros a un texto legible para la UI (ej. "A 450 m" o "A 2.4 km").
 * Devuelve null si la distancia no está disponible o es inválida.
 */
@Composable
fun formatDistance(distanceKm: Double?): String? {
    if (distanceKm == null || distanceKm.isNaN() || distanceKm.isInfinite() || distanceKm >= 9999.0) return null
    return if (distanceKm < 1.0) {
        val meters = (distanceKm * 1000).roundToInt()
        stringResource(R.string.distance_meters_format, meters)
    } else {
        stringResource(R.string.distance_km_format, distanceKm)
    }
}
