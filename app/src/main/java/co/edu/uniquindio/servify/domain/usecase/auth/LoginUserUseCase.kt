package co.edu.uniquindio.servify.domain.usecase.auth

import co.edu.uniquindio.servify.domain.model.AuthResult
import co.edu.uniquindio.servify.domain.repository.AuthRepository
import co.edu.uniquindio.servify.domain.repository.SessionRepository
import javax.inject.Inject

/**
 * Autentica a la persona contra los usuarios registrados y devuelve el rol con el
 * que quedó abierta la sesión.
 *
 * Si el login tiene éxito guarda la sesión para que la navegación reaccione sola.
 * Para exigir un rol concreto esta clase tiene una hermana: [LoginModeratorUseCase].
 */
class LoginUserUseCase @Inject constructor(
    private val authRepository: AuthRepository,
    private val sessionRepository: SessionRepository
) {

    suspend operator fun invoke(email: String, password: String): AuthResult {
        val result = authRepository.login(email, password)
        if (result is AuthResult.Success) {
            sessionRepository.saveSession(result.user.id, result.user.role)
        }
        return result
    }
}
