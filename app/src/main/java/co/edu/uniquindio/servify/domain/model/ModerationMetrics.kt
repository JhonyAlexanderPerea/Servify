package co.edu.uniquindio.servify.domain.model

/** Métricas del panel de moderación (mockup 22). */
data class ModerationMetrics(
    val pending: Int = 0,
    val verifiedToday: Int = 0,
    val rejected: Int = 0,
    val finalized: Int = 0
)
