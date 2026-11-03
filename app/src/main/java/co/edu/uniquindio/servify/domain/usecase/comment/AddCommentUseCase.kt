package co.edu.uniquindio.servify.domain.usecase.comment

import co.edu.uniquindio.servify.domain.model.Comment
import co.edu.uniquindio.servify.domain.repository.CommentRepository
import co.edu.uniquindio.servify.domain.repository.PublicationRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import javax.inject.Inject

/**
 * Caso de uso para publicar una nueva reseña o comentario en una oferta de servicio.
 * Actualiza la lista de comentarios y recalcula el promedio y número de reseñas de la publicación.
 */
class AddCommentUseCase @Inject constructor(
    private val commentRepository: CommentRepository,
    private val publicationRepository: PublicationRepository
) {
    suspend operator fun invoke(comment: Comment) = withContext(Dispatchers.IO) {
        // 1. Guardar comentario
        commentRepository.addComment(comment)

        // 2. Obtener lista actualizada de comentarios para la publicación
        val allComments = commentRepository.observeCommentsForPublication(comment.publicationId).first()
        val newCount = allComments.size
        val newAvg = if (newCount > 0) allComments.map { it.rating }.average() else 5.0

        // 3. Actualizar la publicación con el nuevo promedio y contador
        val publication = publicationRepository.getPublicationById(comment.publicationId)
        if (publication != null) {
            val updatedPublication = publication.copy(
                ratingAverage = newAvg,
                reviewsCount = newCount
            )
            publicationRepository.updatePublication(updatedPublication)
        }
    }
}
