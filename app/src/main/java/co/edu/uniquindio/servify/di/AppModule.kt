package co.edu.uniquindio.servify.di

import co.edu.uniquindio.servify.core.util.ResourceProvider
import co.edu.uniquindio.servify.core.util.ResourceProviderImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/** Utilidades de la aplicación que dependen del contexto de Android. */
@Module
@InstallIn(SingletonComponent::class)
abstract class AppModule {

    @Binds
    @Singleton
    abstract fun bindResourceProvider(impl: ResourceProviderImpl): ResourceProvider
}
