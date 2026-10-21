package co.edu.uniquindio.servify.domain.model

/**
 * Niveles de reputación. El nivel NO se guarda: siempre se deriva de los puntos
 * (ver [Level.fromPoints]), así nunca puede quedar desincronizado.
 *
 * Umbrales (puntos mínimos): Principiante 0 · Profesional 200 · Experto 600 · Maestro 1.500
 */
enum class Level(val label: String, val minPoints: Int) {
    PRINCIPIANTE("Principiante", 0),
    PROFESIONAL("Profesional", 200),
    EXPERTO("Experto", 600),
    MAESTRO("Maestro", 1_500);

    /** Siguiente nivel, o null si ya es el máximo. */
    val next: Level? get() = entries.getOrNull(ordinal + 1)

    companion object {
        fun fromPoints(points: Int): Level =
            entries.last { points >= it.minPoints }
    }
}
