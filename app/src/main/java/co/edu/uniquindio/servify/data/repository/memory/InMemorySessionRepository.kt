package co.edu.uniquindio.servify.data.repository.memory

import co.edu.uniquindio.servify.domain.model.UserRole
import co.edu.uniquindio.servify.domain.model.UserSession
import co.edu.uniquindio.servify.domain.repository.SessionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

class InMemorySessionRepository(initial: UserSession? = null) : SessionRepository {
    private val state = MutableStateFlow(initial)
    override val session: Flow<UserSession?> = state
    override suspend fun saveSession(userId: String, role: UserRole) { state.value = UserSession(userId, role) }
    override suspend fun clearSession() { state.value = null }
}
