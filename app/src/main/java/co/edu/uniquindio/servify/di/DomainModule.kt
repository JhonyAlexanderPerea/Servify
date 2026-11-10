package co.edu.uniquindio.servify.di

import co.edu.uniquindio.servify.domain.repository.CommentRepository
import co.edu.uniquindio.servify.domain.repository.PublicationRepository
import co.edu.uniquindio.servify.domain.repository.UserRepository
import co.edu.uniquindio.servify.domain.service.GamificationService
import co.edu.uniquindio.servify.domain.usecase.notification.GenerateNotificationUseCase
import co.edu.uniquindio.servify.domain.util.TimeProvider
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import javax.inject.Singleton

/** Servicios de dominio y utilidades que no son repositorios. */
@Module
@InstallIn(SingletonComponent::class)
object DomainModule {

    @Provides
    @Singleton
    fun provideTimeProvider(): TimeProvider = TimeProvider { System.currentTimeMillis() }

    @Provides
    @Singleton
    fun provideGamificationService(
        userRepository: UserRepository,
        publicationRepository: PublicationRepository,
        commentRepository: CommentRepository,
        generateNotificationUseCase: GenerateNotificationUseCase
    ): GamificationService {
        // El gancho de insignias no es suspend: la notificación se crea en un scope propio de la app.
        val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
        return GamificationService(
            userRepository = userRepository,
            publications = publicationRepository::snapshot,
            comments = commentRepository::snapshot,
            onBadgeUnlocked = { userId, badge ->
                scope.launch {
                    generateNotificationUseCase.generateAchievementUnlockedNotification(
                        userId = userId,
                        badgeName = badge.title,
                        badgeId = badge.name
                    )
                }
            }
        )
    }
}
