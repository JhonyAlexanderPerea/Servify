package co.edu.uniquindio.servify.domain.rules

/** Reglas de credenciales sin dependencias de Android (para poder probarlas con JUnit local). */
object CredentialRules {
    const val MIN_PASSWORD_LENGTH = 8

    private val EMAIL_REGEX = Regex("^[A-Za-z0-9._%+\\-]+@[A-Za-z0-9.\\-]+\\.[A-Za-z]{2,}$")

    fun normalizeEmail(email: String): String = email.trim().lowercase()

    fun isValidEmail(email: String): Boolean = EMAIL_REGEX.matches(normalizeEmail(email))

    fun isStrongEnough(password: String): Boolean = password.length >= MIN_PASSWORD_LENGTH
}
