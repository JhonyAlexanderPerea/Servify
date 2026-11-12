package co.edu.uniquindio.servify.domain.usecase.auth

import co.edu.uniquindio.servify.data.repository.InMemoryAuthRepository
import co.edu.uniquindio.servify.data.repository.InMemoryUserRepository
import co.edu.uniquindio.servify.data.seed.SeedUsers
import co.edu.uniquindio.servify.domain.model.ConfirmPasswordResetResult
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test

class ConfirmPasswordResetUseCaseTest {

    private fun newRepository(): InMemoryAuthRepository =
        InMemoryAuthRepository(
            InMemoryUserRepository(SeedUsers.users),
            SeedUsers.credentials,
            clock = { 1_000L },
            resetDelayMillis = 0L
        )

    @Test
    fun a_valid_code_with_a_strong_password_succeeds() = runBlocking {
        val result = ConfirmPasswordResetUseCase(newRepository())("codigo-valido", "Servify2026")

        assertEquals(ConfirmPasswordResetResult.SUCCESS, result)
    }

    @Test
    fun a_blank_code_is_invalid_or_expired() = runBlocking {
        val result = ConfirmPasswordResetUseCase(newRepository())("   ", "Servify2026")

        assertEquals(ConfirmPasswordResetResult.INVALID_OR_EXPIRED_CODE, result)
    }

    @Test
    fun a_weak_password_is_rejected() = runBlocking {
        val result = ConfirmPasswordResetUseCase(newRepository())("codigo-valido", "123")

        assertEquals(ConfirmPasswordResetResult.WEAK_PASSWORD, result)
    }
}
