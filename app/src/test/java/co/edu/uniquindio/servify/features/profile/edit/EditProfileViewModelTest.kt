package co.edu.uniquindio.servify.features.profile.edit

import co.edu.uniquindio.servify.data.repository.memory.InMemorySessionRepository
import co.edu.uniquindio.servify.data.seed.SeedUsers
import co.edu.uniquindio.servify.domain.usecase.user.DeleteUserAccountUseCase
import co.edu.uniquindio.servify.domain.usecase.user.UploadProfilePhotoUseCase
import co.edu.uniquindio.servify.testutil.MainDispatcherRule
import co.edu.uniquindio.servify.testutil.TestEnvironment
import co.edu.uniquindio.servify.testutil.ThrowingAuthRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import java.io.IOException

class EditProfileViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val env = TestEnvironment()

    private fun viewModel() = EditProfileViewModel(
        observeCurrentUserUseCase = env.observeCurrentUser,
        updateUserProfileUseCase = env.updateUserProfile,
        deleteUserAccountUseCase = env.deleteAccount,
        uploadProfilePhotoUseCase = env.uploadProfilePhoto,
        removeProfilePhotoUseCase = env.removeProfilePhoto
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

        runBlocking {
            withTimeout(2_000) {
                viewModel.uiState.first { it.userMessage == "Perfil actualizado correctamente" }
            }
        }

        val saved = runBlocking { env.users.getById(SeedUsers.JHONY_ID) }
        assertEquals("Jhony Actualizado", saved?.name)
        assertEquals("3001234567", saved?.phone)
        assertFalse(viewModel.uiState.value.isSaving)
        assertEquals("Perfil actualizado correctamente", viewModel.uiState.value.userMessage)
    }

    @Test
    fun selecting_a_photo_uploads_it_and_persists_the_new_avatar_url() {
        val viewModel = viewModel()

        viewModel.onPhotoSelected("content://galeria/foto.jpg")

        val state = viewModel.uiState.value
        assertFalse(state.isUploadingPhoto)
        assertEquals("Foto actualizada", state.userMessage)
        assertNotNull(state.avatarUrl)
        val saved = runBlocking { env.users.getById(SeedUsers.JHONY_ID) }
        assertEquals(state.avatarUrl, saved?.avatarUrl)
        val (localUri, remotePath) = env.images.uploads.single()
        assertEquals("content://galeria/foto.jpg", localUri)
        assertTrue(remotePath.startsWith("servify/avatars/${SeedUsers.JHONY_ID}/"))
    }

    @Test
    fun removing_the_photo_restores_the_default_avatar() {
        val viewModel = viewModel()
        viewModel.onPhotoSelected("content://galeria/foto.jpg")
        assertNotNull(viewModel.uiState.value.avatarUrl)

        viewModel.onRemovePhoto()

        val state = viewModel.uiState.value
        assertNull(state.avatarUrl)
        assertFalse(state.isRemovingPhoto)
        assertEquals("Foto de perfil restablecida", state.userMessage)
        assertNull(runBlocking { env.users.getById(SeedUsers.JHONY_ID)?.avatarUrl })
    }

    @Test
    fun a_failed_photo_upload_shows_a_message_and_stops_the_loading_state() {
        val viewModel = EditProfileViewModel(
            observeCurrentUserUseCase = env.observeCurrentUser,
            updateUserProfileUseCase = env.updateUserProfile,
            deleteUserAccountUseCase = env.deleteAccount,
            uploadProfilePhotoUseCase = UploadProfilePhotoUseCase(
                imageRepository = object : co.edu.uniquindio.servify.domain.repository.ImageRepository {
                    override suspend fun uploadImage(localUri: String, remotePath: String): String =
                        throw IOException("sin red")
                },
                userRepository = env.users
            ),
            removeProfilePhotoUseCase = env.removeProfilePhoto
        )

        viewModel.onPhotoSelected("content://galeria/foto.jpg")

        val state = viewModel.uiState.value
        assertFalse(state.isUploadingPhoto)
        assertNull(state.avatarUrl)
        assertEquals(
            "No se pudo subir la foto. Revisa tu conexión e inténtalo de nuevo.",
            state.userMessage
        )
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
            ),
            uploadProfilePhotoUseCase = env.uploadProfilePhoto,
            removeProfilePhotoUseCase = env.removeProfilePhoto
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
