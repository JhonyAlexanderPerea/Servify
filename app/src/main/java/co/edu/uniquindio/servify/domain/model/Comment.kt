package co.edu.uniquindio.servify.domain.model

/**
 * Modelo de dominio para comentarios en publicaciones.
 */
data class Comment(
    val id: String,
    val publicationId: String,
    val author: User,
    val rating: Int = 5,
    val text: String,
    val createdAt: Long = System.currentTimeMillis()
) {
    val authorId: String get() = author.id
    val authorName: String get() = author.name
    val content: String get() = text
    val createdAtMillis: Long get() = createdAt

    /**
     * Constructor secundario para compatibilidad cuando se pasa [authorId] como String.
     */
    constructor(
        id: String,
        publicationId: String,
        authorId: String,
        text: String,
        rating: Int = 5,
        createdAt: Long = System.currentTimeMillis()
    ) : this(
        id = id,
        publicationId = publicationId,
        author = User(id = authorId, name = authorId, email = ""),
        rating = rating,
        text = text,
        createdAt = createdAt
    )
}
