package co.edu.uniquindio.servify.di

import co.edu.uniquindio.servify.data.repository.InMemoryAuthRepository
import co.edu.uniquindio.servify.data.repository.InMemoryUserRepository
import co.edu.uniquindio.servify.data.seed.SeedUsers
import co.edu.uniquindio.servify.domain.repository.AuthRepository
import co.edu.uniquindio.servify.domain.repository.UserRepository
import co.edu.uniquindio.servify.domain.service.GamificationService

/**
 * Contenedor de dependencias de la fase 2 (singletons en memoria, sin Hilt).
 * Los ViewModels lo usan como valor por defecto de su constructor, así `viewModel()` sigue funcionando
 * y en las pruebas se inyectan repositorios falsos.
 *
 * En la fase 3 solo cambian las implementaciones de aquí (Firebase/Supabase).
 */
object AppContainer {

    val userRepository: UserRepository by lazy { InMemoryUserRepository(SeedUsers.users) }

    val authRepository: AuthRepository by lazy {
        InMemoryAuthRepository(userRepository, SeedUsers.credentials)
    }

    val gamificationService: GamificationService by lazy {
        GamificationService(
            userRepository = userRepository,
            // TODO(Jean, tareas 4-5): reemplazar por publicationRepository.snapshot() / commentRepository.snapshot()
            publications = { emptyList() },
            comments = { emptyList() },
            // TODO(Jean, tarea 8): notificationGenerator.onBadgeUnlocked(userId, badge)
            onBadgeUnlocked = { _, _ -> }
        )
    }
}
