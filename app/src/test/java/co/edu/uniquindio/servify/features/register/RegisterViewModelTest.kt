package co.edu.uniquindio.servify.features.register

import co.edu.uniquindio.servify.testutil.MainDispatcherRule
import co.edu.uniquindio.servify.testutil.TestEnvironment
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
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

        assertTrue(viewModel.uiState.value.registered)
        assertNotNull(env.users.findByEmail("nuevo@servify.demo"))
        assertEquals("nuevo@servify.demo", env.auth.currentUserSnapshot?.email)
    }

    @Test
    fun a_duplicated_email_sends_the_user_back_to_step_one_with_the_error() {
        fillStepOne(email = TestEnvironment.JHONY_EMAIL)
        viewModel.nextStep()
        fillStepTwo()
        viewModel.register()

        val state = viewModel.uiState.value
        assertFalse(state.registered)
        assertEquals(1, state.step)
        assertEquals("Ya existe una cuenta con este correo", state.emailError)
    }
}
