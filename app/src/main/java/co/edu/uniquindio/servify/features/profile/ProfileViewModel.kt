package co.edu.uniquindio.servify.features.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.edu.uniquindio.servify.domain.usecase.auth.ObserveSessionUseCase
import co.edu.uniquindio.servify.domain.usecase.publication.ObserveMyPublicationsUseCase
import co.edu.uniquindio.servify.domain.usecase.reputation.ObserveUserProgressUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Perfil con datos reales: sesión + progreso de reputación + publicaciones propias.
 * Es reactivo: si el usuario gana puntos o finaliza una publicación, la pantalla se actualiza sola.
 */
@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class ProfileViewModel @Inject constructor(
    observeSessionUseCase: ObserveSessionUseCase,
    observeUserProgressUseCase: ObserveUserProgressUseCase,
    observeMyPublicationsUseCase: ObserveMyPublicationsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())

    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            observeSessionUseCase()
                .filterNotNull()
                .map { it.id }
                .distinctUntilChanged()
                .flatMapLatest { userId ->
                    combine(
                        observeUserProgressUseCase(userId),
                        observeMyPublicationsUseCase(userId)
                    ) { progress, publications -> progress?.let { toProfileUiState(it, publications) } }
                }
                .filterNotNull()
                .collect { state -> _uiState.value = state }
        }
    }
}
