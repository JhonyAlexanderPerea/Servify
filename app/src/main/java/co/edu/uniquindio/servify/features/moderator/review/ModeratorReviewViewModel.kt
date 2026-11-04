package co.edu.uniquindio.servify.features.moderator.review

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.edu.uniquindio.servify.domain.model.ModerationResult
import co.edu.uniquindio.servify.domain.model.Publication
import co.edu.uniquindio.servify.domain.model.RejectionReason
import co.edu.uniquindio.servify.domain.rules.ModerationRules
import co.edu.uniquindio.servify.domain.usecase.moderation.ApprovePublicationUseCase
import co.edu.uniquindio.servify.domain.usecase.moderation.FinalizePublicationByModeratorUseCase
import co.edu.uniquindio.servify.domain.usecase.moderation.RejectPublicationUseCase
import co.edu.uniquindio.servify.domain.usecase.publication.ObserveServicesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ModeratorReviewUiState(
    val isLoading: Boolean = true,
    val publication: Publication? = null,
    val showRejectSheet: Boolean = false,
    val rejectText: String = "",
    val rejectError: String? = null,
    val showFinalizeDialog: Boolean = false,
    val isProcessing: Boolean = false,
    val message: String? = null,
    /** true cuando la decisión se aplicó: la pantalla vuelve al panel. */
    val finished: Boolean = false
) {
    val canReview: Boolean get() = publication?.let { ModerationRules.canReview(it.status) } ?: false
    val canFinalize: Boolean get() = publication?.let { ModerationRules.canFinalize(it.status) } ?: false
    val isRejectValid: Boolean get() = ModerationRules.isValidRejectionReason(rejectText)
}

@HiltViewModel
class ModeratorReviewViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    observeServicesUseCase: ObserveServicesUseCase,
    private val approvePublicationUseCase: ApprovePublicationUseCase,
    private val rejectPublicationUseCase: RejectPublicationUseCase,
    private val finalizePublicationUseCase: FinalizePublicationByModeratorUseCase
) : ViewModel() {

    private val publicationId: String = savedStateHandle.get<String>("publicationId").orEmpty()

    private val _uiState = MutableStateFlow(ModeratorReviewUiState())
    val uiState: StateFlow<ModeratorReviewUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            observeServicesUseCase().collect { list ->
                _uiState.update {
                    it.copy(isLoading = false, publication = list.firstOrNull { p -> p.id == publicationId })
                }
            }
        }
    }

    fun onApprove() {
        runAction { approvePublicationUseCase(publicationId) }
    }

    fun onRejectClick() {
        _uiState.update { it.copy(showRejectSheet = true, rejectText = "", rejectError = null) }
    }

    fun onReasonSelected(reason: RejectionReason) {
        _uiState.update { it.copy(rejectText = reason.suggestedText, rejectError = null) }
    }

    fun onRejectTextChange(value: String) {
        _uiState.update { it.copy(rejectText = value, rejectError = null) }
    }

    fun onDismissRejectSheet() {
        _uiState.update { it.copy(showRejectSheet = false) }
    }

    fun onConfirmReject() {
        val text = _uiState.value.rejectText
        if (!ModerationRules.isValidRejectionReason(text)) {
            _uiState.update {
                it.copy(rejectError = "Escribe un motivo de al menos ${ModerationRules.MIN_REASON_LENGTH} caracteres")
            }
            return
        }
        _uiState.update { it.copy(showRejectSheet = false) }
        runAction { rejectPublicationUseCase(publicationId, text) }
    }

    fun onFinalizeClick() {
        _uiState.update { it.copy(showFinalizeDialog = true) }
    }

    fun onDismissFinalizeDialog() {
        _uiState.update { it.copy(showFinalizeDialog = false) }
    }

    fun onConfirmFinalize() {
        _uiState.update { it.copy(showFinalizeDialog = false) }
        runAction { finalizePublicationUseCase(publicationId) }
    }

    fun onMessageShown() {
        _uiState.update { it.copy(message = null) }
    }

    private fun runAction(action: suspend () -> ModerationResult) {
        if (_uiState.value.isProcessing) return
        _uiState.update { it.copy(isProcessing = true) }
        viewModelScope.launch {
            when (val result = action()) {
                ModerationResult.Success -> _uiState.update { it.copy(isProcessing = false, finished = true) }
                else -> _uiState.update { it.copy(isProcessing = false, message = result.toMessage()) }
            }
        }
    }
}

private fun ModerationResult.toMessage(): String = when (this) {
    ModerationResult.Success -> ""
    ModerationResult.NotFound -> "La publicación ya no existe"
    ModerationResult.ReasonRequired -> "El motivo del rechazo es obligatorio"
    is ModerationResult.NotAllowed -> "La publicación ya fue revisada (estado: ${status.name.lowercase()})"
}
