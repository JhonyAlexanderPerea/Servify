package co.edu.uniquindio.servify.domain.repository

import co.edu.uniquindio.servify.domain.model.AuthResult
import co.edu.uniquindio.servify.domain.model.ConfirmPasswordResetResult
import co.edu.uniquindio.servify.domain.model.DeleteAccountResult
import co.edu.uniquindio.servify.domain.model.PasswordResetResult
import co.edu.uniquindio.servify.domain.model.User
import co.edu.uniquindio.servify.domain.model.UserRole
import kotlinx.coroutines.flow.Flow

/** Sesión y roles. En la fase 3 se reemplaza por una implementación con Firebase Auth sin tocar los ViewModels. */
interface AuthRepository {

    /** Usuario con sesión activa (se actualiza si cambian sus puntos/insignias); null si no hay sesión. */
    val currentUser: Flow<User?>

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

    /**
     * Confirma el restablecimiento con el [oobCode] del enlace enviado por Firebase. No revela si el
     * código existe: el resultado distingue código inválido o vencido de contraseña débil.
     */
    suspend fun confirmPasswordReset(oobCode: String, newPassword: String): ConfirmPasswordResetResult

    /**
     * Elimina la cuenta autenticada tras reautenticar con [password]. Si la contraseña no
     * coincide devuelve WRONG_PASSWORD sin borrar nada.
     */
    suspend fun deleteAccount(password: String): DeleteAccountResult

    /** Cierra la sesión y limpia el estado. */
    fun logout()
}
