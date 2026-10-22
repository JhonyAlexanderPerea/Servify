package co.edu.uniquindio.servify.domain.model

/**
 * Modelo de dominio que representa un nivel dentro del esquema de reputación y puntos Servify.
 */
data class Level(
    val levelNumber: Int,
    val title: String,
    val requiredPoints: Int,
    val reputationLevel: ReputationLevel
)
