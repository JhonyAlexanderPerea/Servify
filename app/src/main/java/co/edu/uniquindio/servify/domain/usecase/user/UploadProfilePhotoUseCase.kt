package co.edu.uniquindio.servify.domain.usecase.user

import co.edu.uniquindio.servify.domain.repository.ImageRepository
import co.edu.uniquindio.servify.domain.repository.UserRepository
import java.util.UUID
import javax.inject.Inject

class UploadProfilePhotoUseCase @Inject constructor(
    private val imageRepository: ImageRepository,
    private val userRepository: UserRepository
) {

    suspend operator fun invoke(userId: String, localUri: String): String {
        // Nombre único por subida: así Coil no sirve una imagen vieja desde su caché
        val url = imageRepository.uploadImage(localUri, "servify/avatars/$userId/${UUID.randomUUID()}")
        userRepository.update(userId) { it.copy(avatarUrl = url) }
        return url
    }
}
