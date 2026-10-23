package co.edu.uniquindio.servify.domain.usecase.notification

import co.edu.uniquindio.servify.domain.repository.NotificationRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

/**
 * Caso de uso para marcar todas las notificaciones como leídas.
 */
class MarkAllNotificationsReadUseCase @Inject constructor(
    private val notificationRepository: NotificationRepository
) {
    suspend operator fun invoke() = withContext(Dispatchers.IO) {
        notificationRepository.markAllAsRead()
    }
}
