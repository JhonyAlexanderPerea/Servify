package co.edu.uniquindio.servify.features.login

import co.edu.uniquindio.servify.domain.model.UserRole
import co.edu.uniquindio.servify.testutil.MainDispatcherRule
import co.edu.uniquindio.servify.testutil.TestEnvironment
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class LoginViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val env = TestEnvironment()
    private val viewModel = LoginViewModel(env.loginUser)

    @Test
    fun starts_empty_without_preloaded_credentials() {
        val state = viewModel.uiState.value

        assertEquals("", state.email)
        assertEquals("", state.password)
        assertFalse(state.isValid)
    }

    @Test
    fun a_malformed_email_shows_an_error_and_blocks_the_form() {
        viewModel.onEmailChange("no-es-un-correo")
        viewModel.onPasswordChange("12345678")

        assertEquals("Correo electrónico inválido", viewModel.uiState.value.emailError)
        assertFalse(viewModel.uiState.value.isValid)
    }

    @Test
    fun an_empty_email_is_required() {
        viewModel.onEmailChange("")

        assertEquals("El correo es obligatorio", viewModel.uiState.value.emailError)
    }

    @Test
    fun a_short_password_shows_an_error() {
        viewModel.onPasswordChange("1234567")

        assertEquals("Mínimo 8 caracteres", viewModel.uiState.value.passwordError)
    }

    @Test
    fun valid_input_enables_the_form() {
        viewModel.onEmailChange(TestEnvironment.JHONY_EMAIL)
        viewModel.onPasswordChange(TestEnvironment.JHONY_PASSWORD)

        assertTrue(viewModel.uiState.value.isValid)
    }

    @Test
    fun user_login_reports_the_user_role() {
        viewModel.onEmailChange(TestEnvironment.JHONY_EMAIL)
        viewModel.onPasswordChange(TestEnvironment.JHONY_PASSWORD)
        viewModel.login()

        assertEquals(UserRole.USER, viewModel.uiState.value.loggedInRole)
        assertFalse(viewModel.uiState.value.isLoading)
    }

    @Test
    fun moderator_login_reports_the_moderator_role_so_the_screen_routes_to_the_panel() {
        viewModel.onEmailChange(TestEnvironment.MODERATOR_EMAIL)
        viewModel.onPasswordChange("Moderador2026")
        viewModel.login()

        assertEquals(UserRole.MODERATOR, viewModel.uiState.value.loggedInRole)
    }

    @Test
    fun wrong_password_shows_the_error_and_does_not_open_a_session() {
        viewModel.onEmailChange(TestEnvironment.JHONY_EMAIL)
        viewModel.onPasswordChange("claveIncorrecta")
        viewModel.login()

        assertEquals("Correo o contraseña incorrectos", viewModel.uiState.value.loginError)
        assertNull(viewModel.uiState.value.loggedInRole)
        assertNull(env.auth.currentUserSnapshot)
    }

    @Test
    fun editing_a_field_clears_the_previous_login_error() {
        viewModel.onEmailChange(TestEnvironment.JHONY_EMAIL)
        viewModel.onPasswordChange("claveIncorrecta")
        viewModel.login()
        viewModel.onPasswordChange("claveIncorrecta2")

        assertNull(viewModel.uiState.value.loginError)
    }

    @Test
    fun login_is_ignored_while_the_form_is_invalid() {
        viewModel.onEmailChange("no-es-un-correo")
        viewModel.login()

        assertNull(env.auth.currentUserSnapshot)
        assertFalse(viewModel.uiState.value.isLoading)
    }

    @Test
    fun password_visibility_and_remember_me_toggle() {
        viewModel.togglePasswordVisibility()
        viewModel.toggleRememberMe()

        assertTrue(viewModel.uiState.value.showPassword)
        assertTrue(viewModel.uiState.value.rememberMe)
    }
}
