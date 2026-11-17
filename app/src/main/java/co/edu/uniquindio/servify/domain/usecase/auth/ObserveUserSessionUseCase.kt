package co.edu.uniquindio.servify.domain.usecase.auth

import co.edu.uniquindio.servify.domain.model.UserSession
import co.edu.uniquindio.servify.domain.repository.SessionRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveUserSessionUseCase @Inject constructor(
    private val sessionRepository: SessionRepository
) {
    operator fun invoke(): Flow<UserSession?> = sessionRepository.session
}
