package co.edu.uniquindio.servify.features.register

import co.edu.uniquindio.servify.domain.rules.CredentialRules

data class RegisterUiState(
    val step: Int = 1,

    val name: String = "",
    val lastName: String = "",
    val email: String = "",

    val password: String = "",
    val confirmPassword: String = "",
    val city: String = "",

    val termsAccepted: Boolean = false,

    val isLoading: Boolean = false,
    val emailError: String? = null,
    val registerError: String? = null,
    /** Correo al que se envió el enlace de verificación; si no es null, se muestra la vista de verificación. */
    val verificationEmail: String? = null
) {

    val firstStepValid: Boolean
        get() =
            name.isNotBlank() &&
                    lastName.isNotBlank() &&
                    CredentialRules.isValidEmail(email)

    val secondStepValid: Boolean
        get() =
            CredentialRules.isStrongEnough(password) &&
                    password == confirmPassword &&
                    city.isNotBlank() &&
                    termsAccepted
}