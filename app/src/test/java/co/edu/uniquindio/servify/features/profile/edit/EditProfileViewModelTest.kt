package co.edu.uniquindio.servify.features.profile.edit

import co.edu.uniquindio.servify.data.repository.memory.InMemorySessionRepository
import co.edu.uniquindio.servify.data.seed.SeedUsers
import co.edu.uniquindio.servify.domain.usecase.user.DeleteUserAccountUseCase
import co.edu.uniquindio.servify.testutil.MainDispatcherRule
import co.edu.uniquindio.servify.testutil.TestEnvironment
import co.edu.uniquindio.servify.testutil.ThrowingAuthRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Rule
import org.junit.Test

class EditProfileViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val env = TestEnvironment()

    private fun viewModel() = EditProfileViewModel(
        observeCurrentUserUseCase = env.observeCurrentUser,
        updateUserProfileUseCase = env.updateUserProfile,
        deleteUserAccountUseCase = env.deleteAccount
    )

    @Test
    fun saving_with_a_blank_name_shows_a_message_and_does_not_update() {
        val viewModel = viewModel()
        viewModel.onNameChanged("")

        viewModel.onSaveProfile()

        assertEquals("El nombre no puede estar vacío", viewModel.uiState.value.userMessage)
        assertEquals("Jhony", runBlocking { env.users.getById(SeedUsers.JHONY_ID)?.name })
    }

    @Test
    fun saving_updates_the_name_and_phone_and_stops_the_loading_state() {
        val viewModel = viewModel()
        viewModel.onNameChanged("  Jhony Actualizado  ")
        viewModel.onPhoneChanged(" 3001234567 ")

        viewModel.onSaveProfile()

        val saved = runBlocking { env.users.getById(SeedUsers.JHONY_ID) }
        assertEquals("Jhony Actualizado", saved?.name)
        assertEquals("3001234567", saved?.phone)
        assertFalse(viewModel.uiState.value.isSaving)
        assertEquals("Perfil actualizado correctamente", viewModel.uiState.value.userMessage)
    }

    @Test
    fun deleting_with_a_blank_password_shows_a_field_error_without_calling_the_use_case() {
        val viewModel = viewModel()
        viewModel.onShowDeleteConfirmation()

        viewModel.onConfirmDeleteAccount()

        assertEquals("Ingresa tu contraseña para confirmar", viewModel.uiState.value.deletePasswordError)
        assertFalse(viewModel.uiState.value.isDeletingAccount)
    }

    @Test
    fun deleting_with_a_wrong_password_shows_the_error_and_keeps_the_session() {
        runBlocking { env.loginAs(TestEnvironment.JHONY_EMAIL, TestEnvironment.JHONY_PASSWORD) }
        val viewModel = viewModel()
        viewModel.onShowDeleteConfirmation()
        viewModel.onDeletePasswordChanged("claveIncorrecta")

        viewModel.onConfirmDeleteAccount()

        assertEquals("Contraseña incorrecta", viewModel.uiState.value.deletePasswordError)
        assertNotNull(runBlocking { env.session.session.first() })
        assertFalse(viewModel.uiState.value.isDeletingAccount)
    }

    @Test
    fun deleting_with_the_correct_password_clears_the_session() {
        runBlocking { env.loginAs(TestEnvironment.JHONY_EMAIL, TestEnvironment.JHONY_PASSWORD) }
        val viewModel = viewModel()
        viewModel.onShowDeleteConfirmation()
        viewModel.onDeletePasswordChanged(TestEnvironment.JHONY_PASSWORD)

        viewModel.onConfirmDeleteAccount()

        assertNull(runBlocking { env.session.session.first() })
        assertNull(env.auth.currentUserSnapshot)
    }

    @Test
    fun a_network_failure_shows_a_generic_message_and_stops_deleting() {
        val viewModel = EditProfileViewModel(
            observeCurrentUserUseCase = env.observeCurrentUser,
            updateUserProfileUseCase = env.updateUserProfile,
            deleteUserAccountUseCase = DeleteUserAccountUseCase(
                ThrowingAuthRepository(),
                InMemorySessionRepository()
            )
        )
        viewModel.onShowDeleteConfirmation()
        viewModel.onDeletePasswordChanged(TestEnvironment.JHONY_PASSWORD)

        viewModel.onConfirmDeleteAccount()

        val state = viewModel.uiState.value
        assertFalse(state.isDeletingAccount)
        assertFalse(state.showDeleteConfirmationDialog)
        assertEquals(
            "No se pudo eliminar la cuenta. Revisa tu conexión e inténtalo de nuevo.",
            state.userMessage
        )
    }
}
