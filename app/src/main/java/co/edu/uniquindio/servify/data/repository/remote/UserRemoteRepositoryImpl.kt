package co.edu.uniquindio.servify.data.repository.remote

import co.edu.uniquindio.servify.data.remote.user.UserDto
import co.edu.uniquindio.servify.data.remote.user.authStateFlow
import co.edu.uniquindio.servify.data.remote.user.observeUser
import co.edu.uniquindio.servify.data.remote.user.toUser
import co.edu.uniquindio.servify.domain.model.User
import co.edu.uniquindio.servify.domain.repository.UserRepository
import co.edu.uniquindio.servify.domain.rules.CredentialRules
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRemoteRepositoryImpl @Inject constructor(
    firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
) : UserRepository {

    private val collection = firestore.collection("users")

    // Usuario autenticado: sigue al estado de Firebase Authentication y a su documento en Firestore
    @OptIn(ExperimentalCoroutinesApi::class)
    override fun observeCurrentUser(): Flow<User> =
        auth.authStateFlow().flatMapLatest { firebaseUser ->
            if (firebaseUser == null) {
                emptyFlow<User>()
            } else {
                collection.document(firebaseUser.uid).observeUser().filterNotNull()
            }
        }

    override fun observeUserById(id: String): Flow<User?> = collection.document(id).observeUser()

    override suspend fun getById(id: String): User? = collection.document(id).get().await().toUser()

    override suspend fun findByEmail(email: String): User? =
        collection.whereEqualTo("email", CredentialRules.normalizeEmail(email))
            .get().await().documents.firstOrNull()?.toUser()

    // El id del documento es el UID de Firebase Authentication
    override suspend fun add(user: User) {
        collection.document(user.id).set(UserDto.fromDomain(user)).await()
    }

    override suspend fun update(id: String, transform: (User) -> User): User? {
        val current = getById(id) ?: return null
        val updated = transform(current).copy(id = id)
        // set() reemplaza el documento completo con los datos nuevos
        collection.document(id).set(UserDto.fromDomain(updated)).await()
        return updated
    }

    override suspend fun updatePoints(pointsToAdd: Int) {
        val uid = auth.currentUser?.uid ?: return
        collection.document(uid).update("points", FieldValue.increment(pointsToAdd.toLong())).await()
    }
}
