package co.edu.uniquindio.servify.domain.rules

import co.edu.uniquindio.servify.domain.model.Publication
import co.edu.uniquindio.servify.domain.model.PublicationStatus

/** Pestañas de "Mis publicaciones" (pantalla 15). */
enum class PublicationTab(val label: String) {
    TODAS("Todas"),
    PENDIENTES("Pendientes"),
    ACTIVAS("Activas"),
    FINALIZADAS("Finalizadas"),
    RECHAZADAS("Rechazadas");

    /** Estado que muestra la pestaña; null = todos. */
    val status: PublicationStatus?
        get() = when (this) {
            TODAS -> null
            PENDIENTES -> PublicationStatus.PENDIENTE
            ACTIVAS -> PublicationStatus.VERIFICADA
            FINALIZADAS -> PublicationStatus.FINALIZADA
            RECHAZADAS -> PublicationStatus.RECHAZADA
        }
}

/** Acciones del menú de cada publicación (Ver / Editar / Eliminar / Finalizar). */
enum class PublicationAction { VER, EDITAR, ELIMINAR, FINALIZAR }

/**
 * Reglas de transición y permisos sobre publicaciones. Son funciones puras (sin Android) para
 * poder probarlas con JUnit local; los casos de uso y la UI usan estas mismas reglas.
 *
 * Transiciones del autor:
 *  - PENDIENTE / RECHAZADA -> editable (una rechazada se corrige y vuelve a enviarse).
 *  - VERIFICADA            -> puede finalizarse. Editarla cambiaría lo que ya fue verificado,
 *                             así que no se permite.
 *  - FINALIZADA            -> estado terminal.
 *  - Eliminar              -> siempre permitido al autor (borra su propia publicación).
 */
object PublicationRules {

    fun matches(tab: PublicationTab, publication: Publication): Boolean =
        tab.status == null || publication.status == tab.status

    fun filter(tab: PublicationTab, publications: List<Publication>): List<Publication> =
        publications.filter { matches(tab, it) }

    /** Conteo por pestaña (para mostrar "Pendientes (2)"). */
    fun counts(publications: List<Publication>): Map<PublicationTab, Int> =
        PublicationTab.entries.associateWith { tab -> publications.count { matches(tab, it) } }

    fun canEdit(status: PublicationStatus): Boolean =
        status == PublicationStatus.PENDIENTE || status == PublicationStatus.RECHAZADA

    fun canFinalize(status: PublicationStatus): Boolean = status == PublicationStatus.VERIFICADA

    fun canDelete(@Suppress("UNUSED_PARAMETER") status: PublicationStatus): Boolean = true

    /** Acciones disponibles para el menú, en el orden en que se muestran. */
    fun actionsFor(status: PublicationStatus): List<PublicationAction> = buildList {
        add(PublicationAction.VER)
        if (canEdit(status)) add(PublicationAction.EDITAR)
        if (canFinalize(status)) add(PublicationAction.FINALIZAR)
        if (canDelete(status)) add(PublicationAction.ELIMINAR)
    }
}
