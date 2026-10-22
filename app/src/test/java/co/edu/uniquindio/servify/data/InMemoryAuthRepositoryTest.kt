package co.edu.uniquindio.servify.data

import co.edu.uniquindio.servify.data.repository.InMemoryAuthRepository
import co.edu.uniquindio.servify.data.repository.InMemoryUserRepository
import co.edu.uniquindio.servify.data.seed.SeedUsers
import co.edu.uniquindio.servify.domain.model.AuthError
import co.edu.uniquindio.servify.domain.model.AuthResult
import co.edu.uniquindio.servify.domain.model.UserRole
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class InMemoryAuthRepositoryTest {

    private fun newRepo(): InMemoryAuthRepository {
        val users = InMemoryUserRepository(SeedUsers.users)
        return InMemoryAuthRepository(users, SeedUsers.credentials, clock = { 1_000L })
    }

    private fun failure(r: AuthResult) = (r as AuthResult.Failure).error

    @Test
    fun login_with_valid_credentials_opens_session() = runBlocking {
        val repo = newRepo()
        val r = repo.login("jhony.ramirez@gmail.com", "12345678")
        assertTrue(r is AuthResult.Success)
        assertEquals(SeedUsers.JHONY_ID, repo.currentUserSnapshot?.id)
        assertEquals(UserRole.USER, (r as AuthResult.Success).user.role)
    }

    @Test
    fun login_is_case_and_space_insensitive_on_email() = runBlocking {
        val r = newRepo().login("  Jhony.Ramirez@GMAIL.com ", "12345678")
        assertTrue(r is AuthResult.Success)
    }

    @Test
    fun wrong_password_or_unknown_email_fails_without_session() = runBlocking {
        val repo = newRepo()
        assertEquals(AuthError.INVALID_CREDENTIALS, failure(repo.login("jhony.ramirez@gmail.com", "mala")))
        assertEquals(AuthError.INVALID_CREDENTIALS, failure(repo.login("nadie@x.co", "12345678")))
        assertNull(repo.currentUserSnapshot)
    }

    @Test
    fun moderator_login_returns_moderator_role() = runBlocking {
        val r = newRepo().login(SeedUsers.MODERATOR_EMAIL, "Moderador2026") as AuthResult.Success
        assertEquals(UserRole.MODERATOR, r.user.role)
    }

    @Test
    fun regular_user_cannot_enter_through_moderator_login() = runBlocking {
        val repo = newRepo()
        val r = repo.login("jhony.ramirez@gmail.com", "12345678", requiredRole = UserRole.MODERATOR)
        assertEquals(AuthError.ROLE_NOT_ALLOWED, failure(r))
        assertNull(repo.currentUserSnapshot) // no abre sesión
    }

    @Test
    fun register_creates_user_role_with_zero_points_and_opens_session() = runBlocking {
        val repo = newRepo()
        val r = repo.register("Ana", "Ruiz", "Ana.Ruiz@Gmail.com", "clave1234", "Armenia, Quindío") as AuthResult.Success
        assertEquals(UserRole.USER, r.user.role)
        assertEquals(0, r.user.points)
        assertEquals("ana.ruiz@gmail.com", r.user.email)
        assertEquals(r.user.id, repo.currentUserSnapshot?.id)
        // y puede volver a entrar con esa cuenta
        repo.logout()
        assertTrue(repo.login("ana.ruiz@gmail.com", "clave1234") is AuthResult.Success)
    }

    @Test
    fun register_rejects_duplicates_including_the_moderator_email() = runBlocking {
        val repo = newRepo()
        assertEquals(AuthError.EMAIL_ALREADY_REGISTERED,
            failure(repo.register("X", "Y", "jhony.ramirez@gmail.com", "clave1234", "Armenia")))
        assertEquals(AuthError.EMAIL_ALREADY_REGISTERED,
            failure(repo.register("X", "Y", SeedUsers.MODERATOR_EMAIL, "clave1234", "Armenia")))
    }

    @Test
    fun register_validations() = runBlocking {
        val repo = newRepo()
        assertEquals(AuthError.INVALID_EMAIL, failure(repo.register("A", "B", "no-es-email", "clave1234", "Armenia")))
        assertEquals(AuthError.WEAK_PASSWORD, failure(repo.register("A", "B", "a@b.co", "corta", "Armenia")))
        assertEquals(AuthError.MISSING_FIELDS, failure(repo.register("", "B", "a@b.co", "clave1234", "Armenia")))
        assertNull(repo.currentUserSnapshot)
    }

    @Test
    fun logout_clears_the_session() = runBlocking {
        val repo = newRepo()
        repo.login("jhony.ramirez@gmail.com", "12345678")
        assertNotNull(repo.currentUser.first())
        repo.logout()
        assertNull(repo.currentUserSnapshot)
        assertNull(repo.currentUser.first())
    }

    @Test
    fun current_user_reflects_point_changes_of_the_session_user() = runBlocking {
        val users = InMemoryUserRepository(SeedUsers.users)
        val repo = InMemoryAuthRepository(users, SeedUsers.credentials)
        repo.login("jhony.ramirez@gmail.com", "12345678")
        users.update(SeedUsers.JHONY_ID) { it.copy(points = 2_000) }
        assertEquals(2_000, repo.currentUser.first()?.points)
    }

    @Test
    fun seed_points_match_their_declared_levels() {
        val byId = SeedUsers.users.associateBy { it.id }
        assertEquals("Experto", byId.getValue(SeedUsers.JHONY_ID).level.label)
        assertEquals("Profesional", byId.getValue(SeedUsers.JUAN_ID).level.label)
        assertEquals("Experto", byId.getValue(SeedUsers.LUIS_ID).level.label)
        assertEquals("Maestro", byId.getValue(SeedUsers.MARIA_ID).level.label)
        assertEquals("Profesional", byId.getValue(SeedUsers.VALENTINA_ID).level.label)
    }

    @Test
    fun exactly_one_preloaded_moderator() {
        assertEquals(1, SeedUsers.users.count { it.role == UserRole.MODERATOR })
    }
}
