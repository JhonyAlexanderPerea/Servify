package co.edu.uniquindio.servify.features.login

import co.edu.uniquindio.servify.data.repository.memory.InMemorySessionRepository
import co.edu.uniquindio.servify.domain.model.UserRole
import co.edu.uniquindio.servify.domain.usecase.auth.LoginUserUseCase
import co.edu.uniquindio.servify.testutil.MainDispatcherRule
import co.edu.uniquindio.servify.testutil.TestEnvironment
import co.edu.uniquindio.servify.testutil.ThrowingAuthRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
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
    fun user_login_saves_the_session_with_the_user_role() {
        viewModel.onEmailChange(TestEnvironment.JHONY_EMAIL)
        viewModel.onPasswordChange(TestEnvironment.JHONY_PASSWORD)
        viewModel.login()

        val saved = runBlocking { env.session.session.first() }
        assertEquals(env.auth.currentUserSnapshot?.id, saved?.userId)
        assertEquals(UserRole.USER, saved?.role)
        assertFalse(viewModel.uiState.value.isLoading)
    }

    @Test
    fun moderator_login_saves_the_moderator_session() {
        viewModel.onEmailChange(TestEnvironment.MODERATOR_EMAIL)
        viewModel.onPasswordChange("Moderador2026")
        viewModel.login()

        val saved = runBlocking { env.session.session.first() }
        assertEquals(UserRole.MODERATOR, saved?.role)
    }

    @Test
    fun wrong_password_shows_the_error_and_does_not_open_a_session() {
        viewModel.onEmailChange(TestEnvironment.JHONY_EMAIL)
        viewModel.onPasswordChange("claveIncorrecta")
        viewModel.login()

        assertEquals("Correo o contraseña incorrectos", viewModel.uiState.value.loginError)
        assertNull(runBlocking { env.session.session.first() })
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
    fun a_network_failure_shows_a_generic_message_and_does_not_open_a_session() {
        val session = InMemorySessionRepository()
        val vm = LoginViewModel(LoginUserUseCase(ThrowingAuthRepository(), session))
        vm.onEmailChange(TestEnvironment.JHONY_EMAIL)
        vm.onPasswordChange(TestEnvironment.JHONY_PASSWORD)

        vm.login()

        val state = vm.uiState.value
        assertFalse(state.isLoading)
        assertEquals(
            "No se pudo iniciar sesión. Revisa tu conexión e inténtalo de nuevo.",
            state.loginError
        )
        assertNull(runBlocking { session.session.first() })
    }

    @Test
    fun password_visibility_and_remember_me_toggle() {
        viewModel.togglePasswordVisibility()
        viewModel.toggleRememberMe()

        assertTrue(viewModel.uiState.value.showPassword)
        assertTrue(viewModel.uiState.value.rememberMe)
    }
}
