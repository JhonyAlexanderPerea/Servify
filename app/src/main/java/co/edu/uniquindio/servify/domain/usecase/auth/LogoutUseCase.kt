package co.edu.uniquindio.servify.domain.usecase.auth

import co.edu.uniquindio.servify.domain.repository.AuthRepository

/**
 * Cierra la sesión y limpia el estado.
 *
 * Es `suspend` aunque el repositorio en memoria no lo sea: cuando la autenticación
 * pase a Firebase, el sign-out es una llamada remota y el caso de uso no debe cambiar.
 */
class LogoutUseCase(private val authRepository: AuthRepository) {

    suspend operator fun invoke() {
        authRepository.logout()
    }
}
