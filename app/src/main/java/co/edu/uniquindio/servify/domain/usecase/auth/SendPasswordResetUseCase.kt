package co.edu.uniquindio.servify.domain.usecase.auth

import co.edu.uniquindio.servify.domain.model.PasswordResetResult
import co.edu.uniquindio.servify.domain.model.UserRole
import co.edu.uniquindio.servify.domain.repository.AuthRepository
import co.edu.uniquindio.servify.domain.rules.CredentialRules
import javax.inject.Inject

/**
 * Solicita el enlace de recuperación de contraseña. Lo usan los dos flujos (usuario y moderador):
 * [role] limita la recuperación a cuentas de ese rol. Valida el formato del correo antes de llamar
 * al repositorio, así la regla es la misma en cualquier pantalla.
 */
class SendPasswordResetUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(email: String, role: UserRole? = null): PasswordResetResult {
        if (!CredentialRules.isValidEmail(email)) return PasswordResetResult.InvalidEmail
        return authRepository.sendPasswordReset(email, role)
    }
}
