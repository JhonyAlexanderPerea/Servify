package co.edu.uniquindio.servify.domain

import co.edu.uniquindio.servify.domain.rules.CredentialRules
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class CredentialRulesTest {

    @Test
    fun accepts_common_valid_emails() {
        listOf("jhony.ramirez@gmail.com", "a+b@servify.co", "x_y-z@uniquindio.edu.co")
            .forEach { assertTrue(it, CredentialRules.isValidEmail(it)) }
    }

    @Test
    fun rejects_malformed_emails() {
        listOf("", "   ", "sin-arroba", "@servify.co", "a@", "a@b", "a b@servify.co", "a@servify..")
            .forEach { assertFalse("\"$it\" no debería ser válido", CredentialRules.isValidEmail(it)) }
    }

    @Test
    fun email_is_validated_after_trimming_and_lowercasing() {
        assertTrue(CredentialRules.isValidEmail("  Jhony.Ramirez@GMAIL.com  "))
        assertEquals("jhony.ramirez@gmail.com", CredentialRules.normalizeEmail("  Jhony.Ramirez@GMAIL.com "))
    }

    @Test
    fun password_needs_at_least_eight_characters() {
        assertFalse(CredentialRules.isStrongEnough("1234567"))
        assertTrue(CredentialRules.isStrongEnough("12345678"))
    }
}
