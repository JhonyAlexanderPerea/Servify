package co.edu.uniquindio.servify.domain.usecase.auth

import co.edu.uniquindio.servify.data.repository.InMemoryAuthRepository
import co.edu.uniquindio.servify.data.repository.InMemoryUserRepository
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
        LoginUserUseCase(repo)("jhony.ramirez@gmail.com", "12345678")
        assertEquals(SeedUsers.JHONY_ID, repo.currentUserSnapshot?.id)

        LogoutUseCase(repo)()

        assertNull(repo.currentUserSnapshot)
    }

    @Test
    fun observe_session_emits_the_user_in_session() = runBlocking {
        val repo = newRepo()
        LoginUserUseCase(repo)("jhony.ramirez@gmail.com", "12345678")

        val user = withTimeout(5_000) { ObserveSessionUseCase(repo)().first() }

        assertEquals(SeedUsers.JHONY_ID, user?.id)
    }

    @Test
    fun observe_session_emits_null_when_there_is_no_session() = runBlocking {
        val user = withTimeout(5_000) { ObserveSessionUseCase(newRepo())().first() }

        assertNull(user)
    }
}
