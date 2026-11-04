package co.edu.uniquindio.servify.domain.rules

import co.edu.uniquindio.servify.domain.model.ModerationMetrics
import co.edu.uniquindio.servify.domain.model.Publication
import co.edu.uniquindio.servify.domain.model.PublicationStatus
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

/**
 * Reglas puras de moderación (sin Android, probables con JUnit local).
 *
 *  - Solo se puede verificar o rechazar una publicación PENDIENTE.
 *  - El rechazo exige un motivo escrito de al menos [MIN_REASON_LENGTH] caracteres.
 *  - El moderador puede marcar como finalizada cualquier publicación que no lo esté.
 */
object ModerationRules {

    const val MIN_REASON_LENGTH = 10

    private val dateFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")

    fun canReview(status: PublicationStatus): Boolean = status == PublicationStatus.PENDIENTE

    fun canFinalize(status: PublicationStatus): Boolean = status != PublicationStatus.FINALIZADA

    fun isValidRejectionReason(text: String): Boolean = text.trim().length >= MIN_REASON_LENGTH

    /** Pendientes por orden de llegada: la más antigua primero. */
    fun pendingQueue(publications: List<Publication>): List<Publication> =
        publications
            .filter { it.status == PublicationStatus.PENDIENTE }
            .sortedBy { it.createdAtMillis }

    fun metrics(
        publications: List<Publication>,
        nowMillis: Long,
        zone: ZoneId = ZoneId.systemDefault()
    ): ModerationMetrics {
        val today = Instant.ofEpochMilli(nowMillis).atZone(zone).toLocalDate()
        return ModerationMetrics(
            pending = publications.count { it.status == PublicationStatus.PENDIENTE },
            verifiedToday = publications.count { publication ->
                publication.verifiedAtMillis?.let {
                    Instant.ofEpochMilli(it).atZone(zone).toLocalDate() == today
                } ?: false
            },
            rejected = publications.count { it.status == PublicationStatus.RECHAZADA },
            finalized = publications.count { it.status == PublicationStatus.FINALIZADA }
        )
    }

    /** "En espera hace 2 d", "En espera hace 3 h", "Recién enviada". */
    fun waitingLabel(createdAtMillis: Long, nowMillis: Long): String {
        val minutes = ((nowMillis - createdAtMillis) / 60_000L).coerceAtLeast(0)
        return when {
            minutes < 60 -> "Recién enviada"
            minutes < 60 * 24 -> "En espera hace ${minutes / 60} h"
            else -> "En espera hace ${minutes / (60 * 24)} d"
        }
    }

    fun formatDate(millis: Long, zone: ZoneId = ZoneId.systemDefault()): String =
        dateFormatter.format(Instant.ofEpochMilli(millis).atZone(zone))
}
