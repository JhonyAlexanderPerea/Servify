package co.edu.uniquindio.servify.domain.usecase.comment

import co.edu.uniquindio.servify.domain.model.Comment
import co.edu.uniquindio.servify.domain.repository.CommentRepository
import co.edu.uniquindio.servify.domain.repository.PublicationRepository
import co.edu.uniquindio.servify.domain.usecase.notification.GenerateNotificationUseCase
import co.edu.uniquindio.servify.domain.usecase.reputation.EvaluateAchievementsUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

/**
 * Caso de uso para publicar una nueva reseña o comentario en una oferta de servicio.
 *
 * El repositorio se encarga de crear el comentario y recalcular `ratingAverage` y
 * `reviewsCount` en la misma transacción de Firestore. Aquí solo se validan los
 * datos y se propagan los fallos; ya no se hace una segunda escritura no atómica
 * sobre la publicación.
 */
class AddCommentUseCase @Inject constructor(
    private val commentRepository: CommentRepository,
    private val publicationRepository: PublicationRepository,
    private val evaluateAchievementsUseCase: EvaluateAchievementsUseCase,
    private val generateNotificationUseCase: GenerateNotificationUseCase
) {
    suspend operator fun invoke(comment: Comment) = withContext(Dispatchers.IO) {
        require(comment.text.isNotBlank()) { "El comentario no puede estar vacío" }
        require(comment.rating in 1..5) { "La calificación debe estar entre 1 y 5" }

        // Transacción atómica: si falla, la excepción llega al ViewModel.
        commentRepository.addComment(comment)

        // Un comentario nuevo puede desbloquear la insignia "10 comentarios".
        evaluateAchievementsUseCase(comment.authorId)

        // Aviso best-effort al autor: no puede deshacer una reseña ya confirmada.
        val publication = publicationRepository.getPublicationById(comment.publicationId)
        if (publication != null && publication.authorId != comment.authorId) {
            runCatching {
                generateNotificationUseCase.generateReviewPublishedNotification(
                    authorId = publication.authorId,
                    reviewerName = comment.authorName,
                    serviceTitle = publication.title,
                    publicationId = publication.id
                )
            }
        }
    }
}
