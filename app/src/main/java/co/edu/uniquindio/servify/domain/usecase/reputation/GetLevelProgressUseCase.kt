package co.edu.uniquindio.servify.domain.usecase.reputation

import co.edu.uniquindio.servify.domain.rules.LevelProgress
import co.edu.uniquindio.servify.domain.service.GamificationService
import javax.inject.Inject

/**
 * Progreso del usuario dentro de su nivel: puntos, nivel actual, siguiente nivel y
 * fracción avanzada. Null si el usuario no existe.
 */
class GetLevelProgressUseCase @Inject constructor(private val gamificationService: GamificationService) {

    suspend operator fun invoke(userId: String): LevelProgress? =
        gamificationService.progressOf(userId)?.level
}
