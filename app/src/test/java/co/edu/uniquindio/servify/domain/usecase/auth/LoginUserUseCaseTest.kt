package co.edu.uniquindio.servify.domain.usecase.auth

import co.edu.uniquindio.servify.data.repository.InMemoryAuthRepository
import co.edu.uniquindio.servify.data.repository.InMemoryUserRepository
import co.edu.uniquindio.servify.data.seed.SeedUsers
import co.edu.uniquindio.servify.domain.model.AuthError
import co.edu.uniquindio.servify.domain.model.AuthResult
import co.edu.uniquindio.servify.domain.model.UserRole
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class LoginUserUseCaseTest {

    private fun newRepo(): InMemoryAuthRepository =
        InMemoryAuthRepository(
            InMemoryUserRepository(SeedUsers.users),
            SeedUsers.credentials,
            clock = { 1_000L }
        )

    private fun failure(r: AuthResult) = (r as AuthResult.Failure).error

    @Test
    fun opens_the_session_for_valid_credentials() = runBlocking {
        val repo = newRepo()
        val result = LoginUserUseCase(repo)("jhony.ramirez@gmail.com", "12345678")

        assertTrue(result is AuthResult.Success)
        assertEquals(SeedUsers.JHONY_ID, repo.currentUserSnapshot?.id)
    }

    @Test
    fun returns_the_role_so_the_screen_can_route() = runBlocking {
        val user = LoginUserUseCase(newRepo())(SeedUsers.MODERATOR_EMAIL, "Moderador2026") as AuthResult.Success

        assertEquals(UserRole.MODERATOR, user.user.role)
    }

    @Test
    fun does_not_open_a_session_on_wrong_credentials() = runBlocking {
        val repo = newRepo()
        val result = LoginUserUseCase(repo)("jhony.ramirez@gmail.com", "malaClave")

        assertEquals(AuthError.INVALID_CREDENTIALS, failure(result))
        assertNull(repo.currentUserSnapshot)
    }

    @Test
    fun normalizes_the_email_before_validating() = runBlocking {
        val result = LoginUserUseCase(newRepo())("  Jhony.Ramirez@GMAIL.com ", "12345678")

        assertTrue(result is AuthResult.Success)
    }
}
