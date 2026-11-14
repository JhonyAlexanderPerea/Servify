package co.edu.uniquindio.servify.domain.usecase.auth

import co.edu.uniquindio.servify.domain.model.AuthResult
import co.edu.uniquindio.servify.domain.repository.AuthRepository
import co.edu.uniquindio.servify.domain.repository.SessionRepository
import javax.inject.Inject

class LoginWithGoogleUseCase @Inject constructor(
    private val authRepository: AuthRepository,
    private val sessionRepository: SessionRepository
) {
    suspend operator fun invoke(idToken: String): AuthResult {
        val result = authRepository.loginWithGoogle(idToken)
        if (result is AuthResult.Success) {
            sessionRepository.saveSession(result.user.id, result.user.role)
        }
        return result
    }
}
