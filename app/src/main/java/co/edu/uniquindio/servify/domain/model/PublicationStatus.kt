package co.edu.uniquindio.servify.domain.model

/**
 * Estado del ciclo de vida de una oferta o publicación de servicio.
 */
enum class PublicationStatus {
    PENDIENTE,
    VERIFICADA,
    RECHAZADA,
    FINALIZADA,
    APPROVED,
    PENDING,
    REJECTED,
    DRAFT,
    PENDING_REVIEW,
    COMPLETED,
    PAUSED
}
