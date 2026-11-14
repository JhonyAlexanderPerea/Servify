package co.edu.uniquindio.servify.domain.repository

import co.edu.uniquindio.servify.domain.model.Location

/**
 * Repositorio de geocodificación (inversa y directa) con Mapbox.
 */
interface GeocodingRepository {
    /**
     * Convierte coordenadas geográficas en una dirección formateada.
     * Devuelve null si no hay resultado o no fue posible geocodificar.
     */
    suspend fun reverse(location: Location): String?
}
