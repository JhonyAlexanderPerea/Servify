package co.edu.uniquindio.servify.domain.model

/**
 * Resultado de solicitar el enlace de recuperación de contraseña.
 *
 * Nota de seguridad: [Sent] se devuelve aunque no exista una cuenta con ese correo (o tenga otro
 * rol). Responder distinto permitiría descubrir qué correos están registrados. El envío real
 * (Firebase `sendPasswordResetEmail`) se comporta igual.
 */
sealed interface PasswordResetResult {
    data object Sent : PasswordResetResult
    data object InvalidEmail : PasswordResetResult
}
