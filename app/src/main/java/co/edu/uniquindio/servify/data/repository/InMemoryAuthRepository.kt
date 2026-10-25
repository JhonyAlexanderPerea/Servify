package co.edu.uniquindio.servify.data.repository

import co.edu.uniquindio.servify.domain.model.AuthError
import co.edu.uniquindio.servify.domain.model.AuthResult
import co.edu.uniquindio.servify.domain.model.PasswordResetResult
import co.edu.uniquindio.servify.domain.model.User
import co.edu.uniquindio.servify.domain.model.UserRole
import co.edu.uniquindio.servify.domain.repository.AuthRepository
import co.edu.uniquindio.servify.domain.repository.UserRepository
import co.edu.uniquindio.servify.domain.rules.CredentialRules
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.combine
import java.util.UUID

class InMemoryAuthRepository(
    private val userRepository: UserRepository,
    credentials: Map<String, String>,
    private val clock: () -> Long = System::currentTimeMillis,
    /** Latencia simulada del envío del correo (en pruebas se pasa 0). */
    private val resetDelayMillis: Long = 800L,
    private val idGenerator: () -> String = { "user_" + UUID.randomUUID() }
) : AuthRepository {

    // email normalizado -> contraseña
    private val passwords = HashMap<String, String>().apply {
        credentials.forEach { (email, pass) -> put(CredentialRules.normalizeEmail(email), pass) }
    }

    private val sessionUserId = MutableStateFlow<String?>(null)

    override val currentUser: Flow<User?> =
        combine(sessionUserId, userRepository.users) { id, users -> users.firstOrNull { it.id == id } }

    override val currentUserSnapshot: User?
        get() = sessionUserId.value?.let(userRepository::getById)

    override suspend fun login(email: String, password: String, requiredRole: UserRole?): AuthResult {
        val normalized = CredentialRules.normalizeEmail(email)
        val user = userRepository.findByEmail(normalized)
        val validPassword = synchronized(passwords) { passwords[normalized] == password }
        if (user == null || !validPassword) return AuthResult.Failure(AuthError.INVALID_CREDENTIALS)
        if (requiredRole != null && user.role != requiredRole) return AuthResult.Failure(AuthError.ROLE_NOT_ALLOWED)

        sessionUserId.value = user.id
        return AuthResult.Success(user)
    }

    override suspend fun register(
        name: String,
        lastName: String,
        email: String,
        password: String,
        city: String
    ): AuthResult {
        if (name.isBlank() || lastName.isBlank() || city.isBlank()) {
            return AuthResult.Failure(AuthError.MISSING_FIELDS)
        }
        if (!CredentialRules.isValidEmail(email)) return AuthResult.Failure(AuthError.INVALID_EMAIL)
        if (!CredentialRules.isStrongEnough(password)) return AuthResult.Failure(AuthError.WEAK_PASSWORD)

        val normalized = CredentialRules.normalizeEmail(email)
        // Registro siempre crea rol USER: ningún registro puede producir un moderador.
        val user = synchronized(passwords) {
            if (userRepository.findByEmail(normalized) != null) return AuthResult.Failure(AuthError.EMAIL_ALREADY_REGISTERED)
            val created = User(
                id = idGenerator(),
                name = name.trim(),
                lastName = lastName.trim(),
                email = normalized,
                city = city.trim(),
                role = UserRole.USER,
                memberSinceMillis = clock()
            )
            userRepository.add(created)
            passwords[normalized] = password
            created
        }
        sessionUserId.value = user.id
        return AuthResult.Success(user)
    }

    private val sentResets = java.util.concurrent.CopyOnWriteArrayList<String>()

    /** Correos a los que se "envió" un enlace (solo para verificar la simulación en pruebas). */
    val sentPasswordResets: List<String> get() = sentResets.toList()

    override suspend fun sendPasswordReset(email: String, requiredRole: UserRole?): PasswordResetResult {
        if (!CredentialRules.isValidEmail(email)) return PasswordResetResult.InvalidEmail
        if (resetDelayMillis > 0) delay(resetDelayMillis)
        val normalized = CredentialRules.normalizeEmail(email)
        val account = userRepository.findByEmail(normalized)
        // Solo "se envía" si la cuenta existe y tiene el rol del flujo; la respuesta es la misma en
        // todos los casos para no revelar qué correos están registrados.
        if (account != null && (requiredRole == null || account.role == requiredRole)) {
            sentResets += normalized
        }
        return PasswordResetResult.Sent
    }

    override fun logout() {
        sessionUserId.value = null
    }
}
