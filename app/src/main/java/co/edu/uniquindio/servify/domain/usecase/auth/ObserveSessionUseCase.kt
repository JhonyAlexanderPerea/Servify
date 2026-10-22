package co.edu.uniquindio.servify.domain.usecase.auth

import co.edu.uniquindio.servify.domain.model.User
import co.edu.uniquindio.servify.domain.repository.AuthRepository
import kotlinx.coroutines.flow.Flow

/**
 * Observa la sesión activa: emite el usuario con sesión (o null si no hay).
 *
 * El flujo se recalcula cuando cambian los puntos o las insignias del usuario, así que
 * las pantallas que dependen de la sesión se refrescan solas.
 */
class ObserveSessionUseCase(private val authRepository: AuthRepository) {

    operator fun invoke(): Flow<User?> = authRepository.currentUser
}
