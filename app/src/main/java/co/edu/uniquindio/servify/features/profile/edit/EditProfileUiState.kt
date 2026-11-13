package co.edu.uniquindio.servify.features.profile.edit

data class EditProfileUiState(
    val name: String = "",
    val email: String = "",
    val phone: String = "",
    val avatarUrl: String? = null,
    val isUploadingPhoto: Boolean = false,
    val isRemovingPhoto: Boolean = false,
    val isSaving: Boolean = false,
    val showDeleteConfirmationDialog: Boolean = false,
    val isDeletingAccount: Boolean = false,
    val deletePassword: String = "",
    val deletePasswordError: String? = null,
    val userMessage: String? = null
)