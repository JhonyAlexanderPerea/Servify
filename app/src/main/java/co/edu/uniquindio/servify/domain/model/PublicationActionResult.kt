package co.edu.uniquindio.servify.domain.model

/** Resultado de las acciones del autor sobre una publicación suya (finalizar, eliminar). */
sealed interface PublicationActionResult {
    data object Success : PublicationActionResult
    data object NotFound : PublicationActionResult

    /** Solo el autor puede actuar sobre su publicación. */
    data object NotOwner : PublicationActionResult

    /** La transición no está permitida desde el estado actual (p. ej. finalizar una pendiente). */
    data class NotAllowed(val status: PublicationStatus) : PublicationActionResult
}
