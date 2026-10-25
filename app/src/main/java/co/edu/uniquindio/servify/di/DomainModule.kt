package co.edu.uniquindio.servify.di

import co.edu.uniquindio.servify.domain.repository.PublicationRepository
import co.edu.uniquindio.servify.domain.repository.UserRepository
import co.edu.uniquindio.servify.domain.service.GamificationService
import co.edu.uniquindio.servify.domain.util.TimeProvider
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
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
        publicationRepository: PublicationRepository
    ): GamificationService = GamificationService(
        userRepository = userRepository,
        publications = publicationRepository::snapshot,
        // TODO(Jean, tarea 6): reemplazar por commentRepository::snapshot cuando exista CommentRepository
        comments = { emptyList() },
        // TODO(Jean, tarea 9): notificationGenerator.onBadgeUnlocked(userId, badge)
        onBadgeUnlocked = { _, _ -> }
    )
}
