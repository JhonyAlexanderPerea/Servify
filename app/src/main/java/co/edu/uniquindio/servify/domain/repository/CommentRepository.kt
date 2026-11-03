package co.edu.uniquindio.servify.domain.repository

import co.edu.uniquindio.servify.domain.model.Comment
import kotlinx.coroutines.flow.Flow

/**
 * Contrato de repositorio para la gestión de comentarios y opiniones de servicios.
 */
interface CommentRepository {
    fun observeCommentsForPublication(publicationId: String): Flow<List<Comment>>
    suspend fun addComment(comment: Comment)
}
