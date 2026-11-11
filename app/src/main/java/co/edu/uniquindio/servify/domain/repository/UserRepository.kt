package co.edu.uniquindio.servify.domain.repository

import co.edu.uniquindio.servify.domain.model.User
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    fun observeCurrentUser(): Flow<User>
    fun observeUserById(id: String): Flow<User?>
    suspend fun getById(id: String): User?
    suspend fun getUserById(id: String): User? = getById(id)
    suspend fun findByEmail(email: String): User?
    suspend fun add(user: User)
    suspend fun update(id: String, transform: (User) -> User): User?
    suspend fun updateUserProfile(user: User) {
        update(user.id) { user }
    }
    suspend fun updatePoints(pointsToAdd: Int) {}
}
