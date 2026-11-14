package co.edu.uniquindio.servify.di

import co.edu.uniquindio.servify.data.repository.firestore.CategoryFirestoreRepositoryImpl
import co.edu.uniquindio.servify.data.repository.firestore.CommentFirestoreRepositoryImpl
import co.edu.uniquindio.servify.data.repository.firestore.NotificationFirestoreRepositoryImpl
import co.edu.uniquindio.servify.data.repository.firestore.PublicationFirestoreRepositoryImpl
import co.edu.uniquindio.servify.data.repository.firestore.ServiceRequestFirestoreRepositoryImpl
import co.edu.uniquindio.servify.data.repository.local.SessionRepositoryImpl
import co.edu.uniquindio.servify.data.repository.remote.CloudinaryImageRepository
import co.edu.uniquindio.servify.data.repository.remote.FirebaseAuthRepository
import co.edu.uniquindio.servify.data.repository.remote.MapboxGeocodingRepository
import co.edu.uniquindio.servify.data.repository.remote.OpenRouterClassifierRepository
import co.edu.uniquindio.servify.data.repository.remote.UserRemoteRepositoryImpl
import co.edu.uniquindio.servify.domain.repository.AiClassifierRepository
import co.edu.uniquindio.servify.domain.repository.AuthRepository
import co.edu.uniquindio.servify.domain.repository.CategoryRepository
import co.edu.uniquindio.servify.domain.repository.CommentRepository
import co.edu.uniquindio.servify.domain.repository.GeocodingRepository
import co.edu.uniquindio.servify.domain.repository.ImageRepository
import co.edu.uniquindio.servify.domain.repository.NotificationRepository
import co.edu.uniquindio.servify.domain.repository.PublicationRepository
import co.edu.uniquindio.servify.domain.repository.ServiceRequestRepository
import co.edu.uniquindio.servify.domain.repository.SessionRepository
import co.edu.uniquindio.servify.domain.repository.UserRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Repositorios de la aplicación vinculados a sus implementaciones en Firebase Firestore.
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindCategoryRepository(
        impl: CategoryFirestoreRepositoryImpl
    ): CategoryRepository

    @Binds
    @Singleton
    abstract fun bindUserRepository(
        impl: UserRemoteRepositoryImpl
    ): UserRepository

    @Binds
    @Singleton
    abstract fun bindServiceRequestRepository(
        impl: ServiceRequestFirestoreRepositoryImpl
    ): ServiceRequestRepository

    @Binds
    @Singleton
    abstract fun bindCommentRepository(
        impl: CommentFirestoreRepositoryImpl
    ): CommentRepository

    @Binds
    @Singleton
    abstract fun bindNotificationRepository(
        impl: NotificationFirestoreRepositoryImpl
    ): NotificationRepository

    @Binds
    @Singleton
    abstract fun bindSessionRepository(impl: SessionRepositoryImpl): SessionRepository

    @Binds
    @Singleton
    abstract fun bindAuthRepository(impl: FirebaseAuthRepository): AuthRepository

    @Binds
    @Singleton
    abstract fun bindImageRepository(impl: CloudinaryImageRepository): ImageRepository

    @Binds
    @Singleton
    abstract fun bindAiClassifierRepository(
        impl: OpenRouterClassifierRepository
    ): AiClassifierRepository

    @Binds
    @Singleton
    abstract fun bindGeocodingRepository(
        impl: MapboxGeocodingRepository
    ): GeocodingRepository

    @Binds
    @Singleton
    abstract fun bindPublicationRepository(
        impl: PublicationFirestoreRepositoryImpl
    ): PublicationRepository
}
