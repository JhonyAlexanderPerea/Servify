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

    private const val DEFAULT_LAT = 4.5339
    private const val DEFAULT_LNG = -75.6811

    fun isPublic(status: PublicationStatus): Boolean =
        status == PublicationStatus.VERIFICADA || status == PublicationStatus.APPROVED

    fun publicFeed(publications: List<Publication>): List<Publication> =
        publications.filter { isPublic(it.status) }

    /**
     * Asocia la distancia geodésica a cada publicación basada en la ubicación del usuario
     * (con fallback a Armenia, Quindío si no hay ubicación de GPS disponible).
     */
    fun attachDistances(
        publications: List<Publication>,
        userLat: Double?,
        userLng: Double?,
        calculator: GeoDistanceCalculator
    ): List<Publication> {
        val lat = userLat ?: DEFAULT_LAT
        val lng = userLng ?: DEFAULT_LNG
        return publications.map { pub ->
            val loc = pub.location
            if (loc != null && (loc.latitude != 0.0 || loc.longitude != 0.0)) {
                val dist = calculator.calculateDistanceKm(lat, lng, loc.latitude, loc.longitude)
                pub.copy(distanceKm = if (dist < 10000.0) dist else null)
            } else {
                pub.copy(distanceKm = null)
            }
        }
    }

    /**
     * Filtra y ordena las publicaciones que se encuentran dentro del radio máximo (por defecto 50 km,
     * abarcando Armenia, Pereira y alrededores del Eje Cafetero) a partir de la ubicación del usuario.
     */
    fun filterNearMe(
        publications: List<Publication>,
        userLat: Double?,
        userLng: Double?,
        maxRadiusKm: Double = 50.0
    ): List<Publication> {
        val lat = userLat ?: DEFAULT_LAT
        val lng = userLng ?: DEFAULT_LNG
        val calculator = GeoDistanceCalculator()

        return publications
            .map { pub ->
                val dist = pub.distanceKm ?: run {
                    val loc = pub.location
                    if (loc != null && (loc.latitude != 0.0 || loc.longitude != 0.0)) {
                        calculator.calculateDistanceKm(lat, lng, loc.latitude, loc.longitude)
                    } else {
                        Double.MAX_VALUE
                    }
                }
                pub.copy(distanceKm = if (dist < 10000.0) dist else null)
            }
            .filter { pub ->
                val dist = pub.distanceKm
                dist != null && dist <= maxRadiusKm
            }
            .sortedBy { it.distanceKm ?: Double.MAX_VALUE }
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
