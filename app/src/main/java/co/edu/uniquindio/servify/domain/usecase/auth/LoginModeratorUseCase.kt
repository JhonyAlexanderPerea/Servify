package co.edu.uniquindio.servify.domain.usecase.auth

import co.edu.uniquindio.servify.domain.model.AuthResult
import co.edu.uniquindio.servify.domain.model.UserRole
import co.edu.uniquindio.servify.domain.repository.AuthRepository
import co.edu.uniquindio.servify.domain.repository.SessionRepository
import javax.inject.Inject

/**
 * Autentica exigiendo rol MODERATOR.
 *
 * Si la cuenta existe pero no es de moderador falla con ROLE_NOT_ALLOWED y NO abre
 * sesión: la restricción vive en el caso de uso, no en la pantalla (ocultar un botón
 * nunca es seguridad). Si el login es válido guarda la sesión.
 */
class LoginModeratorUseCase @Inject constructor(
    private val authRepository: AuthRepository,
    private val sessionRepository: SessionRepository
) {

    suspend operator fun invoke(email: String, password: String): AuthResult {
        val result = authRepository.login(email, password, requiredRole = UserRole.MODERATOR)
        if (result is AuthResult.Success) {
            sessionRepository.saveSession(result.user.id, result.user.role)
        }
        return result
    }
}
