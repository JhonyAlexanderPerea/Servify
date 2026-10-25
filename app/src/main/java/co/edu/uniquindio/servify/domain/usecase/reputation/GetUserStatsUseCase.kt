package co.edu.uniquindio.servify.domain.usecase.reputation

import co.edu.uniquindio.servify.domain.service.GamificationService
import co.edu.uniquindio.servify.domain.service.UserProgress
import javax.inject.Inject

/**
 * Resumen de reputación del usuario: sus datos, el progreso de nivel y el estado de
 * todas las insignias. Es lo que consume la pantalla de Estadísticas.
 */
class GetUserStatsUseCase @Inject constructor(private val gamificationService: GamificationService) {

    suspend operator fun invoke(userId: String): UserProgress? =
        gamificationService.progressOf(userId)
}
