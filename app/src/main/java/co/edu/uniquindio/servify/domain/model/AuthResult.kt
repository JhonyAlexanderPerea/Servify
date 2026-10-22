package co.edu.uniquindio.servify.domain.model

enum class AuthError {
    /** Email o contraseña incorrectos. */
    INVALID_CREDENTIALS,

    /** La cuenta existe pero no tiene el rol requerido (p. ej. un usuario entrando por el login de moderador). */
    ROLE_NOT_ALLOWED,

    EMAIL_ALREADY_REGISTERED,
    INVALID_EMAIL,
    WEAK_PASSWORD,
    MISSING_FIELDS
}

sealed interface AuthResult {
    data class Success(val user: User) : AuthResult
    data class Failure(val error: AuthError) : AuthResult
}
