package co.edu.uniquindio.servify.features.publication.mine

import co.edu.uniquindio.servify.domain.model.Publication
import co.edu.uniquindio.servify.domain.rules.PublicationAction
import co.edu.uniquindio.servify.domain.rules.PublicationRules
import co.edu.uniquindio.servify.domain.rules.PublicationTab

/** Acción que espera confirmación del usuario en el diálogo (Eliminar / Finalizar). */
data class PendingConfirmation(
    val action: PublicationAction,
    val publicationId: String,
    val publicationTitle: String
)

data class MyPublicationsUiState(
    val isLoading: Boolean = true,
    val selectedTab: PublicationTab = PublicationTab.TODAS,
    val publications: List<Publication> = emptyList(),
    val pendingConfirmation: PendingConfirmation? = null,
    /** Mensaje de un solo uso para el Snackbar; la pantalla avisa con onMessageShown(). */
    val message: String? = null
) {
    /** Publicaciones de la pestaña seleccionada. */
    val visiblePublications: List<Publication>
        get() = PublicationRules.filter(selectedTab, publications)

    val counts: Map<PublicationTab, Int>
        get() = PublicationRules.counts(publications)

    /** true cuando el usuario no tiene ninguna publicación (estado vacío de la pantalla). */
    val isEmpty: Boolean get() = !isLoading && publications.isEmpty()
}
