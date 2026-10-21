package co.edu.uniquindio.servify.domain.repository

import co.edu.uniquindio.servify.domain.model.User
import kotlinx.coroutines.flow.Flow

/**
 * Fuente única de verdad de los usuarios (perfil, puntos, insignias).
 * Los puntos y las insignias cambian en usuarios que NO son el de la sesión
 * (p. ej. el autor que recibe un voto), por eso viven aquí y no en AuthRepository.
 */
interface UserRepository {
    val users: Flow<List<User>>

    fun getById(id: String): User?

    fun findByEmail(email: String): User?

    fun add(user: User)

    /** Aplica [transform] de forma atómica. Devuelve el usuario actualizado o null si no existe. */
    fun update(id: String, transform: (User) -> User): User?
}
