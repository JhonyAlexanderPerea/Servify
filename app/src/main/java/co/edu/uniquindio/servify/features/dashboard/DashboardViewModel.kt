package co.edu.uniquindio.servify.features.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.edu.uniquindio.servify.domain.usecase.notification.ObserveNotificationsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

/** Estado del dashboard: el contador de la barra inferior sale de las notificaciones reales. */
@HiltViewModel
class DashboardViewModel @Inject constructor(
    observeNotificationsUseCase: ObserveNotificationsUseCase
) : ViewModel() {

    val unreadCount: StateFlow<Int> = observeNotificationsUseCase()
        .map { list -> list.count { !it.isRead } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), 0)
}
