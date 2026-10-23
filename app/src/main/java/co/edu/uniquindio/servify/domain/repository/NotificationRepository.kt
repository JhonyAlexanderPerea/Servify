package co.edu.uniquindio.servify.domain.repository

import co.edu.uniquindio.servify.domain.model.Notification
import kotlinx.coroutines.flow.Flow

/**
 * Contrato de repositorio para la gestión de notificaciones y alertas.
 */
interface NotificationRepository {
    fun observeNotifications(): Flow<List<Notification>>
    suspend fun addNotification(notification: Notification)
    suspend fun markAsRead(id: String)
}
