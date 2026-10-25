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
    /** true cuando la cuenta fue creada y la sesión quedó abierta. */
    val registered: Boolean = false
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