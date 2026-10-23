package co.edu.uniquindio.servify.domain.usecase.notification

import co.edu.uniquindio.servify.domain.model.Notification
import co.edu.uniquindio.servify.domain.repository.NotificationRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Caso de uso para observar en tiempo real el flujo de notificaciones del usuario.
 */
class ObserveNotificationsUseCase @Inject constructor(
    private val notificationRepository: NotificationRepository
) {
    operator fun invoke(): Flow<List<Notification>> = notificationRepository.observeNotifications()
}
