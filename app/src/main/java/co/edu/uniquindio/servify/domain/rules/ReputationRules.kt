package co.edu.uniquindio.servify.domain.rules

import co.edu.uniquindio.servify.domain.model.Level

/** Eventos que mueven los puntos de un usuario. */
enum class PointEvent(val points: Int) {
    PUBLICATION_CREATED(20),
    PUBLICATION_VERIFIED(30),
    COMMENT_POSTED(5),
    VOTE_RECEIVED(2),

    /** Revierte exactamente lo que dio VOTE_RECEIVED (evita ganar puntos alternando el voto). */
    VOTE_REVOKED(-2)
}

/**
 * Progreso dentro del nivel actual (perfil y estadísticas).
 * [fraction] va de 0f a 1f; en el nivel máximo es 1f y [next]/[pointsToNext] son null/0.
 */
data class LevelProgress(
    val points: Int,
    val level: Level,
    val next: Level?,
    val pointsIntoLevel: Int,
    val pointsToNext: Int,
    val fraction: Float
)

object ReputationRules {

    /** Los puntos nunca bajan de 0. */
    fun applyEvent(currentPoints: Int, event: PointEvent): Int =
        (currentPoints + event.points).coerceAtLeast(0)

    /**
     * Comentar la publicación propia no suma puntos (evita autoinflar la reputación).
     * Devuelve null si no corresponde ningún evento.
     */
    fun eventForComment(commenterId: String, publicationAuthorId: String): PointEvent? =
        if (commenterId == publicationAuthorId) null else PointEvent.COMMENT_POSTED

    fun levelFor(points: Int): Level = Level.fromPoints(points)

    fun progress(points: Int): LevelProgress {
        val safe = points.coerceAtLeast(0)
        val level = Level.fromPoints(safe)
        val next = level.next
        return if (next == null) {
            LevelProgress(safe, level, null, safe - level.minPoints, 0, 1f)
        } else {
            val span = next.minPoints - level.minPoints
            val into = safe - level.minPoints
            LevelProgress(
                points = safe,
                level = level,
                next = next,
                pointsIntoLevel = into,
                pointsToNext = next.minPoints - safe,
                fraction = (into.toFloat() / span).coerceIn(0f, 1f)
            )
        }
    }
}
