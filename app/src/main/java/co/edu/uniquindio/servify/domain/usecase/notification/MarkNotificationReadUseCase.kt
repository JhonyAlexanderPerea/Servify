package co.edu.uniquindio.servify.domain.usecase.notification

import co.edu.uniquindio.servify.domain.repository.NotificationRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

/**
 * Caso de uso para marcar una notificación individual como leída.
 */
class MarkNotificationReadUseCase @Inject constructor(
    private val notificationRepository: NotificationRepository
) {
    suspend operator fun invoke(id: String) = withContext(Dispatchers.IO) {
        notificationRepository.markAsRead(id)
    }
}
