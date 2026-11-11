package co.edu.uniquindio.servify.features.publication.mine

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.edu.uniquindio.servify.domain.model.Publication
import co.edu.uniquindio.servify.domain.model.PublicationActionResult
import co.edu.uniquindio.servify.domain.rules.PublicationAction
import co.edu.uniquindio.servify.domain.rules.PublicationTab
import co.edu.uniquindio.servify.domain.usecase.auth.ObserveSessionUseCase
import co.edu.uniquindio.servify.domain.usecase.publication.DeletePublicationUseCase
import co.edu.uniquindio.servify.domain.usecase.publication.MarkPublicationCompletedUseCase
import co.edu.uniquindio.servify.domain.usecase.publication.ObserveMyPublicationsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Pantalla 15 "Mis publicaciones".
 *
 * Ver y Editar son navegación (los resuelve la pantalla con callbacks). Eliminar y Finalizar piden
 * confirmación: [onActionRequested] deja la acción pendiente y [onConfirmPending] la ejecuta con el
 * caso de uso correspondiente. Los permisos y transiciones viven en el dominio, no aquí.
 */
@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class MyPublicationsViewModel @Inject constructor(
    observeSessionUseCase: ObserveSessionUseCase,
    observeMyPublicationsUseCase: ObserveMyPublicationsUseCase,
    private val deletePublicationUseCase: DeletePublicationUseCase,
    private val markPublicationCompletedUseCase: MarkPublicationCompletedUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(MyPublicationsUiState())

    val uiState: StateFlow<MyPublicationsUiState> = _uiState.asStateFlow()

    private var currentUserId: String? = null

    init {
        viewModelScope.launch {
            observeSessionUseCase()
                .filterNotNull()
                .map { it.id }
                .distinctUntilChanged()
                .flatMapLatest { userId ->
                    currentUserId = userId
                    observeMyPublicationsUseCase(userId)
                }
                .collect { list -> _uiState.update { it.copy(isLoading = false, publications = list) } }
        }
    }

    fun onTabSelected(tab: PublicationTab) {
        _uiState.update { it.copy(selectedTab = tab) }
    }

    /** Solo Eliminar y Finalizar piden confirmación; el resto de acciones no pasan por aquí. */
    fun onActionRequested(action: PublicationAction, publication: Publication) {
        if (action != PublicationAction.ELIMINAR && action != PublicationAction.FINALIZAR) return
        _uiState.update {
            it.copy(pendingConfirmation = PendingConfirmation(action, publication.id, publication.title))
        }
    }

    fun onDismissPending() {
        _uiState.update { it.copy(pendingConfirmation = null) }
    }

    fun onConfirmPending() {
        val pending = _uiState.value.pendingConfirmation ?: return
        val userId = currentUserId ?: return
        _uiState.update { it.copy(pendingConfirmation = null) }

        val result = when (pending.action) {
            PublicationAction.ELIMINAR -> deletePublicationUseCase(userId, pending.publicationId)
            PublicationAction.FINALIZAR -> markPublicationCompletedUseCase(userId, pending.publicationId)
            else -> return
        }
        _uiState.update { it.copy(message = messageFor(pending, result)) }
    }

    fun onMessageShown() {
        _uiState.update { it.copy(message = null) }
    }

    private fun messageFor(pending: PendingConfirmation, result: PublicationActionResult): String =
        when (result) {
            PublicationActionResult.Success -> when (pending.action) {
                PublicationAction.ELIMINAR -> "Publicación eliminada"
                else -> "Publicación finalizada"
            }

            PublicationActionResult.NotFound -> "Esa publicación ya no existe"
            PublicationActionResult.NotOwner -> "Solo puedes modificar tus propias publicaciones"
            is PublicationActionResult.NotAllowed -> "Esa acción no está disponible para esta publicación"
        }
}
