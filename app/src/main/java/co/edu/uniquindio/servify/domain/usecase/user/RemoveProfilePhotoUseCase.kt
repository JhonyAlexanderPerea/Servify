package co.edu.uniquindio.servify.domain.usecase.user

import co.edu.uniquindio.servify.domain.repository.UserRepository
import javax.inject.Inject

/** Restablece la foto de perfil al avatar por defecto (iniciales). */
class RemoveProfilePhotoUseCase @Inject constructor(
    private val userRepository: UserRepository
) {

    suspend operator fun invoke(userId: String) {
        userRepository.update(userId) { it.copy(avatarUrl = null) }
    }
}
