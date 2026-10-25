package co.edu.uniquindio.servify.domain.model

/**
 * Modelo de dominio para comentarios en publicaciones.
 */
data class Comment(
    val id: String,
    val publicationId: String,
    val authorId: String,
    val authorName: String,
    val content: String,
    val createdAtMillis: Long = System.currentTimeMillis()
)
