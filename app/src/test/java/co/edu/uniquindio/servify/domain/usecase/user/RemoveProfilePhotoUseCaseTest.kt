package co.edu.uniquindio.servify.domain.usecase.user

import co.edu.uniquindio.servify.data.repository.InMemoryUserRepository
import co.edu.uniquindio.servify.domain.model.User
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertNull
import org.junit.Test

class RemoveProfilePhotoUseCaseTest {

    private val user = User(
        id = "user_1",
        name = "Ana",
        email = "ana@servify.demo",
        avatarUrl = "https://fake.servify.test/avatar.jpg"
    )
    private val users = InMemoryUserRepository(listOf(user))
    private val removeProfilePhoto = RemoveProfilePhotoUseCase(users)

    @Test
    fun removing_the_photo_clears_the_avatar_url() = runBlocking {
        removeProfilePhoto(user.id)

        assertNull(users.getById(user.id)?.avatarUrl)
    }
}
