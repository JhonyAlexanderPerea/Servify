package co.edu.uniquindio.servify.domain

import co.edu.uniquindio.servify.domain.model.Publication
import co.edu.uniquindio.servify.domain.model.PublicationStatus
import co.edu.uniquindio.servify.domain.rules.FeedRules
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class FeedRulesTest {

    private fun publication(id: String, status: PublicationStatus) =
        Publication(id = id, status = status)

    @Test
    fun `las verificadas son publicas`() {
        assertTrue(FeedRules.isPublic(PublicationStatus.VERIFICADA))
        assertTrue(FeedRules.isPublic(PublicationStatus.APPROVED))
    }

    @Test
    fun `pendientes rechazadas y finalizadas no aparecen en el feed`() {
        assertFalse(FeedRules.isPublic(PublicationStatus.PENDIENTE))
        assertFalse(FeedRules.isPublic(PublicationStatus.RECHAZADA))
        assertFalse(FeedRules.isPublic(PublicationStatus.FINALIZADA))
    }

    @Test
    fun `publicFeed conserva solo las verificadas y respeta el orden`() {
        val all = listOf(
            publication("a", PublicationStatus.VERIFICADA),
            publication("b", PublicationStatus.PENDIENTE),
            publication("c", PublicationStatus.RECHAZADA),
            publication("d", PublicationStatus.VERIFICADA),
            publication("e", PublicationStatus.FINALIZADA)
        )
        assertEquals(listOf("a", "d"), FeedRules.publicFeed(all).map { it.id })
    }
}
