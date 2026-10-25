package co.edu.uniquindio.servify.di

import co.edu.uniquindio.servify.data.repository.InMemoryAuthRepository
import co.edu.uniquindio.servify.data.repository.memory.CommentRepositoryImpl
import co.edu.uniquindio.servify.data.repository.memory.NotificationRepositoryImpl
import co.edu.uniquindio.servify.data.repository.memory.PublicationRepositoryImpl
import co.edu.uniquindio.servify.data.repository.memory.ServiceRequestRepositoryImpl
import co.edu.uniquindio.servify.data.repository.memory.UserRepositoryImpl
import co.edu.uniquindio.servify.data.seed.SeedUsers
import co.edu.uniquindio.servify.domain.repository.AuthRepository
import co.edu.uniquindio.servify.domain.repository.CommentRepository
import co.edu.uniquindio.servify.domain.repository.NotificationRepository
import co.edu.uniquindio.servify.domain.repository.PublicationRepository
import co.edu.uniquindio.servify.domain.repository.ServiceRequestRepository
import co.edu.uniquindio.servify.domain.repository.UserRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Repositorios de la aplicación.
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindPublicationRepository(
        impl: PublicationRepositoryImpl
    ): PublicationRepository

    @Binds
    @Singleton
    abstract fun bindUserRepository(
        impl: UserRepositoryImpl
    ): UserRepository

    @Binds
    @Singleton
    abstract fun bindServiceRequestRepository(
        impl: ServiceRequestRepositoryImpl
    ): ServiceRequestRepository

    @Binds
    @Singleton
    abstract fun bindCommentRepository(
        impl: CommentRepositoryImpl
    ): CommentRepository

    @Binds
    @Singleton
    abstract fun bindNotificationRepository(
        impl: NotificationRepositoryImpl
    ): NotificationRepository

    companion object {
        @Provides
        @Singleton
        fun provideAuthRepository(
            userRepository: UserRepository
        ): AuthRepository = InMemoryAuthRepository(userRepository, SeedUsers.credentials)
    }
}
