package co.edu.uniquindio.servify.domain.model

data class Comment(
    val id: String,
    val publicationId: String,
    val authorId: String,
    val text: String,
    val createdAtMillis: Long = 0L
)
