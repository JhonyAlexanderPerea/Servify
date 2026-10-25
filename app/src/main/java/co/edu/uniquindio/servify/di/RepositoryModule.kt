package co.edu.uniquindio.servify.di

import co.edu.uniquindio.servify.data.repository.InMemoryAuthRepository
import co.edu.uniquindio.servify.data.repository.InMemoryPublicationRepository
import co.edu.uniquindio.servify.data.repository.InMemoryUserRepository
import co.edu.uniquindio.servify.data.seed.SeedPublications
import co.edu.uniquindio.servify.data.seed.SeedUsers
import co.edu.uniquindio.servify.domain.repository.AuthRepository
import co.edu.uniquindio.servify.domain.repository.PublicationRepository
import co.edu.uniquindio.servify.domain.repository.UserRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Repositorios de la FASE 2: implementaciones en memoria con datos semilla.
 *
 * Este es el único archivo que cambia al pasar a Room/Firebase (fase 3): se reemplaza cada
 * `@Provides` por la implementación persistente y ni los casos de uso ni los ViewModels se tocan.
 * Se usa `@Provides` (y no `@Binds`) porque las implementaciones en memoria reciben la semilla
 * por constructor; con una base de datos real pasarían a `@Binds` + `@Inject constructor`.
 */
@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun provideUserRepository(): UserRepository = InMemoryUserRepository(SeedUsers.users)

    @Provides
    @Singleton
    fun providePublicationRepository(): PublicationRepository =
        InMemoryPublicationRepository(SeedPublications.publications)

    @Provides
    @Singleton
    fun provideAuthRepository(userRepository: UserRepository): AuthRepository =
        InMemoryAuthRepository(userRepository, SeedUsers.credentials)
}
