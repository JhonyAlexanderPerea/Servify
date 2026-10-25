package co.edu.uniquindio.servify.domain

import co.edu.uniquindio.servify.data.seed.SeedPublications
import co.edu.uniquindio.servify.data.seed.SeedUsers
import co.edu.uniquindio.servify.domain.model.PublicationStatus
import co.edu.uniquindio.servify.domain.rules.PublicationAction
import co.edu.uniquindio.servify.domain.rules.PublicationRules
import co.edu.uniquindio.servify.domain.rules.PublicationTab
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class PublicationRulesTest {

    private val mine = SeedPublications.publications.filter { it.authorId == SeedUsers.JHONY_ID }

    // ----- Filtro por pestaña (Mis publicaciones) -----

    @Test
    fun all_tab_shows_everything() {
        assertEquals(mine.size, PublicationRules.filter(PublicationTab.TODAS, mine).size)
    }

    @Test
    fun each_tab_only_shows_its_status() {
        mapOf(
            PublicationTab.PENDIENTES to PublicationStatus.PENDIENTE,
            PublicationTab.ACTIVAS to PublicationStatus.VERIFICADA,
            PublicationTab.FINALIZADAS to PublicationStatus.FINALIZADA,
            PublicationTab.RECHAZADAS to PublicationStatus.RECHAZADA
        ).forEach { (tab, status) ->
            val shown = PublicationRules.filter(tab, mine)
            assertTrue("La pestaña $tab está vacía con los datos semilla", shown.isNotEmpty())
            assertTrue(shown.all { it.status == status })
        }
    }

    @Test
    fun counts_match_the_seed_of_the_test_account() {
        val counts = PublicationRules.counts(mine)

        assertEquals(5, counts[PublicationTab.TODAS])
        assertEquals(1, counts[PublicationTab.PENDIENTES])
        assertEquals(2, counts[PublicationTab.ACTIVAS])
        assertEquals(1, counts[PublicationTab.FINALIZADAS])
        assertEquals(1, counts[PublicationTab.RECHAZADAS])
    }

    @Test
    fun the_tab_counts_add_up_to_the_total() {
        val counts = PublicationRules.counts(mine)
        val perStatus = PublicationTab.entries.filter { it != PublicationTab.TODAS }.sumOf { counts.getValue(it) }

        assertEquals(counts[PublicationTab.TODAS], perStatus)
    }

    // ----- Transiciones de estado -----

    @Test
    fun only_pending_and_rejected_can_be_edited() {
        assertTrue(PublicationRules.canEdit(PublicationStatus.PENDIENTE))
        assertTrue(PublicationRules.canEdit(PublicationStatus.RECHAZADA))
        assertFalse(PublicationRules.canEdit(PublicationStatus.VERIFICADA))
        assertFalse(PublicationRules.canEdit(PublicationStatus.FINALIZADA))
    }

    @Test
    fun only_a_verified_publication_can_be_finalized() {
        assertTrue(PublicationRules.canFinalize(PublicationStatus.VERIFICADA))
        assertFalse(PublicationRules.canFinalize(PublicationStatus.PENDIENTE))
        assertFalse(PublicationRules.canFinalize(PublicationStatus.RECHAZADA))
        assertFalse(PublicationRules.canFinalize(PublicationStatus.FINALIZADA))
    }

    @Test
    fun the_author_can_delete_in_any_status() {
        PublicationStatus.entries.forEach { assertTrue(PublicationRules.canDelete(it)) }
    }

    @Test
    fun menu_actions_depend_on_the_status() {
        assertEquals(
            listOf(PublicationAction.VER, PublicationAction.EDITAR, PublicationAction.ELIMINAR),
            PublicationRules.actionsFor(PublicationStatus.PENDIENTE)
        )
        assertEquals(
            listOf(PublicationAction.VER, PublicationAction.EDITAR, PublicationAction.ELIMINAR),
            PublicationRules.actionsFor(PublicationStatus.RECHAZADA)
        )
        assertEquals(
            listOf(PublicationAction.VER, PublicationAction.FINALIZAR, PublicationAction.ELIMINAR),
            PublicationRules.actionsFor(PublicationStatus.VERIFICADA)
        )
        assertEquals(
            listOf(PublicationAction.VER, PublicationAction.ELIMINAR),
            PublicationRules.actionsFor(PublicationStatus.FINALIZADA)
        )
    }
}
