package co.edu.uniquindio.servify.features.moderator.panel

import co.edu.uniquindio.servify.domain.model.Publication
import co.edu.uniquindio.servify.domain.model.PublicationStatus
import co.edu.uniquindio.servify.domain.model.User
import co.edu.uniquindio.servify.domain.rules.ModerationRules
import co.edu.uniquindio.servify.testutil.MainDispatcherRule
import co.edu.uniquindio.servify.testutil.TestEnvironment
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class ModeratorPanelViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val env = TestEnvironment()

    private fun newViewModel(): ModeratorPanelViewModel = runBlocking {
        env.loginAs(TestEnvironment.MODERATOR_EMAIL)
        ModeratorPanelViewModel(
            observeSessionUseCase = env.observeSession,
            observePendingPublicationsUseCase = env.observePendingPublications,
            observeModerationMetricsUseCase = env.observeModerationMetrics,
            timeProvider = env.timeProvider,
            logoutUseCase = env.logout
        )
    }

    private fun awaitState(
        viewModel: ModeratorPanelViewModel,
        predicate: (ModeratorPanelUiState) -> Boolean
    ): ModeratorPanelUiState = runBlocking {
        withTimeout(5_000) { viewModel.uiState.first(predicate) }
    }

    @Test
    fun starts_loading_until_the_flows_are_collected() {
        assertTrue(newViewModel().uiState.value.isLoading)
    }

    @Test
    fun shows_the_moderator_email_and_the_current_metrics() {
        val state = awaitState(newViewModel()) { !it.isLoading }

        assertEquals(TestEnvironment.MODERATOR_EMAIL, state.moderatorEmail)
        assertEquals(2, state.metrics.pending)
        assertEquals(0, state.metrics.verifiedToday)
        assertEquals(1, state.metrics.rejected)
        assertEquals(1, state.metrics.finalized)
    }

    @Test
    fun maps_the_pending_queue_with_positions_and_display_labels() {
        val state = awaitState(newViewModel()) { !it.isLoading }

        assertEquals(listOf("pub_valentina_soporte", "pub_jhony_tutorias"), state.pending.map { it.id })
        assertEquals(listOf(1, 2), state.pending.map { it.position })

        val first = state.pending.first()
        val publication = env.publicationById(first.id)!!
        assertEquals(publication.title, first.title)
        assertEquals("Tecnología > Soporte técnico", first.category)
        assertEquals(publication.provider.name, first.providerName)
        assertEquals(publication.city, first.location)
        assertEquals(ModerationRules.formatDate(publication.createdAtMillis), first.dateLabel)
        assertEquals(
            ModerationRules.waitingLabel(publication.createdAtMillis, env.nowMillis),
            first.waitingLabel
        )
    }

    @Test
    fun uses_category_id_and_location_name_when_the_labels_are_blank() {
        env.addPublication(
            Publication(
                id = "custom_pending",
                title = "Servicio sin etiquetas",
                description = "Descripción",
                categoryId = "cat_1",
                category = "",
                provider = User(id = "provider_1", name = "Proveedor Uno", email = ""),
                authorId = "provider_1",
                status = PublicationStatus.PENDIENTE,
                locationName = "Armenia",
                city = "",
                createdAtMillis = env.nowMillis
            )
        )

        val state = awaitState(newViewModel()) { !it.isLoading }
        val item = state.pending.first { it.id == "custom_pending" }

        assertEquals("cat_1", item.category)
        assertEquals("Armenia", item.location)
        assertEquals("Recién enviada", item.waitingLabel)
    }

    @Test
    fun updates_by_itself_when_a_publication_is_approved() {
        val viewModel = newViewModel()
        awaitState(viewModel) { !it.isLoading }

        runBlocking { env.approvePublication("pub_jhony_tutorias") }

        val state = awaitState(viewModel) { it.metrics.pending == 1 }
        assertEquals(1, state.metrics.verifiedToday)
        assertFalse(state.pending.any { it.id == "pub_jhony_tutorias" })
        assertEquals(listOf("pub_valentina_soporte"), state.pending.map { it.id })
    }

    @Test
    fun shows_an_empty_queue_when_there_are_no_pending_publications() {
        val viewModel = newViewModel()
        awaitState(viewModel) { !it.isLoading }

        runBlocking {
            env.rejectPublication("pub_jhony_tutorias", "Motivo suficientemente largo")
            env.rejectPublication("pub_valentina_soporte", "Motivo suficientemente largo")
        }

        val state = awaitState(viewModel) { it.metrics.pending == 0 }
        assertTrue(state.pending.isEmpty())
    }

    @Test
    fun works_without_a_session_and_keeps_the_queue_visible() {
        val viewModel = ModeratorPanelViewModel(
            observeSessionUseCase = env.observeSession,
            observePendingPublicationsUseCase = env.observePendingPublications,
            observeModerationMetricsUseCase = env.observeModerationMetrics,
            timeProvider = env.timeProvider,
            logoutUseCase = env.logout
        )

        val state = awaitState(viewModel) { !it.isLoading }

        assertEquals("", state.moderatorEmail)
        assertEquals(2, state.pending.size)
    }

    @Test
    fun logout_clears_the_session() {
        val viewModel = newViewModel()

        viewModel.logout()

        assertEquals(null, runBlocking { env.session.session.first() })
        assertEquals(null, env.auth.currentUserSnapshot)
    }
}
