package co.edu.uniquindio.servify.domain.rules

import co.edu.uniquindio.servify.domain.model.Badge
import co.edu.uniquindio.servify.domain.model.Comment
import co.edu.uniquindio.servify.domain.model.Publication
import co.edu.uniquindio.servify.domain.model.PublicationStatus
import co.edu.uniquindio.servify.domain.model.User

/** Estado de una insignia para la pantalla de Logros (color/gris + barra de progreso). */
data class BadgeProgress(
    val badge: Badge,
    val current: Int,
    val target: Int,
    val unlocked: Boolean
) {
    val fraction: Float
        get() = if (unlocked) 1f else (current.toFloat() / target.coerceAtLeast(1)).coerceIn(0f, 1f)
}

/**
 * Evalúa las 7 insignias. Es una función pura: recibe el usuario y los datos, no toca estado.
 * Una insignia, una vez desbloqueada, no se pierde (aunque luego se borre la publicación o baje un voto).
 */
object BadgeEvaluator {
    const val HIGHLIGHT_MIN_VOTES = 5
    const val TRUSTED_MIN_VERIFIED = 3

    fun progress(
        user: User,
        publications: List<Publication>,
        comments: List<Comment>
    ): List<BadgeProgress> {
        val mine = publications.filter { it.authorId == user.id }
        val verified = mine.count { it.wasVerified }
        val rejectedNow = mine.count { it.status == PublicationStatus.RECHAZADA }
        val commentsMade = comments.count { it.authorId == user.id }

        // "Más destacada": la verificada con más votos entre TODAS (empate = ambos autores la ganan).
        val topVotesGlobal = publications.filter { it.wasVerified }.maxOfOrNull { it.interestedCount } ?: 0
        val myBestVotes = mine.filter { it.wasVerified }.maxOfOrNull { it.interestedCount } ?: 0
        val highlightTarget = maxOf(HIGHLIGHT_MIN_VOTES, topVotesGlobal)
        val isHighlighted = myBestVotes >= HIGHLIGHT_MIN_VOTES && myBestVotes >= topVotesGlobal

        val trusted = verified >= TRUSTED_MIN_VERIFIED && rejectedNow == 0

        fun entry(badge: Badge, current: Int, target: Int, met: Boolean) = BadgeProgress(
            badge = badge,
            current = current.coerceAtMost(target),
            target = target,
            unlocked = met || badge in user.badges
        )

        return listOf(
            entry(Badge.PRIMERA_PUBLICACION, mine.size, 1, mine.isNotEmpty()),
            entry(Badge.PRIMERA_VERIFICADA, verified, 1, verified >= 1),
            entry(Badge.DIEZ_VERIFICADAS, verified, 10, verified >= 10),
            entry(Badge.CIEN_PUNTOS, user.points, 100, user.points >= 100),
            entry(Badge.PUBLICACION_DESTACADA, myBestVotes, highlightTarget, isHighlighted),
            entry(Badge.DIEZ_COMENTARIOS, commentsMade, 10, commentsMade >= 10),
            entry(Badge.USUARIO_CONFIABLE, verified, TRUSTED_MIN_VERIFIED, trusted)
        )
    }

    /** Insignias que se cumplen ahora y que el usuario todavía no tiene registradas. */
    fun newlyUnlocked(
        user: User,
        publications: List<Publication>,
        comments: List<Comment>
    ): Set<Badge> =
        progress(user, publications, comments)
            .filter { it.unlocked && it.badge !in user.badges }
            .map { it.badge }
            .toSet()
}
