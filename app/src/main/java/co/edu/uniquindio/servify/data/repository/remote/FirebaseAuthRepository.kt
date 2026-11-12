package co.edu.uniquindio.servify.data.repository.remote

import co.edu.uniquindio.servify.data.remote.user.UserDto
import co.edu.uniquindio.servify.data.remote.user.authStateFlow
import co.edu.uniquindio.servify.data.remote.user.observeUser
import co.edu.uniquindio.servify.data.remote.user.toUser
import co.edu.uniquindio.servify.domain.model.AuthError
import co.edu.uniquindio.servify.domain.model.AuthResult
import co.edu.uniquindio.servify.domain.model.ConfirmPasswordResetResult
import co.edu.uniquindio.servify.domain.model.DeleteAccountResult
import co.edu.uniquindio.servify.domain.model.PasswordResetResult
import co.edu.uniquindio.servify.domain.model.User
import co.edu.uniquindio.servify.domain.model.UserRole
import co.edu.uniquindio.servify.domain.repository.AuthRepository
import co.edu.uniquindio.servify.domain.rules.CredentialRules
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirebaseAuthRepository @Inject constructor(
    firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
) : AuthRepository {

    private val users = firestore.collection("users")

    @OptIn(ExperimentalCoroutinesApi::class)
    override val currentUser: Flow<User?> = auth.authStateFlow().flatMapLatest { firebaseUser ->
        if (firebaseUser == null) flowOf(null) else users.document(firebaseUser.uid).observeUser()
    }

    override suspend fun login(email: String, password: String, requiredRole: UserRole?): AuthResult {
        // Autenticar con Firebase Authentication
        val signIn = try {
            auth.signInWithEmailAndPassword(CredentialRules.normalizeEmail(email), password).await()
        } catch (e: FirebaseAuthInvalidCredentialsException) {
            return AuthResult.Failure(AuthError.INVALID_CREDENTIALS) // correo o contraseña incorrectos
        } catch (e: FirebaseAuthInvalidUserException) {
            return AuthResult.Failure(AuthError.INVALID_CREDENTIALS) // la cuenta no existe o está deshabilitada
        }
        val uid = signIn.user?.uid ?: throw IllegalStateException("Usuario no encontrado")

        // Recuperar el perfil desde Firestore. Si no existe, se cierra la sesión de Firebase para no
        // quedar autenticado a medias
        val user = users.document(uid).get().await().toUser()
        if (user == null) {
            auth.signOut()
            return AuthResult.Failure(AuthError.INVALID_CREDENTIALS)
        }
        if (requiredRole != null && user.role != requiredRole) {
            auth.signOut()
            return AuthResult.Failure(AuthError.ROLE_NOT_ALLOWED)
        }
        // Los moderadores están exentos: sus cuentas se crean a mano en la consola y no tienen el correo verificado
        if (user.role != UserRole.MODERATOR && signIn.user?.isEmailVerified != true) {
            auth.signOut()
            return AuthResult.Failure(AuthError.EMAIL_NOT_VERIFIED)
        }
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
        // Firebase Authentication rechaza los correos repetidos: no hace falta consultarlos antes.
        // OJO con el orden: FirebaseAuthWeakPasswordException es subclase de FirebaseAuthInvalidCredentialsException
        val created = try {
            auth.createUserWithEmailAndPassword(normalized, password).await()
        } catch (e: FirebaseAuthUserCollisionException) {
            return AuthResult.Failure(AuthError.EMAIL_ALREADY_REGISTERED)
        } catch (e: FirebaseAuthWeakPasswordException) {
            return AuthResult.Failure(AuthError.WEAK_PASSWORD)
        } catch (e: FirebaseAuthInvalidCredentialsException) {
            return AuthResult.Failure(AuthError.INVALID_EMAIL)
        }
        val firebaseUser = created.user ?: throw IllegalStateException("Error al obtener el UID del usuario creado")

        // El documento usa el UID de Authentication como id. El registro SIEMPRE crea rol USER
        val newUser = User(
            id = firebaseUser.uid,
            name = name.trim(),
            lastName = lastName.trim(),
            email = normalized,
            city = city.trim(),
            role = UserRole.USER,
            memberSinceMillis = System.currentTimeMillis()
        )
        try {
            users.document(firebaseUser.uid).set(UserDto.fromDomain(newUser)).await()
            // El correo de verificación va dentro del mismo try: si falla, se hace rollback de la cuenta
            firebaseUser.sendEmailVerification().await()
        } catch (e: Exception) {
            // Si no se pudo guardar el perfil, se borra la cuenta para no dejarla incompleta.
            // NonCancellable: el borrado termina aunque se cancele la corrutina
            withContext(NonCancellable) { runCatching { firebaseUser.delete().await() } }
            throw e
        }
        // Sin sesión hasta verificar el correo: se cierra la sesión que Firebase abre al crear la cuenta
        auth.signOut()
        return AuthResult.VerificationRequired(normalized)
    }

    override suspend fun sendPasswordReset(email: String, requiredRole: UserRole?): PasswordResetResult {
        if (!CredentialRules.isValidEmail(email)) return PasswordResetResult.InvalidEmail
        try {
            auth.sendPasswordResetEmail(CredentialRules.normalizeEmail(email)).await()
        } catch (e: FirebaseAuthInvalidUserException) {
            // La respuesta es la misma exista o no la cuenta, para no revelar qué correos están registrados
        }
        return PasswordResetResult.Sent
    }

    override suspend fun confirmPasswordReset(oobCode: String, newPassword: String): ConfirmPasswordResetResult {
        return try {
            auth.confirmPasswordReset(oobCode, newPassword).await()
            ConfirmPasswordResetResult.SUCCESS
        } catch (e: FirebaseAuthWeakPasswordException) {
            ConfirmPasswordResetResult.WEAK_PASSWORD
        } catch (e: FirebaseAuthException) {
            // Código inválido, vencido o ya usado
            ConfirmPasswordResetResult.INVALID_OR_EXPIRED_CODE
        }
    }

    /**
     * Borra primero el perfil y luego la cuenta de Authentication (mientras la sesión sigue activa).
     * Si falla el último paso queda una cuenta de Authentication sin perfil; al iniciar sesión,
     * [login] la rechaza porque no encuentra el perfil.
     */
    override suspend fun deleteAccount(password: String): DeleteAccountResult {
        val firebaseUser = auth.currentUser ?: throw IllegalStateException("No hay una sesión iniciada")
        val email = firebaseUser.email ?: throw IllegalStateException("La cuenta no tiene correo")
        // Borrar una cuenta exige un inicio de sesión reciente: se reautentica con la contraseña
        try {
            firebaseUser.reauthenticate(EmailAuthProvider.getCredential(email, password)).await()
        } catch (e: FirebaseAuthInvalidCredentialsException) {
            return DeleteAccountResult.WRONG_PASSWORD
        } catch (e: FirebaseAuthInvalidUserException) {
            return DeleteAccountResult.WRONG_PASSWORD
        }
        // Primero los datos (mientras la sesión sigue activa) y luego la cuenta de Authentication
        users.document(firebaseUser.uid).delete().await()
        firebaseUser.delete().await()
        return DeleteAccountResult.SUCCESS
    }

    override fun logout() {
        auth.signOut()
    }
}
