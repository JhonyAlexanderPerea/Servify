package co.edu.uniquindio.servify.domain.model

/**
 * Estado del ciclo de vida de una oferta o publicación de servicio.
 */
enum class PublicationStatus {
    /** Borrador guardado localmente por el proveedor. */
    DRAFT,

    /** Enviada y en proceso de revisión por moderador o IA. */
    PENDING_REVIEW,

    /** Aprobada y visible para todos los usuarios en el Marketplace. */
    APPROVED,

    /** Rechazada por incumplimiento de políticas o información insuficiente. */
    REJECTED,

    /** Servicio finalizado por el proveedor. */
    COMPLETED,

    /** Pausada temporalmente por el proveedor. */
    PAUSED
}
