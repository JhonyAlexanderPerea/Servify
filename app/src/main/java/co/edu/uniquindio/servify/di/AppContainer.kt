package co.edu.uniquindio.servify.di

import co.edu.uniquindio.servify.data.repository.InMemoryAuthRepository
import co.edu.uniquindio.servify.data.repository.InMemoryUserRepository
import co.edu.uniquindio.servify.data.seed.SeedUsers
import co.edu.uniquindio.servify.domain.repository.AuthRepository
import co.edu.uniquindio.servify.domain.repository.UserRepository
import co.edu.uniquindio.servify.domain.service.GamificationService
import co.edu.uniquindio.servify.domain.usecase.auth.LoginModeratorUseCase
import co.edu.uniquindio.servify.domain.usecase.auth.LoginUserUseCase
import co.edu.uniquindio.servify.domain.usecase.auth.LogoutUseCase
import co.edu.uniquindio.servify.domain.usecase.auth.ObserveSessionUseCase
import co.edu.uniquindio.servify.domain.usecase.reputation.AwardPointsUseCase
import co.edu.uniquindio.servify.domain.usecase.reputation.EvaluateAchievementsUseCase
import co.edu.uniquindio.servify.domain.usecase.reputation.GetLevelProgressUseCase
import co.edu.uniquindio.servify.domain.usecase.reputation.GetUserStatsUseCase
import co.edu.uniquindio.servify.domain.usecase.reputation.ObserveAchievementsUseCase
import co.edu.uniquindio.servify.domain.usecase.user.RegisterUserUseCase

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

    // -------------------------------------------------------------------------
    // CASOS DE USO
    //
    // Los ViewModels NUNCA hablan con un repositorio: hablan con un caso de uso.
    // Aqui se construyen porque todavia no hay Hilt (en la fase 3 cada uno lleva
    // su @Inject constructor y este bloque desaparece).
    // -------------------------------------------------------------------------

    val loginUserUseCase by lazy { LoginUserUseCase(authRepository) }
    val loginModeratorUseCase by lazy { LoginModeratorUseCase(authRepository) }
    val logoutUseCase by lazy { LogoutUseCase(authRepository) }
    val observeSessionUseCase by lazy { ObserveSessionUseCase(authRepository) }
    val registerUserUseCase by lazy { RegisterUserUseCase(authRepository) }
    val awardPointsUseCase by lazy { AwardPointsUseCase(gamificationService) }
    val evaluateAchievementsUseCase by lazy { EvaluateAchievementsUseCase(gamificationService) }
    val getLevelProgressUseCase by lazy { GetLevelProgressUseCase(gamificationService) }
    val observeAchievementsUseCase by lazy { ObserveAchievementsUseCase(gamificationService) }
    val getUserStatsUseCase by lazy { GetUserStatsUseCase(gamificationService) }
}
