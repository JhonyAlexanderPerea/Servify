package co.edu.uniquindio.servify.domain.usecase.auth

import co.edu.uniquindio.servify.domain.model.AuthResult
import co.edu.uniquindio.servify.domain.model.UserRole
import co.edu.uniquindio.servify.domain.repository.AuthRepository
import javax.inject.Inject

/**
 * Autentica exigiendo rol MODERATOR.
 *
 * Si la cuenta existe pero no es de moderador falla con ROLE_NOT_ALLOWED y NO abre
 * sesión: la restricción vive en el caso de uso, no en la pantalla (ocultar un botón
 * nunca es seguridad).
 *
 * Es el caso de uso de la pantalla de login de moderador. Hoy el panel se alcanza
 * desde el login unificado, que usa [LoginUserUseCase].
 */
class LoginModeratorUseCase @Inject constructor(private val authRepository: AuthRepository) {

    suspend operator fun invoke(email: String, password: String): AuthResult =
        authRepository.login(email, password, requiredRole = UserRole.MODERATOR)
}
