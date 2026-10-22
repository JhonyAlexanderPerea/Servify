package co.edu.uniquindio.servify.domain.model

/**
 * Tipo o categoría de notificación dentro de Servify.
 */
enum class NotificationType {
    /** Nueva solicitud de servicio recibida. */
    REQUEST_RECEIVED,

    /** Solicitud de servicio aceptada por el proveedor. */
    REQUEST_ACCEPTED,

    /** Recordatorio de cita próxima a realizarse. */
    APPOINTMENT_REMINDER,

    /** Nueva reseña u opinión publicada. */
    REVIEW_PUBLISHED,

    /** Logro e insignia desbloqueada por el usuario. */
    ACHIEVEMENT_UNLOCKED,

    /** Actualización de estado en el proceso de moderación. */
    MODERATION_UPDATE
}
