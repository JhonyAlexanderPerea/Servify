package co.edu.uniquindio.servify.domain.model

/**
 * Modelo de dominio que representa coordenadas geográficas (latitud y longitud).
 */
data class Location(
    val latitude: Double,
    val longitude: Double,
    val address: String? = null
)
