package co.edu.uniquindio.servify.domain.usecase.moderation

import co.edu.uniquindio.servify.domain.model.ModerationMetrics
import co.edu.uniquindio.servify.domain.repository.PublicationRepository
import co.edu.uniquindio.servify.domain.rules.ModerationRules
import co.edu.uniquindio.servify.domain.util.TimeProvider
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

/** Métricas del panel: pendientes, verificadas hoy, rechazadas y finalizadas. */
class ObserveModerationMetricsUseCase @Inject constructor(
    private val publicationRepository: PublicationRepository,
    private val timeProvider: TimeProvider
) {
    operator fun invoke(): Flow<ModerationMetrics> =
        publicationRepository.observePublications()
            .map { ModerationRules.metrics(it, timeProvider.now()) }
}
