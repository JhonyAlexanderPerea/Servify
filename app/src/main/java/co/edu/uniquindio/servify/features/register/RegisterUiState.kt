package co.edu.uniquindio.servify.features.register

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
                    email.isNotBlank() &&
                    android.util.Patterns.EMAIL_ADDRESS
                        .matcher(email)
                        .matches()

    val secondStepValid: Boolean
        get() =
            password.length >= 8 &&
                    password == confirmPassword &&
                    city.isNotBlank() &&
                    termsAccepted
}