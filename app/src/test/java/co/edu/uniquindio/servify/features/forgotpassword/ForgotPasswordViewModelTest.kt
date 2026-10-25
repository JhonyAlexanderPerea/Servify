package co.edu.uniquindio.servify.features.forgotpassword

import androidx.lifecycle.SavedStateHandle
import co.edu.uniquindio.servify.domain.model.UserRole
import co.edu.uniquindio.servify.testutil.MainDispatcherRule
import co.edu.uniquindio.servify.testutil.TestEnvironment
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class ForgotPasswordViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val env = TestEnvironment()

    private fun viewModelFor(role: String?): ForgotPasswordViewModel {
        val args = if (role == null) emptyMap() else mapOf(ForgotPasswordViewModel.ARG_ROLE to role)
        return ForgotPasswordViewModel(SavedStateHandle(args), env.sendPasswordReset)
    }

    @Test
    fun the_role_comes_from_the_navigation_argument_and_defaults_to_user() {
        assertEquals(UserRole.MODERATOR, viewModelFor("MODERATOR").uiState.value.role)
        assertEquals(UserRole.USER, viewModelFor("USER").uiState.value.role)
        assertEquals(UserRole.USER, viewModelFor(null).uiState.value.role)
        assertEquals(UserRole.USER, viewModelFor("ADMIN").uiState.value.role)
    }

    @Test
    fun validates_the_email_format_while_typing() {
        val vm = viewModelFor("USER")

        vm.onEmailChange("")
        assertEquals("El correo es obligatorio", vm.uiState.value.error)

        vm.onEmailChange("correo-malo")
        assertEquals("Correo electrónico inválido", vm.uiState.value.error)
        assertFalse(vm.uiState.value.canSend)

        vm.onEmailChange(TestEnvironment.JHONY_EMAIL)
        assertNull(vm.uiState.value.error)
        assertTrue(vm.uiState.value.canSend)
    }

    @Test
    fun an_invalid_email_is_never_sent() {
        val vm = viewModelFor("USER")
        vm.onEmailChange("correo-malo")
        vm.sendRecoveryEmail()

        assertFalse(vm.uiState.value.sent)
        assertTrue(env.auth.sentPasswordResets.isEmpty())
    }

    @Test
    fun user_flow_simulates_the_send_and_shows_the_sent_state() {
        val vm = viewModelFor("USER")
        vm.onEmailChange(TestEnvironment.JHONY_EMAIL)
        vm.sendRecoveryEmail()

        assertTrue(vm.uiState.value.sent)
        assertFalse(vm.uiState.value.isLoading)
        assertEquals(listOf(TestEnvironment.JHONY_EMAIL), env.auth.sentPasswordResets)
    }

    @Test
    fun moderator_flow_sends_only_to_moderator_accounts() {
        val vm = viewModelFor("MODERATOR")
        vm.onEmailChange(TestEnvironment.MODERATOR_EMAIL)
        vm.sendRecoveryEmail()

        assertTrue(vm.uiState.value.sent)
        assertEquals(listOf(TestEnvironment.MODERATOR_EMAIL), env.auth.sentPasswordResets)
    }

    @Test
    fun moderator_flow_with_a_user_email_looks_identical_but_sends_nothing() {
        val vm = viewModelFor("MODERATOR")
        vm.onEmailChange(TestEnvironment.JHONY_EMAIL)
        vm.sendRecoveryEmail()

        assertTrue(vm.uiState.value.sent)
        assertTrue(env.auth.sentPasswordResets.isEmpty())
    }

    @Test
    fun resend_sends_again_to_the_same_email_and_flags_it() {
        val vm = viewModelFor("USER")
        vm.onEmailChange(TestEnvironment.JHONY_EMAIL)
        vm.sendRecoveryEmail()
        vm.resend()

        assertTrue(vm.uiState.value.resent)
        assertEquals(2, env.auth.sentPasswordResets.size)
    }
}
