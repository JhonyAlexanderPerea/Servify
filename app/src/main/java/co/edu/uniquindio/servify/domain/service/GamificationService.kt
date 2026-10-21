package co.edu.uniquindio.servify.domain.service

import co.edu.uniquindio.servify.domain.model.Badge
import co.edu.uniquindio.servify.domain.model.Comment
import co.edu.uniquindio.servify.domain.model.Level
import co.edu.uniquindio.servify.domain.model.Publication
import co.edu.uniquindio.servify.domain.model.User
import co.edu.uniquindio.servify.domain.repository.UserRepository
import co.edu.uniquindio.servify.domain.rules.BadgeEvaluator
import co.edu.uniquindio.servify.domain.rules.BadgeProgress
import co.edu.uniquindio.servify.domain.rules.LevelProgress
import co.edu.uniquindio.servify.domain.rules.PointEvent
import co.edu.uniquindio.servify.domain.rules.ReputationRules

/** Todo lo que necesitan Perfil (17), Estadísticas (18) y Logros (19) en una sola llamada. */
data class UserProgress(
    val user: User,
    val level: LevelProgress,
    val badges: List<BadgeProgress>
) {
    val unlockedCount: Int get() = badges.count { it.unlocked }
    val totalCount: Int get() = badges.size
    val globalFraction: Float get() = if (badges.isEmpty()) 0f else unlockedCount.toFloat() / totalCount
}

data class AwardResult(
    val user: User,
    val levelBefore: Level,
    val levelAfter: Level,
    val newBadges: Set<Badge>
) {
    val leveledUp: Boolean get() = levelAfter.ordinal > levelBefore.ordinal
}

/**
 * Aplica las reglas de puntos/niveles/insignias sobre el [UserRepository].
 *
 * Las publicaciones y comentarios se piden mediante lambdas ([publications], [comments]) para no
 * depender de los repositorios de publicaciones (así no hay dependencia circular).
 *
 * IMPORTANTE para quien lo llame: invocar [award] DESPUÉS de haber guardado la publicación/comentario/voto,
 * para que el evaluador de insignias ya "vea" el cambio.
 *
 * [onBadgeUnlocked] es el gancho al generador de notificaciones: cuando exista, se conecta aquí
 * (ver AppContainer) para crear la notificación "Nuevo logro desbloqueado".
 */
class GamificationService(
    private val userRepository: UserRepository,
    private val publications: () -> List<Publication>,
    private val comments: () -> List<Comment>,
    private val onBadgeUnlocked: (userId: String, badge: Badge) -> Unit = { _, _ -> }
) {

    /** Suma/resta puntos por [event], luego evalúa insignias. Null si el usuario no existe. */
    fun award(userId: String, event: PointEvent): AwardResult? {
        val before = userRepository.getById(userId) ?: return null
        userRepository.update(userId) { it.copy(points = ReputationRules.applyEvent(it.points, event)) }
        val newBadges = evaluateBadges(userId)
        val after = userRepository.getById(userId) ?: return null
        return AwardResult(after, before.level, after.level, newBadges)
    }

    /**
     * Evalúa y registra las insignias nuevas (sin tocar puntos). Úsalo cuando cambia algo que no son
     * puntos: verificar/rechazar una publicación, por ejemplo. Devuelve solo las recién desbloqueadas.
     */
    fun evaluateBadges(userId: String, notify: Boolean = true): Set<Badge> {
        val user = userRepository.getById(userId) ?: return emptySet()
        val unlocked = BadgeEvaluator.newlyUnlocked(user, publications(), comments())
        if (unlocked.isEmpty()) return emptySet()
        userRepository.update(userId) { it.copy(badges = it.badges + unlocked) }
        if (notify) unlocked.forEach { onBadgeUnlocked(userId, it) }
        return unlocked
    }

    fun progressOf(userId: String): UserProgress? {
        val user = userRepository.getById(userId) ?: return null
        return UserProgress(
            user = user,
            level = ReputationRules.progress(user.points),
            badges = BadgeEvaluator.progress(user, publications(), comments())
        )
    }
}
