package co.edu.uniquindio.servify.domain.usecase.user

import co.edu.uniquindio.servify.data.repository.InMemoryAuthRepository
import co.edu.uniquindio.servify.data.repository.InMemoryUserRepository
import co.edu.uniquindio.servify.data.seed.SeedUsers
import co.edu.uniquindio.servify.domain.model.AuthError
import co.edu.uniquindio.servify.domain.model.AuthResult
import co.edu.uniquindio.servify.domain.model.UserRole
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class RegisterUserUseCaseTest {

    private fun newRepo(): InMemoryAuthRepository =
        InMemoryAuthRepository(
            InMemoryUserRepository(SeedUsers.users),
            SeedUsers.credentials,
            clock = { 1_000L },
            idGenerator = { "user_nuevo" }
        )

    private fun failure(r: AuthResult) = (r as AuthResult.Failure).error

    @Test
    fun registers_a_new_user_and_opens_the_session() = runBlocking {
        val repo = newRepo()
        val result = RegisterUserUseCase(repo)(
            name = "Ana",
            lastName = "Ruiz",
            email = "ana.ruiz@servify.demo",
            password = "Servify2026",
            city = "Armenia"
        )

        assertTrue(result is AuthResult.Success)
        assertEquals("user_nuevo", repo.currentUserSnapshot?.id)
    }

    @Test
    fun a_registered_user_always_has_the_user_role() = runBlocking {
        val result = RegisterUserUseCase(newRepo())(
            "Ana", "Ruiz", "ana.ruiz@servify.demo", "Servify2026", "Armenia"
        ) as AuthResult.Success

        assertEquals(UserRole.USER, result.user.role)
    }

    @Test
    fun rejects_a_weak_password() = runBlocking {
        val result = RegisterUserUseCase(newRepo())(
            "Ana", "Ruiz", "ana.ruiz@servify.demo", "123", "Armenia"
        )

        assertEquals(AuthError.WEAK_PASSWORD, failure(result))
    }

    @Test
    fun rejects_an_invalid_email() = runBlocking {
        val result = RegisterUserUseCase(newRepo())(
            "Ana", "Ruiz", "no-es-un-email", "Servify2026", "Armenia"
        )

        assertEquals(AuthError.INVALID_EMAIL, failure(result))
    }

    @Test
    fun rejects_a_duplicate_email() = runBlocking {
        val result = RegisterUserUseCase(newRepo())(
            "Ana", "Ruiz", "jhony.ramirez@gmail.com", "Servify2026", "Armenia"
        )

        assertEquals(AuthError.EMAIL_ALREADY_REGISTERED, failure(result))
    }

    @Test
    fun rejects_missing_fields() = runBlocking {
        val result = RegisterUserUseCase(newRepo())(
            "", "Ruiz", "ana.ruiz@servify.demo", "Servify2026", "Armenia"
        )

        assertEquals(AuthError.MISSING_FIELDS, failure(result))
        assertNotNull(result)
    }
}
