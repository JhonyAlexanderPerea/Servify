package co.edu.uniquindio.servify.domain

import co.edu.uniquindio.servify.domain.model.Publication
import co.edu.uniquindio.servify.domain.model.PublicationStatus
import co.edu.uniquindio.servify.domain.rules.PointEvent
import co.edu.uniquindio.servify.domain.rules.VoteDenied
import co.edu.uniquindio.servify.domain.rules.VoteOutcome
import co.edu.uniquindio.servify.domain.rules.VoteRules
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class VoteRulesTest {

    private fun publication(
        status: PublicationStatus = PublicationStatus.VERIFICADA,
        voters: Set<String> = emptySet()
    ) = Publication(
        id = "p1", authorId = "author", title = "t", category = "Hogar", subcategory = "Plomería",
        description = "d", priceRange = "$1", zone = "z", city = "c",
        status = status, interestedUserIds = voters
    )

    @Test
    fun first_vote_adds_the_user() {
        val r = VoteRules.toggle(publication(), "u1") as VoteOutcome.Toggled
        assertTrue(r.nowInterested)
        assertEquals(setOf("u1"), r.interestedUserIds)
    }

    @Test
    fun second_vote_from_same_user_removes_it() {
        val r = VoteRules.toggle(publication(voters = setOf("u1", "u2")), "u1") as VoteOutcome.Toggled
        assertFalse(r.nowInterested)
        assertEquals(setOf("u2"), r.interestedUserIds)
    }

    @Test
    fun one_vote_per_user_even_if_toggled_repeatedly() {
        var p = publication()
        repeat(5) {
            val r = VoteRules.toggle(p, "u1") as VoteOutcome.Toggled
            p = p.copy(interestedUserIds = r.interestedUserIds)
        }
        assertEquals(1, p.interestedSize) // 5 toques (impar) = 1 voto
    }

    @Test
    fun author_cannot_vote_own_publication() {
        val r = VoteRules.toggle(publication(), "author")
        assertEquals(VoteOutcome.Denied(VoteDenied.OWN_PUBLICATION), r)
    }

    @Test
    fun only_verified_publications_accept_votes() {
        listOf(PublicationStatus.PENDIENTE, PublicationStatus.RECHAZADA, PublicationStatus.FINALIZADA).forEach {
            assertEquals(VoteOutcome.Denied(VoteDenied.NOT_ACTIVE), VoteRules.toggle(publication(status = it), "u1"))
        }
    }

    @Test
    fun point_event_follows_the_vote_direction() {
        val added = VoteOutcome.Toggled(setOf("u1"), true)
        val removed = VoteOutcome.Toggled(emptySet(), false)
        assertEquals(PointEvent.VOTE_RECEIVED, VoteRules.pointEventFor(added))
        assertEquals(PointEvent.VOTE_REVOKED, VoteRules.pointEventFor(removed))
    }
}
