package co.edu.uniquindio.servify.domain.usecase.comment

import co.edu.uniquindio.servify.domain.model.Comment
import co.edu.uniquindio.servify.domain.repository.CommentRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Caso de uso para observar el flujo reactivo de comentarios de una publicación específica.
 */
class ObserveCommentsUseCase @Inject constructor(
    private val commentRepository: CommentRepository
) {
    operator fun invoke(publicationId: String): Flow<List<Comment>> {
        return commentRepository.observeCommentsForPublication(publicationId)
    }
}
