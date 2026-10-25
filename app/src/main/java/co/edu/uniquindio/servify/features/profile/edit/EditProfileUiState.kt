package co.edu.uniquindio.servify.features.profile.edit

data class EditProfileUiState(
    val name: String = "",
    val email: String = "",
    val phone: String = "",
    val avatarUrl: String? = null,
    val isSaving: Boolean = false,
    val showDeleteConfirmationDialog: Boolean = false,
    val isDeletingAccount: Boolean = false,
    val userMessage: String? = null
)