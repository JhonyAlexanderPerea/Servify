package co.edu.uniquindio.servify.features.publication

import co.edu.uniquindio.servify.domain.model.PublicationStatus
import co.edu.uniquindio.servify.domain.rules.PublicationAction
import co.edu.uniquindio.servify.domain.rules.PublicationTab
import co.edu.uniquindio.servify.features.publication.mine.MyPublicationsViewModel
import co.edu.uniquindio.servify.testutil.MainDispatcherRule
import co.edu.uniquindio.servify.testutil.TestEnvironment
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class MyPublicationsViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val env = TestEnvironment(nowMillis = 1_900_000_000_000L)

    /** Inicia sesión con la cuenta de pruebas (publicaciones en los 4 estados) y crea el ViewModel. */
    private fun jhonyViewModel(): MyPublicationsViewModel = runBlocking {
        env.loginAs(TestEnvironment.JHONY_EMAIL)
        MyPublicationsViewModel(env.observeSession, env.observeMyPublications, env.deletePublication, env.markCompleted)
    }

    @Test
    fun loads_only_the_logged_users_publications() {
        val state = jhonyViewModel().uiState.value

        assertEquals(false, state.isLoading)
        assertEquals(5, state.publications.size)
        assertEquals(PublicationTab.TODAS, state.selectedTab)
    }

    @Test
    fun a_user_without_publications_sees_the_empty_state() = runBlocking {
        env.loginAs("luis.torres@servify.demo")
        env.deletePublication("user_luis", "pub_luis_limpieza")
        val vm = MyPublicationsViewModel(env.observeSession, env.observeMyPublications, env.deletePublication, env.markCompleted)

        assertTrue(vm.uiState.value.isEmpty)
    }

    @Test
    fun selecting_a_tab_filters_the_visible_publications() {
        val vm = jhonyViewModel()

        vm.onTabSelected(PublicationTab.RECHAZADAS)
        assertEquals(listOf(PublicationStatus.RECHAZADA), vm.uiState.value.visiblePublications.map { it.status })

        vm.onTabSelected(PublicationTab.ACTIVAS)
        assertEquals(2, vm.uiState.value.visiblePublications.size)

        vm.onTabSelected(PublicationTab.TODAS)
        assertEquals(5, vm.uiState.value.visiblePublications.size)
    }

    @Test
    fun counts_per_tab_are_exposed_for_the_tab_labels() {
        val counts = jhonyViewModel().uiState.value.counts

        assertEquals(5, counts[PublicationTab.TODAS])
        assertEquals(1, counts[PublicationTab.PENDIENTES])
        assertEquals(2, counts[PublicationTab.ACTIVAS])
    }

    @Test
    fun delete_asks_for_confirmation_before_doing_anything() {
        val vm = jhonyViewModel()
        val target = vm.uiState.value.publications.first { it.id == "pub_jhony_paseo" }

        vm.onActionRequested(PublicationAction.ELIMINAR, target)

        assertEquals("pub_jhony_paseo", vm.uiState.value.pendingConfirmation?.publicationId)
        assertNotNull(env.publications.getById("pub_jhony_paseo"))
    }

    @Test
    fun confirming_a_delete_removes_the_publication_and_reports_it() {
        val vm = jhonyViewModel()
        val target = vm.uiState.value.publications.first { it.id == "pub_jhony_paseo" }

        vm.onActionRequested(PublicationAction.ELIMINAR, target)
        vm.onConfirmPending()

        assertNull(env.publications.getById("pub_jhony_paseo"))
        assertEquals(4, vm.uiState.value.publications.size)
        assertNull(vm.uiState.value.pendingConfirmation)
        assertEquals("Publicación eliminada", vm.uiState.value.message)
    }

    @Test
    fun cancelling_the_dialog_changes_nothing() {
        val vm = jhonyViewModel()
        val target = vm.uiState.value.publications.first { it.id == "pub_jhony_paseo" }

        vm.onActionRequested(PublicationAction.ELIMINAR, target)
        vm.onDismissPending()

        assertNull(vm.uiState.value.pendingConfirmation)
        assertNotNull(env.publications.getById("pub_jhony_paseo"))
        assertNull(vm.uiState.value.message)
    }

    @Test
    fun finalizing_a_verified_publication_moves_it_to_the_finalized_tab() {
        val vm = jhonyViewModel()
        val target = vm.uiState.value.publications.first { it.id == "pub_jhony_plomeria" }

        vm.onActionRequested(PublicationAction.FINALIZAR, target)
        vm.onConfirmPending()

        vm.onTabSelected(PublicationTab.FINALIZADAS)
        assertEquals("Publicación finalizada", vm.uiState.value.message)
        assertEquals(2, vm.uiState.value.visiblePublications.size)
        assertEquals(1, vm.uiState.value.counts[PublicationTab.ACTIVAS])
    }

    @Test
    fun finalizing_a_pending_publication_is_refused_by_the_domain() {
        val vm = jhonyViewModel()
        val target = vm.uiState.value.publications.first { it.id == "pub_jhony_tutorias" }

        vm.onActionRequested(PublicationAction.FINALIZAR, target)
        vm.onConfirmPending()

        assertEquals(PublicationStatus.PENDIENTE, env.publications.getById("pub_jhony_tutorias")?.status)
        assertEquals("Esa acción no está disponible para esta publicación", vm.uiState.value.message)
    }

    @Test
    fun view_and_edit_are_navigation_and_never_ask_for_confirmation() {
        val vm = jhonyViewModel()
        val target = vm.uiState.value.publications.first()

        vm.onActionRequested(PublicationAction.VER, target)
        vm.onActionRequested(PublicationAction.EDITAR, target)

        assertNull(vm.uiState.value.pendingConfirmation)
    }

    @Test
    fun the_message_is_consumed_once_shown() {
        val vm = jhonyViewModel()
        val target = vm.uiState.value.publications.first { it.id == "pub_jhony_paseo" }
        vm.onActionRequested(PublicationAction.ELIMINAR, target)
        vm.onConfirmPending()

        vm.onMessageShown()

        assertNull(vm.uiState.value.message)
    }

    @Test
    fun the_rejected_publication_exposes_its_reason_for_the_banner() {
        val vm = jhonyViewModel()
        vm.onTabSelected(PublicationTab.RECHAZADAS)

        val rejected = vm.uiState.value.visiblePublications.single()
        assertEquals("La descripción no contiene información suficiente sobre el servicio.", rejected.rejectionReason)
    }
}
