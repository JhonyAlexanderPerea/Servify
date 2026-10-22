package co.edu.uniquindio.servify.domain.model

/**
 * Estado del agendamiento de una cita presencial o remota.
 */
enum class AppointmentStatus {
    /** Cita agendada y confirmada. */
    SCHEDULED,

    /** En curso o en desarrollo. */
    IN_PROGRESS,

    /** Finalizada satisfactoriamente. */
    COMPLETED,

    /** Cancelada. */
    CANCELLED,

    /** El cliente o proveedor no asistió. */
    NO_SHOW
}
