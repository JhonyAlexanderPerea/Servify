package co.edu.uniquindio.servify.domain.util

import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Calculador de distancia geodésica mediante la fórmula de Haversine en Kotlin puro.
 */
@Singleton
class GeoDistanceCalculator @Inject constructor() {

    /**
     * Calcula la distancia en kilómetros entre dos coordenadas geográficas.
     * @param lat1 Latitud del punto 1 en grados
     * @param lon1 Longitud del punto 1 en grados
     * @param lat2 Latitud del punto 2 en grados
     * @param lon2 Longitud del punto 2 en grados
     * @return Distancia calculada en kilómetros.
     */
    fun calculateDistanceKm(
        lat1: Double,
        lon1: Double,
        lat2: Double,
        lon2: Double
    ): Double {
        if ((lat1 == 0.0 && lon1 == 0.0) || (lat2 == 0.0 && lon2 == 0.0)) {
            return Double.MAX_VALUE
        }

        val earthRadiusKm = 6371.0

        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)

        val radLat1 = Math.toRadians(lat1)
        val radLat2 = Math.toRadians(lat2)

        val a = sin(dLat / 2) * sin(dLat / 2) +
                sin(dLon / 2) * sin(dLon / 2) * cos(radLat1) * cos(radLat2)

        val c = 2 * atan2(sqrt(a), sqrt(1 - a))

        return earthRadiusKm * c
    }
}
