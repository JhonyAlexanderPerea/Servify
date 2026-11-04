package co.edu.uniquindio.servify.e2e

import co.edu.uniquindio.servify.data.repository.InMemoryAuthRepository
import co.edu.uniquindio.servify.data.repository.InMemoryPublicationRepository
import co.edu.uniquindio.servify.data.repository.memory.CommentRepositoryImpl
import co.edu.uniquindio.servify.data.repository.memory.NotificationRepositoryImpl
import co.edu.uniquindio.servify.data.repository.memory.ServiceRequestRepositoryImpl
import co.edu.uniquindio.servify.data.repository.memory.UserRepositoryImpl
import co.edu.uniquindio.servify.data.seed.SeedPublications
import co.edu.uniquindio.servify.data.seed.SeedUsers
import co.edu.uniquindio.servify.di.RepositoryModule
import co.edu.uniquindio.servify.domain.repository.AuthRepository
import co.edu.uniquindio.servify.domain.repository.CommentRepository
import co.edu.uniquindio.servify.domain.repository.NotificationRepository
import co.edu.uniquindio.servify.domain.repository.PublicationRepository
import co.edu.uniquindio.servify.domain.repository.ServiceRequestRepository
import co.edu.uniquindio.servify.domain.repository.UserRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import javax.inject.Singleton

@TestInstallIn(
    components = [SingletonComponent::class],
    replaces = [RepositoryModule::class]
)
@Module
object E2ETestRepositoryModule {

    @Provides
    @Singleton
    fun providePublicationRepository(): PublicationRepository =
        InMemoryPublicationRepository(SeedPublications.publications)

    @Provides
    @Singleton
    fun provideUserRepository(): UserRepository = UserRepositoryImpl()

    @Provides
    @Singleton
    fun provideAuthRepository(userRepository: UserRepository): AuthRepository =
        InMemoryAuthRepository(
            userRepository = userRepository,
            credentials = SeedUsers.credentials,
            resetDelayMillis = 0
        )

    @Provides
    @Singleton
    fun provideNotificationRepository(): NotificationRepository = NotificationRepositoryImpl()

    @Provides
    @Singleton
    fun provideServiceRequestRepository(): ServiceRequestRepository = ServiceRequestRepositoryImpl()

    @Provides
    @Singleton
    fun provideCommentRepository(): CommentRepository = CommentRepositoryImpl()
}
