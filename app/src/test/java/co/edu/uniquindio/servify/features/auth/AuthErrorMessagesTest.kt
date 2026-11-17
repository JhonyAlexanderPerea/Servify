package co.edu.uniquindio.servify.features.auth

import co.edu.uniquindio.servify.domain.model.AuthError
import org.junit.Assert.assertTrue
import org.junit.Test

class AuthErrorMessagesTest {

    @Test
    fun an_unverified_email_has_a_message_for_the_user() {
        assertTrue(AuthError.EMAIL_NOT_VERIFIED.toMessage().isNotBlank())
    }
}
