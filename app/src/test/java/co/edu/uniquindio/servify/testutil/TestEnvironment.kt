package co.edu.uniquindio.servify.testutil

import co.edu.uniquindio.servify.data.repository.InMemoryAuthRepository
import co.edu.uniquindio.servify.data.repository.InMemoryPublicationRepository
import co.edu.uniquindio.servify.data.repository.InMemoryUserRepository
import co.edu.uniquindio.servify.data.repository.memory.NotificationRepositoryImpl
import co.edu.uniquindio.servify.data.repository.memory.ServiceRequestRepositoryImpl
import co.edu.uniquindio.servify.data.seed.SeedPublications
import co.edu.uniquindio.servify.data.seed.SeedUsers
import co.edu.uniquindio.servify.domain.model.AuthResult
import co.edu.uniquindio.servify.domain.service.GamificationService
import co.edu.uniquindio.servify.domain.usecase.auth.LoginUserUseCase
import co.edu.uniquindio.servify.domain.usecase.auth.ObserveSessionUseCase
import co.edu.uniquindio.servify.domain.usecase.auth.SendPasswordResetUseCase
import co.edu.uniquindio.servify.domain.usecase.moderation.ApprovePublicationUseCase
import co.edu.uniquindio.servify.domain.usecase.moderation.FinalizePublicationByModeratorUseCase
import co.edu.uniquindio.servify.domain.usecase.moderation.ObserveModerationMetricsUseCase
import co.edu.uniquindio.servify.domain.usecase.moderation.ObservePendingPublicationsUseCase
import co.edu.uniquindio.servify.domain.usecase.moderation.RejectPublicationUseCase
import co.edu.uniquindio.servify.domain.usecase.notification.GenerateNotificationUseCase
import co.edu.uniquindio.servify.domain.usecase.publication.DeletePublicationUseCase
import co.edu.uniquindio.servify.domain.usecase.publication.MarkPublicationCompletedUseCase
import co.edu.uniquindio.servify.domain.usecase.publication.ObserveMyPublicationsUseCase
import co.edu.uniquindio.servify.domain.usecase.publication.ObserveServicesUseCase
import co.edu.uniquindio.servify.domain.usecase.reputation.AwardPointsUseCase
import co.edu.uniquindio.servify.domain.usecase.reputation.ObserveUserProgressUseCase
import co.edu.uniquindio.servify.domain.usecase.request.CreateServiceRequestUseCase
import co.edu.uniquindio.servify.domain.usecase.user.RegisterUserUseCase
import co.edu.uniquindio.servify.domain.util.TimeProvider

/**
 * Arma el mismo grafo que la app (repositorios en memoria + datos semilla) pero a mano, sin Hilt,
 * y con reloj fijo. Cada test crea su propio entorno, así que los cambios nunca se filtran entre tests.
 *
 * Cuentas de prueba (ver SeedUsers): [JHONY_EMAIL] tiene publicaciones en los 4 estados.
 */
class TestEnvironment(var nowMillis: Long = 2_000_000_000_000L) {

    val timeProvider = TimeProvider { nowMillis }

    val users = InMemoryUserRepository(SeedUsers.users)
    val publications = InMemoryPublicationRepository(SeedPublications.publications)
    val serviceRequests = ServiceRequestRepositoryImpl()
    val notifications = NotificationRepositoryImpl()
    val auth = InMemoryAuthRepository(
        userRepository = users,
        credentials = SeedUsers.credentials,
        clock = { nowMillis },
        resetDelayMillis = 0L
    )
    val gamification = GamificationService(
        userRepository = users,
        publications = publications::snapshot,
        comments = { emptyList() }
    )

    // Casos de uso (los mismos que inyecta Hilt en la app)
    val loginUser = LoginUserUseCase(auth)
    val registerUser = RegisterUserUseCase(auth)
    val observeSession = ObserveSessionUseCase(auth)
    val sendPasswordReset = SendPasswordResetUseCase(auth)
    val observeMyPublications = ObserveMyPublicationsUseCase(publications)
    val observeServices = ObserveServicesUseCase(publications)
    val deletePublication = DeletePublicationUseCase(publications)
    val markCompleted = MarkPublicationCompletedUseCase(publications, timeProvider)
    val observeUserProgress = ObserveUserProgressUseCase(gamification)
    val awardPoints = AwardPointsUseCase(gamification)

    val generateNotification = GenerateNotificationUseCase(notifications)
    val createServiceRequest = CreateServiceRequestUseCase(
        serviceRequestRepository = serviceRequests,
        publicationRepository = publications,
        userRepository = users,
        generateNotificationUseCase = generateNotification
    )
    val approvePublication = ApprovePublicationUseCase(
        publicationRepository = publications,
        generateNotificationUseCase = generateNotification,
        awardPointsUseCase = awardPoints,
        timeProvider = timeProvider
    )
    val rejectPublication = RejectPublicationUseCase(publications, generateNotification)
    val finalizePublication = FinalizePublicationByModeratorUseCase(publications, timeProvider)
    val observePendingPublications = ObservePendingPublicationsUseCase(publications)
    val observeModerationMetrics = ObserveModerationMetricsUseCase(publications, timeProvider)

    /** Abre sesión con una cuenta semilla; falla el test si las credenciales no son válidas. */
    suspend fun loginAs(email: String, password: String = SeedUsers.credentials.getValue(email)) {
        check(loginUser(email, password) is AuthResult.Success) { "No se pudo iniciar sesión como $email" }
    }

    companion object {
        const val JHONY_EMAIL = "jhony.ramirez@gmail.com"
        const val JHONY_PASSWORD = "12345678"
        const val MODERATOR_EMAIL = SeedUsers.MODERATOR_EMAIL
    }
}
