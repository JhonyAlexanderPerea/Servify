package co.edu.uniquindio.servify.domain.usecase.auth

import co.edu.uniquindio.servify.data.repository.InMemoryAuthRepository
import co.edu.uniquindio.servify.data.repository.InMemoryUserRepository
import co.edu.uniquindio.servify.data.repository.memory.InMemorySessionRepository
import co.edu.uniquindio.servify.data.seed.SeedUsers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class LogoutAndObserveSessionUseCaseTest {

    private fun newRepo(): InMemoryAuthRepository =
        InMemoryAuthRepository(
            InMemoryUserRepository(SeedUsers.users),
            SeedUsers.credentials,
            clock = { 1_000L }
        )

    @Test
    fun logout_clears_the_session() = runBlocking {
        val repo = newRepo()
        val session = InMemorySessionRepository()
        LoginUserUseCase(repo, session)("jhony.ramirez@gmail.com", "12345678")
        assertEquals(SeedUsers.JHONY_ID, repo.currentUserSnapshot?.id)

        LogoutUseCase(repo, session)()

        assertNull(repo.currentUserSnapshot)
        assertNull(session.session.first())
    }

    @Test
    fun observe_session_emits_the_user_in_session() = runBlocking {
        val repo = newRepo()
        LoginUserUseCase(repo, InMemorySessionRepository())("jhony.ramirez@gmail.com", "12345678")

        val user = withTimeout(5_000) { ObserveSessionUseCase(repo)().first() }

        assertEquals(SeedUsers.JHONY_ID, user?.id)
    }

    @Test
    fun observe_session_emits_null_when_there_is_no_session() = runBlocking {
        val user = withTimeout(5_000) { ObserveSessionUseCase(newRepo())().first() }

        assertNull(user)
    }

    @Test
    fun observe_user_session_emits_the_saved_session() = runBlocking {
        val repo = newRepo()
        val session = InMemorySessionRepository()
        LoginUserUseCase(repo, session)("jhony.ramirez@gmail.com", "12345678")

        val saved = withTimeout(5_000) { ObserveUserSessionUseCase(session)().first() }

        assertEquals(SeedUsers.JHONY_ID, saved?.userId)
    }

    @Test
    fun observe_user_session_emits_null_after_logout() = runBlocking {
        val repo = newRepo()
        val session = InMemorySessionRepository()
        LoginUserUseCase(repo, session)("jhony.ramirez@gmail.com", "12345678")
        LogoutUseCase(repo, session)()

        val saved = withTimeout(5_000) { ObserveUserSessionUseCase(session)().first() }

        assertNull(saved)
    }
}
