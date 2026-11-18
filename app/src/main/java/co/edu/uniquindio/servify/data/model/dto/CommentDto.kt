package co.edu.uniquindio.servify.data.model.dto

import androidx.annotation.Keep
import co.edu.uniquindio.servify.domain.model.Comment
import co.edu.uniquindio.servify.domain.model.User

/**
 * Data Transfer Object (DTO) para la colección "comments" en Cloud Firestore.
 */
@Keep
data class CommentDto(
    val id: String = "",
    val publicationId: String = "",
    val authorId: String = "",
    val authorName: String = "",
    val authorAvatarUrl: String? = null,
    val rating: Int = 5,
    val text: String = "",
    val createdAt: Long = System.currentTimeMillis()
) {
    fun toDomain(): Comment = Comment(
        id = id,
        publicationId = publicationId,
        author = User(
            id = authorId,
            name = authorName.ifBlank { authorId },
            email = "",
            avatarUrl = authorAvatarUrl
        ),
        rating = rating,
        text = text,
        createdAt = createdAt
    )

    fun toDocument(): Map<String, Any?> = mapOf(
        "id" to id,
        "publicationId" to publicationId,
        "authorId" to authorId,
        "authorName" to authorName,
        "authorAvatarUrl" to authorAvatarUrl,
        "rating" to rating,
        "text" to text,
        "createdAt" to createdAt
    )

    companion object {
        fun fromDomain(c: Comment): CommentDto = CommentDto(
            id = c.id,
            publicationId = c.publicationId,
            authorId = c.author.id,
            authorName = c.author.name,
            authorAvatarUrl = c.author.avatarUrl,
            rating = c.rating,
            text = c.text,
            createdAt = c.createdAt
        )
    }
}
