package co.edu.uniquindio.servify.features.profile.edit

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.edu.uniquindio.servify.domain.model.DeleteAccountResult
import co.edu.uniquindio.servify.domain.usecase.user.DeleteUserAccountUseCase
import co.edu.uniquindio.servify.domain.usecase.user.ObserveCurrentUserUseCase
import co.edu.uniquindio.servify.domain.usecase.user.UpdateUserProfileUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EditProfileViewModel @Inject constructor(
    private val observeCurrentUserUseCase: ObserveCurrentUserUseCase,
    private val updateUserProfileUseCase: UpdateUserProfileUseCase,
    private val deleteUserAccountUseCase: DeleteUserAccountUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(EditProfileUiState())
    val uiState: StateFlow<EditProfileUiState> = _uiState.asStateFlow()

    init {
        loadCurrentUser()
    }

    private fun loadCurrentUser() {
        viewModelScope.launch {
            // first(): el formulario se rellena UNA sola vez y no pisa lo que el usuario escriba
            val user = observeCurrentUserUseCase().first()
            _uiState.update {
                it.copy(
                    name = user.name,
                    email = user.email,
                    phone = user.phone,
                    avatarUrl = user.avatarUrl
                )
            }
        }
    }

    fun onNameChanged(name: String) {
        _uiState.update { it.copy(name = name) }
    }

    fun onPhoneChanged(phone: String) {
        _uiState.update { it.copy(phone = phone) }
    }

    fun onSaveProfile() {
        val state = _uiState.value
        if (state.name.isBlank()) {
            // TODO(i18n): mover a strings.xml
            _uiState.update { it.copy(userMessage = "El nombre no puede estar vacío") }
            return
        }
        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true) }
            runCatching {
                // first(): toma el usuario actual UNA sola vez (con collect el flujo no termina nunca)
                val user = observeCurrentUserUseCase().first()
                updateUserProfileUseCase(user.copy(name = state.name.trim(), phone = state.phone.trim()))
            }.fold(
                onSuccess = {
                    _uiState.update {
                        it.copy(
                            isSaving = false,
                            userMessage = "Perfil actualizado correctamente"
                        )
                    }
                },
                onFailure = {
                    // TODO(i18n): mover a strings.xml
                    _uiState.update {
                        it.copy(
                            isSaving = false,
                            userMessage = "No se pudo actualizar el perfil. Revisa tu conexión e inténtalo de nuevo."
                        )
                    }
                }
            )
        }
    }

    fun onShowDeleteConfirmation() {
        _uiState.update {
            it.copy(
                showDeleteConfirmationDialog = true,
                deletePassword = "",
                deletePasswordError = null
            )
        }
    }

    fun onDismissDeleteConfirmation() {
        _uiState.update {
            it.copy(
                showDeleteConfirmationDialog = false,
                deletePassword = "",
                deletePasswordError = null
            )
        }
    }

    fun onDeletePasswordChanged(value: String) {
        _uiState.update {
            it.copy(
                deletePassword = value,
                deletePasswordError = null
            )
        }
    }

    fun onConfirmDeleteAccount() {
        val password = _uiState.value.deletePassword
        if (password.isBlank()) {
            // TODO(i18n): mover a strings.xml
            _uiState.update { it.copy(deletePasswordError = "Ingresa tu contraseña para confirmar") }
            return
        }
        viewModelScope.launch {
            _uiState.update { it.copy(isDeletingAccount = true, deletePasswordError = null) }
            runCatching { deleteUserAccountUseCase(password) }.fold(
                onSuccess = { result ->
                    when (result) {
                        // La sesión ya se borró: la navegación saca al usuario de esta pantalla
                        DeleteAccountResult.SUCCESS -> Unit
                        DeleteAccountResult.WRONG_PASSWORD -> _uiState.update {
                            it.copy(isDeletingAccount = false, deletePasswordError = "Contraseña incorrecta")
                        }
                    }
                },
                onFailure = {
                    // TODO(i18n): mover a strings.xml
                    _uiState.update {
                        it.copy(
                            isDeletingAccount = false,
                            showDeleteConfirmationDialog = false,
                            userMessage = "No se pudo eliminar la cuenta. Revisa tu conexión e inténtalo de nuevo."
                        )
                    }
                }
            )
        }
    }

    fun clearMessage() {
        _uiState.update { it.copy(userMessage = null) }
    }
}
