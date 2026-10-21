package co.edu.uniquindio.servify.data.repository

import co.edu.uniquindio.servify.domain.model.User
import co.edu.uniquindio.servify.domain.repository.UserRepository
import co.edu.uniquindio.servify.domain.rules.CredentialRules
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update

class InMemoryUserRepository(seed: List<User> = emptyList()) : UserRepository {

    // LinkedHashMap preserva el orden de inserción; el mapa es inmutable por snapshot.
    private val state = MutableStateFlow(seed.associateBy { it.id })

    override val users: Flow<List<User>> = state.map { it.values.toList() }

    override fun getById(id: String): User? = state.value[id]

    override fun findByEmail(email: String): User? {
        val normalized = CredentialRules.normalizeEmail(email)
        return state.value.values.firstOrNull { CredentialRules.normalizeEmail(it.email) == normalized }
    }

    override fun add(user: User) {
        state.update { it + (user.id to user) }
    }

    override fun update(id: String, transform: (User) -> User): User? {
        var result: User? = null
        state.update { current ->
            val existing = current[id] ?: return@update current
            val updated = transform(existing)
            result = updated
            current + (id to updated)
        }
        return result
    }
}
