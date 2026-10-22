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

    private val _currentUser = MutableStateFlow(SampleData.currentUser)

    override fun observeCurrentUser(): Flow<User> = _currentUser.asStateFlow()

    override suspend fun updateUserProfile(user: User) {
        _currentUser.value = user
    }

    override suspend fun updatePoints(pointsToAdd: Int) {
        _currentUser.update { user ->
            user.copy(currentPoints = user.currentPoints + pointsToAdd)
        }
    }
}
