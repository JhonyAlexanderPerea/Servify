package co.edu.uniquindio.servify.domain.rules

import co.edu.uniquindio.servify.domain.model.Publication
import co.edu.uniquindio.servify.domain.model.PublicationStatus
import co.edu.uniquindio.servify.domain.util.GeoDistanceCalculator

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

    /**
     * Asocia la distancia geodésica a cada publicación basada en la ubicación del usuario.
     */
    fun attachDistances(
        publications: List<Publication>,
        userLat: Double?,
        userLng: Double?,
        calculator: GeoDistanceCalculator
    ): List<Publication> {
        if (userLat == null || userLng == null) return publications
        return publications.map { pub ->
            val loc = pub.location
            if (loc != null && (loc.latitude != 0.0 || loc.longitude != 0.0)) {
                val dist = calculator.calculateDistanceKm(userLat, userLng, loc.latitude, loc.longitude)
                pub.copy(distanceKm = if (dist < 10000.0) dist else null)
            } else {
                pub.copy(distanceKm = null)
            }
        }
    }

    /**
     * Ordena las publicaciones por distancia cuando el filtro "Cerca de mí" está activo.
     */
    fun sortFeed(
        publications: List<Publication>,
        filterChip: String,
        isNearMeActive: Boolean = filterChip == "Cerca de mí"
    ): List<Publication> {
        if (isNearMeActive) {
            return publications.sortedBy { it.distanceKm ?: Double.MAX_VALUE }
        }
        return publications
    }
}
