package co.edu.uniquindio.servify.domain.model

enum class AuthError {
    /** Email o contraseña incorrectos. */
    INVALID_CREDENTIALS,

    /** La cuenta existe pero no tiene el rol requerido (p. ej. un usuario entrando por el login de moderador). */
    ROLE_NOT_ALLOWED,

    EMAIL_ALREADY_REGISTERED,
    INVALID_EMAIL,
    WEAK_PASSWORD,
    MISSING_FIELDS,

    /** La cuenta existe, pero el correo aún no se ha verificado (los moderadores están exentos). */
    EMAIL_NOT_VERIFIED
}

sealed interface AuthResult {
    data class Success(val user: User) : AuthResult
    data class Failure(val error: AuthError) : AuthResult

    /** El registro terminó, pero el correo debe verificarse antes de iniciar sesión (no hay sesión abierta). */
    data class VerificationRequired(val email: String) : AuthResult
}
