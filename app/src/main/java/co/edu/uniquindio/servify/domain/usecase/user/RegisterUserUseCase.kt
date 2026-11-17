package co.edu.uniquindio.servify.domain.usecase.user

import co.edu.uniquindio.servify.domain.model.AuthResult
import co.edu.uniquindio.servify.domain.repository.AuthRepository
import co.edu.uniquindio.servify.domain.repository.SessionRepository
import javax.inject.Inject

/**
 * Registra un usuario nuevo y abre su sesión.
 *
 * El registro siempre crea una cuenta con rol USER: nadie puede convertirse en
 * moderador por la pantalla de registro. Si el registro tiene éxito guarda la sesión.
 */
class RegisterUserUseCase @Inject constructor(
    private val authRepository: AuthRepository,
    private val sessionRepository: SessionRepository
) {

    suspend operator fun invoke(
        name: String,
        lastName: String,
        email: String,
        password: String,
        city: String
    ): AuthResult {
        val result = authRepository.register(
            name = name,
            lastName = lastName,
            email = email,
            password = password,
            city = city
        )
        if (result is AuthResult.Success) {
            sessionRepository.saveSession(result.user.id, result.user.role)
        }
        return result
    }
}
