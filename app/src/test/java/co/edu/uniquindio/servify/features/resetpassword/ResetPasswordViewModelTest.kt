package co.edu.uniquindio.servify.features.resetpassword

import co.edu.uniquindio.servify.data.repository.InMemoryAuthRepository
import co.edu.uniquindio.servify.data.repository.InMemoryUserRepository
import co.edu.uniquindio.servify.data.seed.SeedUsers
import co.edu.uniquindio.servify.domain.usecase.auth.ConfirmPasswordResetUseCase
import co.edu.uniquindio.servify.testutil.MainDispatcherRule
import co.edu.uniquindio.servify.testutil.ThrowingAuthRepository
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class ResetPasswordViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private fun newViewModel(): ResetPasswordViewModel {
        val repository = InMemoryAuthRepository(
            InMemoryUserRepository(SeedUsers.users),
            SeedUsers.credentials,
            clock = { 1_000L },
            resetDelayMillis = 0L
        )
        return ResetPasswordViewModel(ConfirmPasswordResetUseCase(repository))
    }

    private fun ResetPasswordViewModel.fillValidForm() {
        onPasswordChange("Servify2026")
        onConfirmPasswordChange("Servify2026")
    }

    @Test
    fun a_weak_password_shows_an_error_and_blocks_the_form() {
        val viewModel = newViewModel()

        viewModel.onPasswordChange("123")

        assertNotNull(viewModel.uiState.value.passwordError)
        assertFalse(viewModel.uiState.value.isValid)
    }

    @Test
    fun different_passwords_show_an_error_and_block_the_form() {
        val viewModel = newViewModel()

        viewModel.onPasswordChange("Servify2026")
        viewModel.onConfirmPasswordChange("Servify2027")

        assertNotNull(viewModel.uiState.value.confirmPasswordError)
        assertFalse(viewModel.uiState.value.isValid)
    }

    @Test
    fun a_valid_form_with_a_valid_code_marks_the_reset_as_successful() {
        val viewModel = newViewModel()
        viewModel.fillValidForm()

        viewModel.onSubmit("codigo-valido")

        val state = viewModel.uiState.value
        assertTrue(state.success)
        assertFalse(state.isLoading)
        assertFalse(state.invalidCode)
    }

    @Test
    fun an_invalid_or_expired_code_shows_the_expected_message() {
        val viewModel = newViewModel()
        viewModel.fillValidForm()

        viewModel.onSubmit("")

        val state = viewModel.uiState.value
        assertFalse(state.success)
        assertTrue(state.invalidCode)
        assertEquals("El enlace no es válido o ya venció. Solicita uno nuevo.", state.message)
    }

    @Test
    fun a_network_failure_shows_a_generic_message_and_stops_loading() {
        val viewModel = ResetPasswordViewModel(
            ConfirmPasswordResetUseCase(ThrowingAuthRepository())
        )
        viewModel.fillValidForm()

        viewModel.onSubmit("codigo-valido")

        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertFalse(state.success)
        assertFalse(state.invalidCode)
        assertEquals(
            "No se pudo restablecer la contraseña. Revisa tu conexión e inténtalo de nuevo.",
            state.message
        )
    }

    @Test
    fun password_visibility_toggles() {
        val viewModel = newViewModel()

        viewModel.togglePasswordVisibility()

        assertTrue(viewModel.uiState.value.showPassword)
    }
}
