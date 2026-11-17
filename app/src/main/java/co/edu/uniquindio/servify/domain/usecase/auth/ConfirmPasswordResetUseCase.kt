package co.edu.uniquindio.servify.domain.usecase.auth

import co.edu.uniquindio.servify.domain.model.ConfirmPasswordResetResult
import co.edu.uniquindio.servify.domain.repository.AuthRepository
import javax.inject.Inject

/** Confirma el restablecimiento usando el código del enlace de recuperación. */
class ConfirmPasswordResetUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {

    suspend operator fun invoke(oobCode: String, newPassword: String): ConfirmPasswordResetResult =
        authRepository.confirmPasswordReset(oobCode, newPassword)
}
