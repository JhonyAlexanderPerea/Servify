package co.edu.uniquindio.servify.domain.model

/**
 * Niveles de reputación basados en puntos.
 */
enum class Level(val minPoints: Int) {
    NOVATO(0),
    BRONCE(50),
    PLATA(150),
    ORO(300),
    PLATINO(500);

    companion object {
        fun fromPoints(points: Int): Level = entries
            .filter { points >= it.minPoints }
            .maxByOrNull { it.minPoints }
            ?: NOVATO
    }
}
