package co.edu.uniquindio.servify.features.notifications

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.edu.uniquindio.servify.domain.model.Notification
import co.edu.uniquindio.servify.domain.model.NotificationType
import co.edu.uniquindio.servify.domain.usecase.notification.MarkAllNotificationsReadUseCase
import co.edu.uniquindio.servify.domain.usecase.notification.MarkNotificationReadUseCase
import co.edu.uniquindio.servify.domain.usecase.notification.ObserveNotificationsUseCase
import co.edu.uniquindio.servify.navigation.Screen
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class NotificationsUiState(
    val todayNotifications: List<Notification> = emptyList(),
    val earlierNotifications: List<Notification> = emptyList(),
    val unreadCount: Int = 0,
    val isLoading: Boolean = false,
    val userMessage: String? = null
)

@HiltViewModel
class NotificationsViewModel @Inject constructor(
    observeNotificationsUseCase: ObserveNotificationsUseCase,
    private val markNotificationReadUseCase: MarkNotificationReadUseCase,
    private val markAllNotificationsReadUseCase: MarkAllNotificationsReadUseCase
) : ViewModel() {

    val uiState: StateFlow<NotificationsUiState> = observeNotificationsUseCase().map { list ->
        val now = System.currentTimeMillis()
        val oneDayMillis = 86_400_000L

        val (today, earlier) = list.partition { (now - it.createdAt) <= oneDayMillis }
        val unread = list.count { !it.isRead }

        NotificationsUiState(
            todayNotifications = today,
            earlierNotifications = earlier,
            unreadCount = unread,
            isLoading = false
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = NotificationsUiState(isLoading = true)
    )

    fun onNotificationClicked(
        notification: Notification,
        onNavigateToRoute: (String) -> Unit
    ) {
        viewModelScope.launch {
            if (!notification.isRead) {
                markNotificationReadUseCase(notification.id)
            }

            val route = when (notification.type) {
                NotificationType.REQUEST_RECEIVED,
                NotificationType.REQUEST_ACCEPTED,
                NotificationType.APPOINTMENT_REMINDER -> {
                    notification.targetId?.let { Screen.ServiceDetail.createRoute(it) }
                        ?: Screen.Main.route
                }

                NotificationType.MODERATION_UPDATE -> {
                    notification.targetId?.let { Screen.ServiceDetail.createRoute(it) }
                        ?: Screen.Main.route
                }

                NotificationType.ACHIEVEMENT_UNLOCKED -> {
                    Screen.Profile.route
                }

                else -> Screen.Main.route
            }

            onNavigateToRoute(route)
        }
    }

    fun onMarkAllAsRead() {
        viewModelScope.launch {
            markAllNotificationsReadUseCase()
        }
    }
}
