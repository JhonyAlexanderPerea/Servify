package co.edu.uniquindio.servify.domain.rules

import co.edu.uniquindio.servify.domain.model.Publication
import co.edu.uniquindio.servify.domain.model.PublicationStatus

/**
 * Reglas del feed público (Home y Explorar): solo se muestran las publicaciones verificadas.
 * Las pendientes esperan al moderador, las rechazadas se ocultan y las finalizadas ya no se ofrecen.
 * (APPROVED es el estado heredado de los datos de ejemplo iniciales.)
 */
object FeedRules {

    fun isPublic(status: PublicationStatus): Boolean =
        status == PublicationStatus.VERIFICADA || status == PublicationStatus.APPROVED

    fun publicFeed(publications: List<Publication>): List<Publication> =
        publications.filter { isPublic(it.status) }
}
