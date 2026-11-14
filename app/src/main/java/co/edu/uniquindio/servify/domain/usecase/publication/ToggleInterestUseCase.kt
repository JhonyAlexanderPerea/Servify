package co.edu.uniquindio.servify.domain.usecase.publication

import co.edu.uniquindio.servify.domain.repository.PublicationRepository
import co.edu.uniquindio.servify.domain.repository.UserRepository
import co.edu.uniquindio.servify.domain.rules.PointEvent
import co.edu.uniquindio.servify.domain.rules.VoteOutcome
import co.edu.uniquindio.servify.domain.rules.VoteRules
import co.edu.uniquindio.servify.domain.usecase.reputation.AwardPointsUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.withContext
import javax.inject.Inject

/**
 * Caso de uso para alternar el estado de "Me interesa" (favorito/guardar) en un servicio.
 */
class ToggleInterestUseCase @Inject constructor(
    private val publicationRepository: PublicationRepository,
    private val userRepository: UserRepository? = null,
    private val awardPointsUseCase: AwardPointsUseCase? = null
) {
    suspend operator fun invoke(publicationId: String): Boolean? = withContext(Dispatchers.IO) {
        val publication = publicationRepository.getPublicationById(publicationId)
            ?: return@withContext null

        val currentUser = userRepository?.observeCurrentUser()?.firstOrNull()
        val userId = currentUser?.id ?: return@withContext null

        when (VoteRules.toggle(publication, userId)) {
            is VoteOutcome.Toggled -> {
                val nowInterested = publicationRepository.toggleInterest(publicationId, userId)
                    ?: return@withContext null

                if (awardPointsUseCase != null && publication.authorId.isNotBlank() && publication.authorId != userId) {
                    val pointEvent = if (nowInterested) {
                        PointEvent.VOTE_RECEIVED
                    } else {
                        PointEvent.VOTE_REVOKED
                    }
                    runCatching {
                        awardPointsUseCase(publication.authorId, pointEvent)
                    }
                }
                nowInterested
            }
            is VoteOutcome.Denied -> null
        }
    }
}
