package co.edu.uniquindio.servify.domain.usecase.auth

import co.edu.uniquindio.servify.domain.model.AuthResult
import co.edu.uniquindio.servify.domain.repository.AuthRepository

/**
 * Autentica a la persona contra los usuarios registrados y devuelve el rol con el
 * que quedó abierta la sesión.
 *
 * El rol viaja en el resultado porque la pantalla de login enruta a partir de él
 * (usuario -> Home, moderador -> panel). Para exigir un rol concreto esta clase
 * tiene una hermana: [LoginModeratorUseCase].
 */
class LoginUserUseCase(private val authRepository: AuthRepository) {

    suspend operator fun invoke(email: String, password: String): AuthResult =
        authRepository.login(email, password)
}
