package co.edu.uniquindio.servify.domain.usecase.reputation

import co.edu.uniquindio.servify.domain.rules.BadgeProgress
import co.edu.uniquindio.servify.domain.service.GamificationService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

/**
 * Observa el progreso de las insignias del usuario: cuáles están desbloqueadas, cuáles
 * no y cuánto falta en cada una.
 *
 * Es reactivo: emite de nuevo cuando cambian los puntos o las insignias de cualquier
 * usuario, así la pantalla de Logros se actualiza sola.
 */
class ObserveAchievementsUseCase @Inject constructor(private val gamificationService: GamificationService) {

    operator fun invoke(userId: String): Flow<List<BadgeProgress>> =
        gamificationService.observeProgress(userId)
            .map { progress -> progress?.badges ?: emptyList() }
}
