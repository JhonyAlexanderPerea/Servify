package co.edu.uniquindio.servify.features.moderator.panel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.edu.uniquindio.servify.domain.model.ModerationMetrics
import co.edu.uniquindio.servify.domain.model.Publication
import co.edu.uniquindio.servify.domain.rules.ModerationRules
import co.edu.uniquindio.servify.domain.usecase.auth.ObserveSessionUseCase
import co.edu.uniquindio.servify.domain.usecase.moderation.ObserveModerationMetricsUseCase
import co.edu.uniquindio.servify.domain.usecase.moderation.ObservePendingPublicationsUseCase
import co.edu.uniquindio.servify.domain.util.TimeProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

/** Tarjeta de la lista de pendientes (mockup 22). */
data class PendingItem(
    val id: String,
    val position: Int,
    val title: String,
    val category: String,
    val providerName: String,
    val location: String,
    val dateLabel: String,
    val waitingLabel: String
)

data class ModeratorPanelUiState(
    val isLoading: Boolean = true,
    val moderatorEmail: String = "",
    val metrics: ModerationMetrics = ModerationMetrics(),
    val pending: List<PendingItem> = emptyList()
)

/**
 * Panel de moderación: sesión + métricas + cola de pendientes. Es reactivo: cuando el moderador
 * verifica o rechaza una publicación, las métricas y la lista se actualizan solas.
 */
@HiltViewModel
class ModeratorPanelViewModel @Inject constructor(
    observeSessionUseCase: ObserveSessionUseCase,
    observePendingPublicationsUseCase: ObservePendingPublicationsUseCase,
    observeModerationMetricsUseCase: ObserveModerationMetricsUseCase,
    private val timeProvider: TimeProvider
) : ViewModel() {

    val uiState: StateFlow<ModeratorPanelUiState> = combine(
        observeSessionUseCase(),
        observePendingPublicationsUseCase(),
        observeModerationMetricsUseCase()
    ) { user, pending, metrics ->
        val now = timeProvider.now()
        ModeratorPanelUiState(
            isLoading = false,
            moderatorEmail = user?.email.orEmpty(),
            metrics = metrics,
            pending = pending.mapIndexed { index, publication ->
                publication.toPendingItem(position = index + 1, now = now)
            }
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = ModeratorPanelUiState()
    )
}

private fun Publication.toPendingItem(position: Int, now: Long): PendingItem {
    val categoryLabel = category.ifBlank { categoryId }
    return PendingItem(
        id = id,
        position = position,
        title = title,
        category = if (subcategory.isNotBlank()) "$categoryLabel > $subcategory" else categoryLabel,
        providerName = provider.name,
        location = city.ifBlank { locationName },
        dateLabel = ModerationRules.formatDate(createdAtMillis),
        waitingLabel = ModerationRules.waitingLabel(createdAtMillis, now)
    )
}
