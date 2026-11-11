package co.edu.uniquindio.servify.features.register

import co.edu.uniquindio.servify.data.repository.memory.InMemorySessionRepository
import co.edu.uniquindio.servify.domain.model.UserRole
import co.edu.uniquindio.servify.domain.usecase.user.RegisterUserUseCase
import co.edu.uniquindio.servify.testutil.MainDispatcherRule
import co.edu.uniquindio.servify.testutil.TestEnvironment
import co.edu.uniquindio.servify.testutil.ThrowingAuthRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class RegisterViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val env = TestEnvironment()
    private val viewModel = RegisterViewModel(env.registerUser)

    private fun fillStepOne(email: String = "nuevo@servify.demo") {
        viewModel.onNameChange("Ana")
        viewModel.onLastNameChange("Gómez")
        viewModel.onEmailChange(email)
    }

    private fun fillStepTwo(password: String = "ClaveSegura1") {
        viewModel.onPasswordChange(password)
        viewModel.onConfirmPasswordChange(password)
        viewModel.onCityChange("Armenia, Quindío")
        viewModel.toggleTerms()
    }

    @Test
    fun step_one_needs_name_last_name_and_a_valid_email() {
        assertFalse(viewModel.uiState.value.firstStepValid)

        fillStepOne(email = "correo-invalido")
        assertFalse(viewModel.uiState.value.firstStepValid)

        viewModel.onEmailChange("ana@servify.demo")
        assertTrue(viewModel.uiState.value.firstStepValid)
    }

    @Test
    fun nextStep_only_advances_when_step_one_is_valid() {
        viewModel.nextStep()
        assertEquals(1, viewModel.uiState.value.step)

        fillStepOne()
        viewModel.nextStep()
        assertEquals(2, viewModel.uiState.value.step)
    }

    @Test
    fun step_two_needs_matching_strong_passwords_a_city_and_accepted_terms() {
        fillStepOne()
        viewModel.nextStep()

        viewModel.onPasswordChange("corta")
        viewModel.onConfirmPasswordChange("corta")
        assertFalse(viewModel.uiState.value.secondStepValid)

        fillStepTwo()
        assertTrue(viewModel.uiState.value.secondStepValid)

        viewModel.onConfirmPasswordChange("otraClave123")
        assertFalse(viewModel.uiState.value.secondStepValid)
    }

    @Test
    fun a_valid_registration_creates_the_account_and_opens_the_session() {
        fillStepOne()
        viewModel.nextStep()
        fillStepTwo()
        viewModel.register()

        val saved = runBlocking { env.session.session.first() }
        assertNotNull(runBlocking { env.users.findByEmail("nuevo@servify.demo") })
        assertEquals(env.auth.currentUserSnapshot?.id, saved?.userId)
        assertEquals(UserRole.USER, saved?.role)
        assertEquals("nuevo@servify.demo", env.auth.currentUserSnapshot?.email)
    }

    @Test
    fun a_network_failure_shows_a_generic_message_and_does_not_save_the_session() {
        val session = InMemorySessionRepository()
        val vm = RegisterViewModel(RegisterUserUseCase(ThrowingAuthRepository(), session))
        vm.onNameChange("Ana")
        vm.onLastNameChange("Gómez")
        vm.onEmailChange("nuevo@servify.demo")
        vm.nextStep()
        vm.onPasswordChange("ClaveSegura1")
        vm.onConfirmPasswordChange("ClaveSegura1")
        vm.onCityChange("Armenia, Quindío")
        vm.toggleTerms()

        vm.register()

        val state = vm.uiState.value
        assertFalse(state.isLoading)
        assertEquals(
            "No se pudo completar el registro. Revisa tu conexión e inténtalo de nuevo.",
            state.registerError
        )
        assertNull(runBlocking { session.session.first() })
    }

    @Test
    fun a_duplicated_email_sends_the_user_back_to_step_one_with_the_error() {
        fillStepOne(email = TestEnvironment.JHONY_EMAIL)
        viewModel.nextStep()
        fillStepTwo()
        viewModel.register()

        val state = viewModel.uiState.value
        assertEquals(1, state.step)
        assertEquals("Ya existe una cuenta con este correo", state.emailError)
        assertNull(runBlocking { env.session.session.first() })
    }
}
