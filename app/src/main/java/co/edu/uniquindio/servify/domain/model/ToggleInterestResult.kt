package co.edu.uniquindio.servify.domain.model

/** Resultado de la operación "Me interesa" / "Ya no me interesa". */
sealed interface ToggleInterestResult {
    /** La operación se completó correctamente. [nowInterested] indica el nuevo estado. */
    data class Success(val nowInterested: Boolean) : ToggleInterestResult

    /** El usuario no está autenticado. */
    data object NotAuthenticated : ToggleInterestResult

    /** La publicación no existe. */
    data object PublicationNotFound : ToggleInterestResult

    /** El usuario no puede interactuar con su propia publicación. */
    data object OwnPublication : ToggleInterestResult

    /** La publicación no está en un estado que permita interacciones (p. ej. pendiente, rechazada, finalizada). */
    data object PublicationNotActive : ToggleInterestResult

    /** Error genérico de Firestore / red. */
    data class Error(val message: String) : ToggleInterestResult
}