package co.edu.uniquindio.servify.domain.usecase.reputation

import co.edu.uniquindio.servify.domain.service.GamificationService
import co.edu.uniquindio.servify.domain.service.UserProgress
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Observa el progreso completo del usuario (nivel, puntos, insignias). Es la versión reactiva de
 * [GetUserStatsUseCase]: el Perfil se actualiza solo cuando cambian puntos o insignias.
 */
class ObserveUserProgressUseCase @Inject constructor(
    private val gamificationService: GamificationService
) {
    operator fun invoke(userId: String): Flow<UserProgress?> =
        gamificationService.observeProgress(userId)
}
