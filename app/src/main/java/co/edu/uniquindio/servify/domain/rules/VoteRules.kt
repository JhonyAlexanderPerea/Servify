package co.edu.uniquindio.servify.domain.rules

import co.edu.uniquindio.servify.domain.model.Publication
import co.edu.uniquindio.servify.domain.model.PublicationStatus

enum class VoteDenied {
    /** El autor no puede votar su propia publicación. */
    OWN_PUBLICATION,

    /** Solo se vota sobre publicaciones verificadas (las visibles y activas en el feed). */
    NOT_ACTIVE
}

sealed interface VoteOutcome {
    /** [nowInterested] = true si el voto se agregó, false si se retiró. */
    data class Toggled(
        val interestedUserIds: Set<String>,
        val nowInterested: Boolean
    ) : VoteOutcome

    data class Denied(val reason: VoteDenied) : VoteOutcome
}

object VoteRules {

    /** "Me interesa": un voto por usuario por publicación; tocar de nuevo lo retira. */
    fun toggle(publication: Publication, voterId: String): VoteOutcome {
        if (voterId == publication.authorId) return VoteOutcome.Denied(VoteDenied.OWN_PUBLICATION)
        if (publication.status != PublicationStatus.VERIFICADA) return VoteOutcome.Denied(VoteDenied.NOT_ACTIVE)

        val alreadyVoted = voterId in publication.interestedUserIds
        val updated = if (alreadyVoted) publication.interestedUserIds - voterId
        else publication.interestedUserIds + voterId
        return VoteOutcome.Toggled(updated, nowInterested = !alreadyVoted)
    }

    /** Evento de puntos para el AUTOR según el resultado del voto. */
    fun pointEventFor(outcome: VoteOutcome.Toggled): PointEvent =
        if (outcome.nowInterested) PointEvent.VOTE_RECEIVED else PointEvent.VOTE_REVOKED
}
