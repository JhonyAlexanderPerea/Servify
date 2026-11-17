package co.edu.uniquindio.servify.domain.usecase.user

import co.edu.uniquindio.servify.domain.model.DeleteAccountResult
import co.edu.uniquindio.servify.domain.repository.AuthRepository
import co.edu.uniquindio.servify.domain.repository.SessionRepository
import javax.inject.Inject

/** Elimina la cuenta y limpia la sesión guardada para que la navegación vuelva sola al login. */
class DeleteUserAccountUseCase @Inject constructor(
    private val authRepository: AuthRepository,
    private val sessionRepository: SessionRepository
) {

    suspend operator fun invoke(password: String): DeleteAccountResult {
        val result = authRepository.deleteAccount(password)
        // Borrar la sesión guardada hace que la navegación vuelva sola a AuthNavigation
        if (result == DeleteAccountResult.SUCCESS) sessionRepository.clearSession()
        return result
    }
}
