package co.edu.uniquindio.servify.features.resetpassword

import co.edu.uniquindio.servify.domain.rules.CredentialRules

data class ResetPasswordUiState(
    val password: String = "",
    val confirmPassword: String = "",
    val showPassword: Boolean = false,
    val isLoading: Boolean = false,
    val passwordError: String? = null,
    val confirmPasswordError: String? = null,
    val message: String? = null,
    val success: Boolean = false,
    val invalidCode: Boolean = false
) {

    val isValid: Boolean
        get() =
            CredentialRules.isStrongEnough(password) &&
                    password.isNotEmpty() &&
                    password == confirmPassword &&
                    passwordError == null &&
                    confirmPasswordError == null
}
