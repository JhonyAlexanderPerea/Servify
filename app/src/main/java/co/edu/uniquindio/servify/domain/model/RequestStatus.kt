package co.edu.uniquindio.servify.domain.model

/**
 * Estado de una solicitud de contratación de servicio.
 */
enum class RequestStatus {
    /** Pendiente de confirmación por el proveedor. */
    PENDING,

    /** Aceptada por el proveedor. */
    ACCEPTED,

    /** Rechazada por el proveedor. */
    REJECTED,

    /** Cancelada por el cliente o el proveedor. */
    CANCELLED,

    /** Servicio completado exitosamente. */
    COMPLETED
}
