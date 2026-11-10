package co.edu.uniquindio.servify.features.moderator.review

import androidx.lifecycle.SavedStateHandle
import co.edu.uniquindio.servify.domain.model.PublicationStatus
import co.edu.uniquindio.servify.domain.model.RejectionReason
import co.edu.uniquindio.servify.domain.rules.ModerationRules
import co.edu.uniquindio.servify.testutil.MainDispatcherRule
import co.edu.uniquindio.servify.testutil.TestEnvironment
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class ModeratorReviewViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val env = TestEnvironment()

    private fun newViewModel(publicationId: String) = ModeratorReviewViewModel(
        savedStateHandle = SavedStateHandle(mapOf("publicationId" to publicationId)),
        observeServicesUseCase = env.observeServices,
        approvePublicationUseCase = env.approvePublication,
        rejectPublicationUseCase = env.rejectPublication,
        finalizePublicationUseCase = env.finalizePublication
    )

    private fun awaitState(
        viewModel: ModeratorReviewViewModel,
        predicate: (ModeratorReviewUiState) -> Boolean
    ): ModeratorReviewUiState = runBlocking {
        withTimeout(5_000) { viewModel.uiState.first(predicate) }
    }

    @Test
    fun loads_the_requested_publication_and_enables_its_actions() {
        val state = awaitState(newViewModel("pub_jhony_tutorias")) { !it.isLoading }

        assertEquals("pub_jhony_tutorias", state.publication?.id)
        assertEquals(PublicationStatus.PENDIENTE, state.publication?.status)
        assertTrue(state.canReview)
        assertTrue(state.canFinalize)
    }

    @Test
    fun reports_an_unknown_publication_without_actions() {
        val state = awaitState(newViewModel("no_existe")) { !it.isLoading }

        assertNull(state.publication)
        assertFalse(state.canReview)
        assertFalse(state.canFinalize)
    }

    @Test
    fun approving_a_pending_publication_finishes_the_screen() {
        val viewModel = newViewModel("pub_jhony_tutorias")
        awaitState(viewModel) { !it.isLoading }

        viewModel.onApprove()

        val state = awaitState(viewModel) { it.finished }
        assertEquals(PublicationStatus.VERIFICADA, env.publications.getById("pub_jhony_tutorias")?.status)
        assertEquals(env.nowMillis, env.publications.getById("pub_jhony_tutorias")?.verifiedAtMillis)
        assertFalse(state.isProcessing)
    }

    @Test
    fun verify_action_requests_confirmation_and_can_be_dismissed_without_changes() {
        val viewModel = newViewModel("pub_jhony_tutorias")
        awaitState(viewModel) { !it.isLoading }

        viewModel.onApproveClick()

        assertTrue(viewModel.uiState.value.showApproveDialog)
        assertEquals(PublicationStatus.PENDIENTE, env.publications.getById("pub_jhony_tutorias")?.status)

        viewModel.onDismissApproveDialog()

        assertFalse(viewModel.uiState.value.showApproveDialog)
        assertEquals(PublicationStatus.PENDIENTE, env.publications.getById("pub_jhony_tutorias")?.status)
    }

    @Test
    fun confirming_verification_applies_the_approval() {
        val viewModel = newViewModel("pub_jhony_tutorias")
        awaitState(viewModel) { !it.isLoading }

        viewModel.onApproveClick()
        viewModel.onConfirmApprove()

        val state = awaitState(viewModel) { it.finished }
        assertFalse(state.showApproveDialog)
        assertEquals(PublicationStatus.VERIFICADA, env.publications.getById("pub_jhony_tutorias")?.status)
    }

    @Test
    fun approving_an_already_reviewed_publication_shows_a_message() {
        val viewModel = newViewModel("pub_jhony_plomeria")
        awaitState(viewModel) { !it.isLoading }

        viewModel.onApprove()

        val state = awaitState(viewModel) { it.message != null }
        assertEquals("La publicación ya fue revisada (estado: verificada)", state.message)
        assertFalse(state.finished)
        assertEquals(PublicationStatus.VERIFICADA, env.publications.getById("pub_jhony_plomeria")?.status)
    }

    @Test
    fun acting_on_a_missing_publication_shows_a_message() {
        val viewModel = newViewModel("no_existe")
        awaitState(viewModel) { !it.isLoading }

        viewModel.onApprove()

        val state = awaitState(viewModel) { it.message != null }
        assertEquals("La publicación ya no existe", state.message)
        assertFalse(state.finished)
    }

    @Test
    fun reject_flow_opens_empty_and_selection_fills_the_suggested_text() {
        val viewModel = newViewModel("pub_jhony_tutorias")
        awaitState(viewModel) { !it.isLoading }

        viewModel.onRejectClick()
        assertEquals(true, viewModel.uiState.value.showRejectSheet)
        assertEquals("", viewModel.uiState.value.rejectText)
        assertNull(viewModel.uiState.value.rejectError)

        viewModel.onReasonSelected(RejectionReason.INSUFFICIENT_INFO)

        assertEquals(RejectionReason.INSUFFICIENT_INFO.suggestedText, viewModel.uiState.value.rejectText)
        assertTrue(viewModel.uiState.value.isRejectValid)
        assertNull(viewModel.uiState.value.rejectError)
    }

    @Test
    fun reject_requires_a_valid_written_reason() {
        val viewModel = newViewModel("pub_jhony_tutorias")
        awaitState(viewModel) { !it.isLoading }
        viewModel.onRejectClick()
        viewModel.onRejectTextChange("corto")

        viewModel.onConfirmReject()

        val state = viewModel.uiState.value
        assertEquals(
            "Escribe un motivo de al menos ${ModerationRules.MIN_REASON_LENGTH} caracteres",
            state.rejectError
        )
        assertTrue(state.showRejectSheet)
        assertFalse(state.isRejectValid)
        assertEquals(PublicationStatus.PENDIENTE, env.publications.getById("pub_jhony_tutorias")?.status)
    }

    @Test
    fun selecting_a_reason_clears_the_previous_error() {
        val viewModel = newViewModel("pub_jhony_tutorias")
        awaitState(viewModel) { !it.isLoading }
        viewModel.onRejectClick()
        viewModel.onConfirmReject()

        viewModel.onReasonSelected(RejectionReason.WRONG_CATEGORY)

        assertNull(viewModel.uiState.value.rejectError)
        assertEquals(RejectionReason.WRONG_CATEGORY.suggestedText, viewModel.uiState.value.rejectText)
    }

    @Test
    fun confirming_a_valid_rejection_finishes_and_persists_the_trimmed_reason() {
        val viewModel = newViewModel("pub_jhony_tutorias")
        awaitState(viewModel) { !it.isLoading }
        viewModel.onRejectClick()
        viewModel.onRejectTextChange("  La categoría no corresponde al servicio.  ")

        viewModel.onConfirmReject()

        val state = awaitState(viewModel) { it.finished }
        assertFalse(state.showRejectSheet)
        val updated = env.publications.getById("pub_jhony_tutorias")!!
        assertEquals(PublicationStatus.RECHAZADA, updated.status)
        assertEquals("La categoría no corresponde al servicio.", updated.rejectionReason)
    }

    @Test
    fun dismissing_the_reject_sheet_keeps_the_publication_unchanged() {
        val viewModel = newViewModel("pub_jhony_tutorias")
        awaitState(viewModel) { !it.isLoading }
        viewModel.onRejectClick()

        viewModel.onDismissRejectSheet()

        assertFalse(viewModel.uiState.value.showRejectSheet)
        assertEquals(PublicationStatus.PENDIENTE, env.publications.getById("pub_jhony_tutorias")?.status)
    }

    @Test
    fun finalize_dialog_can_be_dismissed_without_changes() {
        val viewModel = newViewModel("pub_jhony_tutorias")
        awaitState(viewModel) { !it.isLoading }

        viewModel.onFinalizeClick()
        assertTrue(viewModel.uiState.value.showFinalizeDialog)

        viewModel.onDismissFinalizeDialog()

        assertFalse(viewModel.uiState.value.showFinalizeDialog)
        assertEquals(PublicationStatus.PENDIENTE, env.publications.getById("pub_jhony_tutorias")?.status)
    }

    @Test
    fun confirming_finalize_sets_the_finalized_status_and_timestamp() {
        val viewModel = newViewModel("pub_jhony_tutorias")
        awaitState(viewModel) { !it.isLoading }
        viewModel.onFinalizeClick()

        viewModel.onConfirmFinalize()

        val state = awaitState(viewModel) { it.finished }
        assertFalse(state.showFinalizeDialog)
        val updated = env.publications.getById("pub_jhony_tutorias")!!
        assertEquals(PublicationStatus.FINALIZADA, updated.status)
        assertEquals(env.nowMillis, updated.finalizedAtMillis)
    }

    @Test
    fun finalizing_an_already_finalized_publication_shows_a_message() {
        val viewModel = newViewModel("pub_jhony_computadores")
        awaitState(viewModel) { !it.isLoading }

        viewModel.onFinalizeClick()
        viewModel.onConfirmFinalize()

        val state = awaitState(viewModel) { it.message != null }
        assertEquals("La publicación ya fue revisada (estado: finalizada)", state.message)
        assertFalse(state.finished)
    }

    @Test
    fun the_message_is_consumed_once_shown() {
        val viewModel = newViewModel("pub_jhony_plomeria")
        awaitState(viewModel) { !it.isLoading }
        viewModel.onApprove()
        awaitState(viewModel) { it.message != null }

        viewModel.onMessageShown()

        assertNull(viewModel.uiState.value.message)
    }

    @Test
    fun reflects_changes_made_to_the_publication_elsewhere() {
        val viewModel = newViewModel("pub_jhony_tutorias")
        awaitState(viewModel) { !it.isLoading }

        runBlocking { env.finalizePublication("pub_jhony_tutorias") }

        val state = awaitState(viewModel) {
            it.publication?.status == PublicationStatus.FINALIZADA
        }
        assertFalse(state.canReview)
        assertFalse(state.canFinalize)
    }
}
