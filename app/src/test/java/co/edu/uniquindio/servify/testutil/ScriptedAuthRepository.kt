package co.edu.uniquindio.servify.testutil

import co.edu.uniquindio.servify.domain.model.AuthError
import co.edu.uniquindio.servify.domain.model.AuthResult
import co.edu.uniquindio.servify.domain.model.ConfirmPasswordResetResult
import co.edu.uniquindio.servify.domain.model.PasswordResetResult
import co.edu.uniquindio.servify.domain.model.User
import co.edu.uniquindio.servify.domain.model.UserRole
import co.edu.uniquindio.servify.domain.repository.AuthRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

/** Devuelve exactamente el resultado configurado para login y registro. */
class ScriptedAuthRepository(
    private val registerResult: AuthResult = AuthResult.Failure(AuthError.INVALID_CREDENTIALS),
    private val loginResult: AuthResult = AuthResult.Failure(AuthError.INVALID_CREDENTIALS),
    private val confirmPasswordResetResult: ConfirmPasswordResetResult = ConfirmPasswordResetResult.SUCCESS
) : AuthRepository {

    override val currentUser: Flow<User?> = flowOf(null)

    override suspend fun login(email: String, password: String, requiredRole: UserRole?): AuthResult =
        loginResult

    override suspend fun register(
        name: String,
        lastName: String,
        email: String,
        password: String,
        city: String
    ): AuthResult = registerResult

    override suspend fun sendPasswordReset(email: String, requiredRole: UserRole?): PasswordResetResult =
        PasswordResetResult.Sent

    override suspend fun confirmPasswordReset(oobCode: String, newPassword: String): ConfirmPasswordResetResult =
        confirmPasswordResetResult

    override fun logout() = Unit
}
