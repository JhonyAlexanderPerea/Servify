package co.edu.uniquindio.servify.domain.usecase.auth

import co.edu.uniquindio.servify.domain.repository.AuthRepository
import co.edu.uniquindio.servify.domain.repository.SessionRepository
import javax.inject.Inject

/**
 * Cierra la sesión y limpia el estado.
 *
 * Cierra la autenticación en el origen de datos de usuarios y borra la sesión guardada.
 */
class LogoutUseCase @Inject constructor(
    private val authRepository: AuthRepository,
    private val sessionRepository: SessionRepository
) {

    suspend operator fun invoke() {
        authRepository.logout()
        sessionRepository.clearSession()
    }
}
