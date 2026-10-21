package co.edu.uniquindio.servify.features.auth

import co.edu.uniquindio.servify.domain.model.AuthError

/** Textos de error de autenticación para la UI (el dominio solo conoce el código del error). */
fun AuthError.toMessage(): String = when (this) {
    AuthError.INVALID_CREDENTIALS -> "Correo o contraseña incorrectos"
    AuthError.ROLE_NOT_ALLOWED -> "Esta cuenta no tiene acceso de moderador"
    AuthError.EMAIL_ALREADY_REGISTERED -> "Ya existe una cuenta con este correo"
    AuthError.INVALID_EMAIL -> "Correo electrónico inválido"
    AuthError.WEAK_PASSWORD -> "La contraseña debe tener mínimo 8 caracteres"
    AuthError.MISSING_FIELDS -> "Completa todos los campos"
}
