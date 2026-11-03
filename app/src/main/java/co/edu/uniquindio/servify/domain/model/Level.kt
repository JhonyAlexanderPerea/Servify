package co.edu.uniquindio.servify.domain.model

/**
 * Niveles de reputación basados en puntos.
 */
enum class Level(val minPoints: Int, val label: String) {
    PRINCIPIANTE(0, "Principiante"),
    PROFESIONAL(200, "Profesional"),
    EXPERTO(600, "Experto"),
    MAESTRO(1_500, "Maestro");

    val next: Level?
        get() = entries.getOrNull(ordinal + 1)

    companion object {
        fun fromPoints(points: Int): Level = entries
            .filter { points >= it.minPoints }
            .maxByOrNull { it.minPoints }
            ?: PRINCIPIANTE
    }
}
