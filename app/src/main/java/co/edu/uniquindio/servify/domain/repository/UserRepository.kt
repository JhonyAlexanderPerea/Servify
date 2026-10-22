package co.edu.uniquindio.servify.domain.repository

import co.edu.uniquindio.servify.domain.model.User
import kotlinx.coroutines.flow.Flow

/**
 * Contrato de repositorio para la gestión del perfil de usuario y reputación.
 */
interface UserRepository {
    fun observeCurrentUser(): Flow<User>
    suspend fun updateUserProfile(user: User)
    suspend fun updatePoints(pointsToAdd: Int)
}
