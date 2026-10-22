package co.edu.uniquindio.servify.domain.model

/**
 * Modelo de dominio que representa un comentario o reseña dejada por un cliente en una publicación.
 */
data class Comment(
    val id: String,
    val publicationId: String,
    val author: User,
    val rating: Int = 5,
    val text: String,
    val createdAt: Long = System.currentTimeMillis()
)
