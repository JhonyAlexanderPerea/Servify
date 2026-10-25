package co.edu.uniquindio.servify.domain.usecase.auth

import co.edu.uniquindio.servify.domain.model.PasswordResetResult
import co.edu.uniquindio.servify.domain.model.UserRole
import co.edu.uniquindio.servify.testutil.TestEnvironment
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class SendPasswordResetUseCaseTest {

    private val env = TestEnvironment()

    @Test
    fun invalid_email_is_rejected_before_reaching_the_repository() = runBlocking {
        listOf("", "   ", "sin-arroba", "a@b").forEach {
            assertEquals(PasswordResetResult.InvalidEmail, env.sendPasswordReset(it))
        }
        assertTrue(env.auth.sentPasswordResets.isEmpty())
    }

    @Test
    fun user_flow_sends_the_link_to_a_user_account() = runBlocking {
        val result = env.sendPasswordReset("  Jhony.Ramirez@GMAIL.com ", UserRole.USER)

        assertEquals(PasswordResetResult.Sent, result)
        assertEquals(listOf(TestEnvironment.JHONY_EMAIL), env.auth.sentPasswordResets)
    }

    @Test
    fun moderator_flow_sends_the_link_to_the_moderator_account() = runBlocking {
        val result = env.sendPasswordReset(TestEnvironment.MODERATOR_EMAIL, UserRole.MODERATOR)

        assertEquals(PasswordResetResult.Sent, result)
        assertEquals(listOf(TestEnvironment.MODERATOR_EMAIL), env.auth.sentPasswordResets)
    }

    @Test
    fun moderator_flow_does_not_send_to_a_regular_user_but_answers_the_same() = runBlocking {
        val result = env.sendPasswordReset(TestEnvironment.JHONY_EMAIL, UserRole.MODERATOR)

        assertEquals(PasswordResetResult.Sent, result)
        assertTrue(env.auth.sentPasswordResets.isEmpty())
    }

    @Test
    fun user_flow_does_not_send_to_the_moderator_account() = runBlocking {
        val result = env.sendPasswordReset(TestEnvironment.MODERATOR_EMAIL, UserRole.USER)

        assertEquals(PasswordResetResult.Sent, result)
        assertTrue(env.auth.sentPasswordResets.isEmpty())
    }

    @Test
    fun unknown_email_answers_sent_to_avoid_revealing_registered_accounts() = runBlocking {
        val result = env.sendPasswordReset("nadie@servify.demo")

        assertEquals(PasswordResetResult.Sent, result)
        assertTrue(env.auth.sentPasswordResets.isEmpty())
    }

    @Test
    fun without_a_required_role_any_account_receives_the_link() = runBlocking {
        env.sendPasswordReset(TestEnvironment.JHONY_EMAIL)
        env.sendPasswordReset(TestEnvironment.MODERATOR_EMAIL)

        assertEquals(2, env.auth.sentPasswordResets.size)
    }
}
