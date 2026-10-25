package co.edu.uniquindio.servify.domain.usecase.reputation

import co.edu.uniquindio.servify.domain.model.Badge
import co.edu.uniquindio.servify.domain.service.GamificationService
import javax.inject.Inject

/**
 * Evalúa las insignias del usuario y registra las nuevas.
 *
 * Se usa cuando cambia algo que no son puntos (verificar o rechazar una publicación,
 * por ejemplo). Devuelve solo las recién desbloqueadas.
 *
 * @param notify si es true dispara el gancho de notificación de cada insignia nueva.
 */
class EvaluateAchievementsUseCase @Inject constructor(private val gamificationService: GamificationService) {

    suspend operator fun invoke(userId: String, notify: Boolean = true): Set<Badge> =
        gamificationService.evaluateBadges(userId, notify)
}
