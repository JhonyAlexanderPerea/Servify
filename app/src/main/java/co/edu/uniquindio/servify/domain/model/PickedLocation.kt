package co.edu.uniquindio.servify.domain.model

/**
 * Modelo de ubicación seleccionada por el usuario en el mapa.
 */
data class PickedLocation(
    val latitude: Double,
    val longitude: Double,
    val address: String
)
