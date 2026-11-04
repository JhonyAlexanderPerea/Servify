package co.edu.uniquindio.servify.domain

import co.edu.uniquindio.servify.domain.model.RejectionReason
import co.edu.uniquindio.servify.domain.rules.ModerationRules
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class RejectionReasonTest {

    @Test
    fun every_reason_has_a_label_and_a_suggested_text() {
        RejectionReason.entries.forEach { reason ->
            assertTrue(reason.label.isNotBlank())
            assertTrue(reason.suggestedText.isNotBlank())
        }
    }

    @Test
    fun every_suggested_text_is_a_valid_rejection_reason() {
        RejectionReason.entries.forEach { reason ->
            assertTrue(ModerationRules.isValidRejectionReason(reason.suggestedText))
        }
    }

    @Test
    fun reasons_have_distinct_labels_and_suggested_texts() {
        assertEquals(
            RejectionReason.entries.size,
            RejectionReason.entries.map { it.label }.toSet().size
        )
        assertEquals(
            RejectionReason.entries.size,
            RejectionReason.entries.map { it.suggestedText }.toSet().size
        )
    }
}
