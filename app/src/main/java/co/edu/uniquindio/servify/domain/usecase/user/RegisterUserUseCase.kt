package co.edu.uniquindio.servify.domain.usecase.user

import co.edu.uniquindio.servify.domain.model.AuthResult
import co.edu.uniquindio.servify.domain.repository.AuthRepository

/**
 * Registra un usuario nuevo y abre su sesión.
 *
 * El registro siempre crea una cuenta con rol USER: nadie puede convertirse en
 * moderador por la pantalla de registro.
 */
class RegisterUserUseCase(private val authRepository: AuthRepository) {

    suspend operator fun invoke(
        name: String,
        lastName: String,
        email: String,
        password: String,
        city: String
    ): AuthResult = authRepository.register(
        name = name,
        lastName = lastName,
        email = email,
        password = password,
        city = city
    )
}
