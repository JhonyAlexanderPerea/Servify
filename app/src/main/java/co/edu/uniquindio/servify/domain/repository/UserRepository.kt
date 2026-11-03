package co.edu.uniquindio.servify.domain.repository

import co.edu.uniquindio.servify.domain.model.User
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    val users: Flow<List<User>>

    fun observeUsers(): Flow<List<User>> = users

    fun observeCurrentUser(): Flow<User>

    fun getUserById(id: String): User? = getById(id)

    fun getById(id: String): User?

    fun findByEmail(email: String): User?

    fun add(user: User)

    fun update(id: String, transform: (User) -> User): User?

    suspend fun updateUserProfile(user: User) {
        update(user.id) { user }
    }

    suspend fun updatePoints(pointsToAdd: Int) {}
}
