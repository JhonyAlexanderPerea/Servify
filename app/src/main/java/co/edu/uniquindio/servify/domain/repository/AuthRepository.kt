package co.edu.uniquindio.servify.domain.repository

import co.edu.uniquindio.servify.domain.model.AuthResult
import co.edu.uniquindio.servify.domain.model.PasswordResetResult
import co.edu.uniquindio.servify.domain.model.User
import co.edu.uniquindio.servify.domain.model.UserRole
import kotlinx.coroutines.flow.Flow

/** Sesión y roles. En la fase 3 se reemplaza por una implementación con Firebase Auth sin tocar los ViewModels. */
interface AuthRepository {

    /** Usuario con sesión activa (se actualiza si cambian sus puntos/insignias); null si no hay sesión. */
    val currentUser: Flow<User?>

    /** Lectura síncrona de la sesión actual. */
    val currentUserSnapshot: User?

    /**
     * Valida contra los usuarios registrados. Si [requiredRole] no es null y la cuenta
     * tiene otro rol, falla con ROLE_NOT_ALLOWED y NO abre sesión (login de moderador).
     */
    suspend fun login(email: String, password: String, requiredRole: UserRole? = null): AuthResult

    /** Crea un usuario con rol USER y abre sesión. */
    suspend fun register(
        name: String,
        lastName: String,
        email: String,
        password: String,
        city: String
    ): AuthResult

    /**
     * Solicita el enlace de recuperación. Si [requiredRole] no es null, solo las cuentas con ese rol
     * reciben el enlace (el flujo de moderador no puede usarse para recuperar una cuenta de usuario,
     * ni al revés). Ver [PasswordResetResult] sobre por qué el resultado no revela si la cuenta existe.
     */
    suspend fun sendPasswordReset(email: String, requiredRole: UserRole? = null): PasswordResetResult

    /** Cierra la sesión y limpia el estado. */
    fun logout()
}
