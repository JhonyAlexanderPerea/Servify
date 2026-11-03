package co.edu.uniquindio.servify.domain.usecase.achievement

import co.edu.uniquindio.servify.domain.repository.UserRepository
import co.edu.uniquindio.servify.domain.usecase.notification.GenerateNotificationUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import javax.inject.Inject

/**
 * Caso de uso para otorgar insignias y puntos de reputación a un usuario.
 * Emite automáticamente la notificación [ACHIEVEMENT_UNLOCKED].
 */
class UnlockAchievementUseCase @Inject constructor(
    private val userRepository: UserRepository,
    private val generateNotificationUseCase: GenerateNotificationUseCase
) {

    suspend operator fun invoke(
        badgeId: String,
        badgeName: String,
        pointsAwarded: Int = 100
    ) = withContext(Dispatchers.IO) {
        val currentUser = userRepository.observeCurrentUser().first()

        userRepository.updatePoints(pointsAwarded)

        generateNotificationUseCase.generateAchievementUnlockedNotification(
            userId = currentUser.id,
            badgeName = badgeName,
            badgeId = badgeId
        )
    }
}
