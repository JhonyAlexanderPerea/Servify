package co.edu.uniquindio.servify.testutil

import co.edu.uniquindio.servify.domain.model.AuthResult
import co.edu.uniquindio.servify.domain.model.ConfirmPasswordResetResult
import co.edu.uniquindio.servify.domain.model.DeleteAccountResult
import co.edu.uniquindio.servify.domain.model.PasswordResetResult
import co.edu.uniquindio.servify.domain.model.User
import co.edu.uniquindio.servify.domain.model.UserRole
import co.edu.uniquindio.servify.domain.repository.AuthRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import java.io.IOException

/** Repositorio de autenticación que simula una caída de red en todas las operaciones remotas. */
class ThrowingAuthRepository : AuthRepository {

    override val currentUser: Flow<User?> = flowOf(null)

    override suspend fun login(email: String, password: String, requiredRole: UserRole?): AuthResult =
        throw IOException("sin red")

    override suspend fun register(
        name: String,
        lastName: String,
        email: String,
        password: String,
        city: String
    ): AuthResult = throw IOException("sin red")

    override suspend fun sendPasswordReset(email: String, requiredRole: UserRole?): PasswordResetResult =
        throw IOException("sin red")

    override suspend fun confirmPasswordReset(oobCode: String, newPassword: String): ConfirmPasswordResetResult =
        throw IOException("sin red")

    override suspend fun deleteAccount(password: String): DeleteAccountResult =
        throw IOException("sin red")

    override fun logout() = Unit
}
