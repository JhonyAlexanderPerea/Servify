package co.edu.uniquindio.servify.features.profile.edit

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.edu.uniquindio.servify.domain.usecase.auth.LogoutUseCase
import co.edu.uniquindio.servify.domain.usecase.user.DeleteUserAccountUseCase
import co.edu.uniquindio.servify.domain.usecase.user.ObserveCurrentUserUseCase
import co.edu.uniquindio.servify.domain.usecase.user.UpdateUserProfileUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EditProfileViewModel @Inject constructor(
    private val observeCurrentUserUseCase: ObserveCurrentUserUseCase,
    private val updateUserProfileUseCase: UpdateUserProfileUseCase,
    private val deleteUserAccountUseCase: DeleteUserAccountUseCase,
    private val logoutUseCase: LogoutUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(EditProfileUiState())
    val uiState: StateFlow<EditProfileUiState> = _uiState.asStateFlow()

    init {
        loadCurrentUser()
    }

    private fun loadCurrentUser() {
        viewModelScope.launch {
            observeCurrentUserUseCase().collect { user ->
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
    }

    fun onNameChanged(name: String) {
        _uiState.update { it.copy(name = name) }
    }

    fun onPhoneChanged(phone: String) {
        _uiState.update { it.copy(phone = phone) }
    }

    fun onSaveProfile() {
        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true) }
            try {
                val currentState = _uiState.value
                observeCurrentUserUseCase().collect { user ->
                    val updatedUser = user.copy(
                        name = currentState.name,
                        phone = currentState.phone
                    )
                    updateUserProfileUseCase(updatedUser)
                    _uiState.update {
                        it.copy(
                            isSaving = false,
                            userMessage = "Perfil actualizado correctamente"
                        )
                    }
                    return@collect
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isSaving = false,
                        userMessage = "Error al actualizar el perfil"
                    )
                }
            }
        }
    }

    fun onShowDeleteConfirmation() {
        _uiState.update { it.copy(showDeleteConfirmationDialog = true) }
    }

    fun onDismissDeleteConfirmation() {
        _uiState.update { it.copy(showDeleteConfirmationDialog = false) }
    }

    fun onConfirmDeleteAccount() {
        viewModelScope.launch {
            _uiState.update { it.copy(isDeletingAccount = true) }
            try {
                deleteUserAccountUseCase()
                _uiState.update { it.copy(showDeleteConfirmationDialog = false) }
                logoutUseCase()
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isDeletingAccount = false,
                        userMessage = "Error al eliminar la cuenta"
                    )
                }
            }
        }
    }

    fun clearMessage() {
        _uiState.update { it.copy(userMessage = null) }
    }
}