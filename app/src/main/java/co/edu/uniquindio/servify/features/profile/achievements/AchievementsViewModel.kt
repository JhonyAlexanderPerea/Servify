package co.edu.uniquindio.servify.features.profile.achievements

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.edu.uniquindio.servify.domain.usecase.auth.ObserveSessionUseCase
import co.edu.uniquindio.servify.domain.usecase.reputation.ObserveAchievementsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

/**
 * Logros del usuario en sesión. Es reactivo: si gana puntos o desbloquea una insignia
 * (por ejemplo, cuando un moderador verifica su publicación) la pantalla se actualiza sola.
 */
@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class AchievementsViewModel @Inject constructor(
    observeSessionUseCase: ObserveSessionUseCase,
    observeAchievementsUseCase: ObserveAchievementsUseCase
) : ViewModel() {

    val uiState: StateFlow<AchievementsUiState> = observeSessionUseCase()
        .filterNotNull()
        .map { it.id }
        .distinctUntilChanged()
        .flatMapLatest { userId -> observeAchievementsUseCase(userId) }
        .map { badges -> AchievementsUiState(isLoading = false, badges = badges) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = AchievementsUiState()
        )
}