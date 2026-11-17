package co.edu.uniquindio.servify.domain.usecase.user

import co.edu.uniquindio.servify.data.repository.InMemoryUserRepository
import co.edu.uniquindio.servify.data.repository.memory.InMemoryImageRepository
import co.edu.uniquindio.servify.domain.model.User
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class UploadProfilePhotoUseCaseTest {

    private val user = User(id = "user_1", name = "Ana", email = "ana@servify.demo")
    private val users = InMemoryUserRepository(listOf(user))
    private val images = InMemoryImageRepository()
    private val uploadProfilePhoto = UploadProfilePhotoUseCase(images, users)

    @Test
    fun uploading_a_photo_persists_the_returned_url_on_the_user() = runBlocking {
        val url = uploadProfilePhoto(user.id, "content://galeria/foto.jpg")

        assertEquals("https://fake.servify.test/${images.uploads.single().second}", url)
        assertEquals("content://galeria/foto.jpg", images.uploads.single().first)
        assertEquals(url, users.getById(user.id)?.avatarUrl)
    }

    @Test
    fun the_remote_path_starts_with_the_user_avatar_folder() = runBlocking {
        uploadProfilePhoto(user.id, "content://galeria/foto.jpg")

        val remotePath = images.uploads.single().second
        assertTrue(remotePath.startsWith("servify/avatars/${user.id}/"))
    }
}
