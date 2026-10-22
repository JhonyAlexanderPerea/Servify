package co.edu.uniquindio.servify.features.moderator.panel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.edu.uniquindio.servify.di.AppContainer
import co.edu.uniquindio.servify.domain.usecase.auth.ObserveSessionUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ModeratorPanelUiState(
    val moderatorEmail: String = "",
    val isLoading: Boolean = true
)

/**
 * Estado del panel de moderación.
 *
 * La pantalla NO lee el repositorio: observa la sesión a través del caso de uso
 * [ObserveSessionUseCase], igual que cualquier otra feature.
 *
 * Cuando existan los repositorios de moderación (tarea 28) este ViewModel crecerá
 * con las métricas del día y la lista de pendientes; por ahora solo expone quién
 * está en sesión.
 */
class ModeratorPanelViewModel(
    private val observeSessionUseCase: ObserveSessionUseCase = AppContainer.observeSessionUseCase
) : ViewModel() {

    private val _uiState =
        MutableStateFlow(ModeratorPanelUiState())

    val uiState: StateFlow<ModeratorPanelUiState> =
        _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            observeSessionUseCase().collect { user ->
                _uiState.update {
                    it.copy(
                        moderatorEmail = user?.email.orEmpty(),
                        isLoading = false
                    )
                }
            }
        }
    }
}
