package co.edu.uniquindio.servify.domain.usecase.auth

import co.edu.uniquindio.servify.data.repository.InMemoryAuthRepository
import co.edu.uniquindio.servify.data.repository.InMemoryUserRepository
import co.edu.uniquindio.servify.data.repository.memory.InMemorySessionRepository
import co.edu.uniquindio.servify.data.seed.SeedUsers
import co.edu.uniquindio.servify.domain.model.AuthError
import co.edu.uniquindio.servify.domain.model.AuthResult
import co.edu.uniquindio.servify.domain.model.UserRole
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class LoginModeratorUseCaseTest {

    private fun newRepo(): InMemoryAuthRepository =
        InMemoryAuthRepository(
            InMemoryUserRepository(SeedUsers.users),
            SeedUsers.credentials,
            clock = { 1_000L }
        )

    private fun failure(r: AuthResult) = (r as AuthResult.Failure).error

    @Test
    fun opens_the_session_for_a_moderator() = runBlocking {
        val repo = newRepo()
        val result = LoginModeratorUseCase(repo, InMemorySessionRepository())(
            SeedUsers.MODERATOR_EMAIL,
            "Moderador2026"
        )

        assertTrue(result is AuthResult.Success)
        assertEquals(SeedUsers.MODERATOR_ID, repo.currentUserSnapshot?.id)
    }

    @Test
    fun saves_the_moderator_session_on_success() = runBlocking {
        val session = InMemorySessionRepository()
        LoginModeratorUseCase(newRepo(), session)(SeedUsers.MODERATOR_EMAIL, "Moderador2026")

        val saved = session.session.first()

        assertEquals(SeedUsers.MODERATOR_ID, saved?.userId)
        assertEquals(UserRole.MODERATOR, saved?.role)
    }

    @Test
    fun refuses_a_regular_user_with_role_not_allowed() = runBlocking {
        val repo = newRepo()
        val result = LoginModeratorUseCase(repo, InMemorySessionRepository())("jhony.ramirez@gmail.com", "12345678")

        assertEquals(AuthError.ROLE_NOT_ALLOWED, failure(result))
    }

    @Test
    fun a_refused_login_does_not_open_a_session() = runBlocking {
        val repo = newRepo()
        LoginModeratorUseCase(repo, InMemorySessionRepository())("jhony.ramirez@gmail.com", "12345678")

        assertNull(repo.currentUserSnapshot)
    }

    @Test
    fun a_wrong_role_login_does_not_save_a_session() = runBlocking {
        val session = InMemorySessionRepository()
        LoginModeratorUseCase(newRepo(), session)("jhony.ramirez@gmail.com", "12345678")

        assertNull(session.session.first())
    }
}
