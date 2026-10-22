package co.edu.uniquindio.servify.data.repository.memory

import co.edu.uniquindio.servify.domain.model.Notification
import co.edu.uniquindio.servify.domain.repository.NotificationRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NotificationRepositoryImpl @Inject constructor() : NotificationRepository {

    private val _notifications = MutableStateFlow(SampleData.notificationsList)

    override fun observeNotifications(): Flow<List<Notification>> = _notifications.asStateFlow()

    override suspend fun markAsRead(id: String) {
        _notifications.update { list ->
            list.map { if (it.id == id) it.copy(isRead = true) else it }
        }
    }
}
