package co.edu.uniquindio.servify.data.repository.memory

import co.edu.uniquindio.servify.domain.model.User
import co.edu.uniquindio.servify.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepositoryImpl @Inject constructor() : UserRepository {

    private val _users = MutableStateFlow(
        listOf(
            SampleData.currentUser,
            SampleData.providerJuan,
            SampleData.providerLuis,
            SampleData.providerMaria
        )
    )

    private val _currentUser = MutableStateFlow(SampleData.currentUser)

    override val users: Flow<List<User>> = _users.asStateFlow()

    override fun observeCurrentUser(): Flow<User> = _currentUser.asStateFlow()

    override fun getById(id: String): User? = _users.value.find { it.id == id }

    override fun findByEmail(email: String): User? =
        _users.value.find { it.email.equals(email, ignoreCase = true) }

    override fun add(user: User) {
        _users.update { current ->
            if (current.none { it.id == user.id }) current + user else current
        }
    }

    override fun update(id: String, transform: (User) -> User): User? {
        var updatedUser: User? = null
        _users.update { list ->
            list.map { u ->
                if (u.id == id) {
                    val newU = transform(u)
                    updatedUser = newU
                    newU
                } else u
            }
        }
        if (_currentUser.value.id == id && updatedUser != null) {
            _currentUser.value = updatedUser
        }
        return updatedUser
    }

    override suspend fun updateUserProfile(user: User) {
        _currentUser.value = user
        update(user.id) { user }
    }

    override suspend fun updatePoints(pointsToAdd: Int) {
        _currentUser.update { user ->
            user.copy(currentPoints = user.currentPoints + pointsToAdd)
        }
        update(_currentUser.value.id) { user ->
            user.copy(currentPoints = user.currentPoints + pointsToAdd)
        }
    }
}
