package co.edu.uniquindio.servify.domain.usecase.user

import co.edu.uniquindio.servify.domain.model.User
import co.edu.uniquindio.servify.domain.repository.UserRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class UpdateUserProfileUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(user: User) = withContext(Dispatchers.IO) {
        userRepository.updateUserProfile(user)
    }
}