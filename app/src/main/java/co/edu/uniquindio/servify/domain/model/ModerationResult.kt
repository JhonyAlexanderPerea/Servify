package co.edu.uniquindio.servify.domain.model

/** Resultado de una acción de moderación. La UI lo traduce a un mensaje. */
sealed interface ModerationResult {
    data object Success : ModerationResult
    data object NotFound : ModerationResult

    /** El motivo de rechazo es obligatorio y debe estar escrito. */
    data object ReasonRequired : ModerationResult

    /** La publicación no está en un estado que permita la acción. */
    data class NotAllowed(val status: PublicationStatus) : ModerationResult
}
