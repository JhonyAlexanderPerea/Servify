package co.edu.uniquindio.servify.domain.usecase.user

import co.edu.uniquindio.servify.domain.repository.UserRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class DeleteUserAccountUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke() = withContext(Dispatchers.IO) {
        // Simulated deletion in memory - clear by updating to empty state if needed
        // For the in-memory implementation, we just complete successfully
    }
}