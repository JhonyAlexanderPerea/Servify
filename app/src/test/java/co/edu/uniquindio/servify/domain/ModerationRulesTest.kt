package co.edu.uniquindio.servify.domain

import co.edu.uniquindio.servify.domain.model.Publication
import co.edu.uniquindio.servify.domain.model.PublicationStatus
import co.edu.uniquindio.servify.domain.rules.ModerationRules
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.ZoneOffset

class ModerationRulesTest {

    private val day = 86_400_000L
    private val today = 1_700_000_000_000L

    private fun publication(
        id: String,
        status: PublicationStatus,
        createdAtMillis: Long = today,
        verifiedAtMillis: Long? = null,
        finalizedAtMillis: Long? = null
    ) = Publication(
        id = id,
        title = id,
        description = id,
        status = status,
        createdAtMillis = createdAtMillis,
        verifiedAtMillis = verifiedAtMillis,
        finalizedAtMillis = finalizedAtMillis
    )

    @Test
    fun only_pending_publications_can_be_reviewed() {
        PublicationStatus.entries.forEach { status ->
            assertEquals(status == PublicationStatus.PENDIENTE, ModerationRules.canReview(status))
        }
    }

    @Test
    fun every_status_except_finalized_can_be_finalized_by_a_moderator() {
        PublicationStatus.entries.forEach { status ->
            assertEquals(status != PublicationStatus.FINALIZADA, ModerationRules.canFinalize(status))
        }
    }

    @Test
    fun rejection_reason_requires_at_least_ten_visible_characters() {
        assertFalse(ModerationRules.isValidRejectionReason(""))
        assertFalse(ModerationRules.isValidRejectionReason("   corto   "))
        assertFalse(ModerationRules.isValidRejectionReason("a".repeat(ModerationRules.MIN_REASON_LENGTH - 1)))
        assertTrue(ModerationRules.isValidRejectionReason("a".repeat(ModerationRules.MIN_REASON_LENGTH)))
        assertTrue(ModerationRules.isValidRejectionReason("  motivo válido  "))
    }

    @Test
    fun pending_queue_filters_and_sorts_by_creation_time() {
        val newest = publication("newest", PublicationStatus.PENDIENTE, createdAtMillis = today + day)
        val oldest = publication("oldest", PublicationStatus.PENDIENTE, createdAtMillis = today - day)
        val middle = publication("middle", PublicationStatus.PENDIENTE, createdAtMillis = today)
        val others = listOf(
            publication("verified", PublicationStatus.VERIFICADA),
            publication("rejected", PublicationStatus.RECHAZADA),
            publication("finalized", PublicationStatus.FINALIZADA)
        )

        val queue = ModerationRules.pendingQueue(listOf(newest, oldest, middle) + others)

        assertEquals(listOf(oldest, middle, newest), queue)
    }

    @Test
    fun metrics_count_each_status_and_only_verifications_from_today() {
        val publications = listOf(
            publication("pending-1", PublicationStatus.PENDIENTE),
            publication("pending-2", PublicationStatus.PENDIENTE),
            publication("verified-today", PublicationStatus.VERIFICADA, verifiedAtMillis = today),
            publication("verified-yesterday", PublicationStatus.VERIFICADA, verifiedAtMillis = today - day),
            publication("rejected", PublicationStatus.RECHAZADA),
            publication("finalized", PublicationStatus.FINALIZADA)
        )

        val metrics = ModerationRules.metrics(publications, today, ZoneOffset.UTC)

        assertEquals(2, metrics.pending)
        assertEquals(1, metrics.verifiedToday)
        assertEquals(1, metrics.rejected)
        assertEquals(1, metrics.finalized)
    }

    @Test
    fun metrics_are_zero_without_publications() {
        val metrics = ModerationRules.metrics(emptyList(), today, ZoneOffset.UTC)

        assertEquals(0, metrics.pending)
        assertEquals(0, metrics.verifiedToday)
        assertEquals(0, metrics.rejected)
        assertEquals(0, metrics.finalized)
    }

    @Test
    fun waiting_label_changes_from_recent_to_hours_to_days() {
        assertEquals("Recién enviada", ModerationRules.waitingLabel(today - 30 * 60_000L, today))
        assertEquals("Recién enviada", ModerationRules.waitingLabel(today + day, today))
        assertEquals("En espera hace 1 h", ModerationRules.waitingLabel(today - 60 * 60_000L, today))
        assertEquals("En espera hace 3 h", ModerationRules.waitingLabel(today - 3 * 60 * 60_000L, today))
        assertEquals("En espera hace 23 h", ModerationRules.waitingLabel(today - 23 * 60 * 60_000L, today))
        assertEquals("En espera hace 1 d", ModerationRules.waitingLabel(today - day, today))
        assertEquals("En espera hace 5 d", ModerationRules.waitingLabel(today - 5 * day, today))
    }

    @Test
    fun format_date_uses_day_month_year() {
        val millis = java.time.Instant.parse("2026-11-04T15:30:00Z").toEpochMilli()

        assertEquals("04/11/2026", ModerationRules.formatDate(millis, ZoneOffset.UTC))
    }
}
