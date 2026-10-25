package co.edu.uniquindio.servify.domain.usecase.reputation

import co.edu.uniquindio.servify.domain.rules.PointEvent
import co.edu.uniquindio.servify.domain.service.AwardResult
import co.edu.uniquindio.servify.domain.service.GamificationService
import javax.inject.Inject

/**
 * Suma (o resta) puntos por un evento de reputación y reevalúa las insignias.
 *
 * Invocarlo DESPUÉS de haber guardado la publicación, el comentario o el voto, para
 * que el evaluador de insignias ya vea el cambio.
 */
class AwardPointsUseCase @Inject constructor(private val gamificationService: GamificationService) {

    suspend operator fun invoke(userId: String, event: PointEvent): AwardResult? =
        gamificationService.award(userId, event)
}
