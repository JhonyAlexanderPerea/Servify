package co.edu.uniquindio.servify.navigation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.edu.uniquindio.servify.domain.model.UserSession
import co.edu.uniquindio.servify.domain.usecase.auth.ObserveUserSessionUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

// Define los posibles estados de la sesión
sealed interface SessionState {
    data object Loading : SessionState
    data object NotAuthenticated : SessionState
    data class Authenticated(val session: UserSession) : SessionState
}

@HiltViewModel
class SessionViewModel @Inject constructor(
    observeUserSessionUseCase: ObserveUserSessionUseCase
) : ViewModel() {

    val sessionState: StateFlow<SessionState> = observeUserSessionUseCase()
        .map { session ->
            if (session != null) SessionState.Authenticated(session) else SessionState.NotAuthenticated
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = SessionState.Loading
        )
}
