package co.edu.uniquindio.servify.domain.repository

import co.edu.uniquindio.servify.domain.model.UserRole
import co.edu.uniquindio.servify.domain.model.UserSession
import kotlinx.coroutines.flow.Flow

interface SessionRepository {
    val session: Flow<UserSession?>
    suspend fun saveSession(userId: String, role: UserRole)
    suspend fun clearSession()
}
