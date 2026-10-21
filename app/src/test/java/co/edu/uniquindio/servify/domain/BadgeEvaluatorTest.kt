package co.edu.uniquindio.servify.domain

import co.edu.uniquindio.servify.domain.model.Badge
import co.edu.uniquindio.servify.domain.model.Comment
import co.edu.uniquindio.servify.domain.model.Publication
import co.edu.uniquindio.servify.domain.model.PublicationStatus
import co.edu.uniquindio.servify.domain.model.User
import co.edu.uniquindio.servify.domain.rules.BadgeEvaluator
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class BadgeEvaluatorTest {

    private val me = User(id = "me", name = "Ana", lastName = "Ruiz", email = "a@a.co", city = "Armenia")

    private fun pub(
        id: String,
        author: String = "me",
        status: PublicationStatus = PublicationStatus.PENDIENTE,
        verified: Boolean = false,
        votes: Int = 0
    ) = Publication(
        id = id, authorId = author, title = "t", category = "c", subcategory = "s", description = "d",
        priceRange = "p", zone = "z", city = "c", status = status,
        interestedUserIds = (1..votes).map { "v$it" }.toSet(),
        verifiedAtMillis = if (verified) 1L else null
    )

    private fun comments(n: Int, author: String = "me") =
        (1..n).map { Comment("c$it", "p1", author, "hola") }

    private fun unlocked(user: User, pubs: List<Publication>, comms: List<Comment> = emptyList()) =
        BadgeEvaluator.newlyUnlocked(user, pubs, comms)

    @Test
    fun nothing_unlocked_for_a_brand_new_user() {
        assertTrue(unlocked(me, emptyList()).isEmpty())
    }

    @Test
    fun first_publication() {
        assertEquals(setOf(Badge.PRIMERA_PUBLICACION), unlocked(me, listOf(pub("1"))))
    }

    @Test
    fun first_verified_publication() {
        val r = unlocked(me, listOf(pub("1", status = PublicationStatus.VERIFICADA, verified = true)))
        assertEquals(setOf(Badge.PRIMERA_PUBLICACION, Badge.PRIMERA_VERIFICADA), r)
    }

    @Test
    fun a_finalized_publication_that_was_verified_still_counts_as_verified() {
        val r = unlocked(me, listOf(pub("1", status = PublicationStatus.FINALIZADA, verified = true)))
        assertTrue(Badge.PRIMERA_VERIFICADA in r)
    }

    @Test
    fun a_finalized_publication_that_was_never_verified_does_not_count() {
        val r = unlocked(me, listOf(pub("1", status = PublicationStatus.FINALIZADA, verified = false)))
        assertFalse(Badge.PRIMERA_VERIFICADA in r)
    }

    @Test
    fun ten_verified_publications() {
        val pubs = (1..10).map { pub("p$it", status = PublicationStatus.VERIFICADA, verified = true) }
        assertTrue(Badge.DIEZ_VERIFICADAS in unlocked(me, pubs))
        assertFalse(Badge.DIEZ_VERIFICADAS in unlocked(me, pubs.take(9)))
    }

    @Test
    fun hundred_points() {
        assertFalse(Badge.CIEN_PUNTOS in unlocked(me.copy(points = 99), emptyList()))
        assertTrue(Badge.CIEN_PUNTOS in unlocked(me.copy(points = 100), emptyList()))
    }

    @Test
    fun ten_comments_only_count_my_own() {
        assertFalse(Badge.DIEZ_COMENTARIOS in unlocked(me, emptyList(), comments(9)))
        assertTrue(Badge.DIEZ_COMENTARIOS in unlocked(me, emptyList(), comments(10)))
        assertFalse(Badge.DIEZ_COMENTARIOS in unlocked(me, emptyList(), comments(10, author = "other")))
    }

    @Test
    fun trusted_user_needs_three_verified_and_no_rejected() {
        val verified = (1..3).map { pub("v$it", status = PublicationStatus.VERIFICADA, verified = true) }
        assertTrue(Badge.USUARIO_CONFIABLE in unlocked(me, verified))
        assertFalse(Badge.USUARIO_CONFIABLE in unlocked(me, verified.take(2)))
        val withRejected = verified + pub("r", status = PublicationStatus.RECHAZADA)
        assertFalse(Badge.USUARIO_CONFIABLE in unlocked(me, withRejected))
    }

    @Test
    fun highlighted_needs_min_votes_and_to_be_the_top() {
        val mine = pub("mine", status = PublicationStatus.VERIFICADA, verified = true, votes = 6)
        val other = pub("other", author = "x", status = PublicationStatus.VERIFICADA, verified = true, votes = 8)
        assertFalse(Badge.PUBLICACION_DESTACADA in unlocked(me, listOf(mine, other)))
        assertTrue(Badge.PUBLICACION_DESTACADA in unlocked(me, listOf(mine)))
        // empate en el primer lugar: ambos la ganan
        assertTrue(Badge.PUBLICACION_DESTACADA in unlocked(me, listOf(mine.copy(interestedUserIds = other.interestedUserIds), other)))
    }

    @Test
    fun highlighted_not_unlocked_below_min_votes() {
        val mine = pub("mine", status = PublicationStatus.VERIFICADA, verified = true, votes = 4)
        assertFalse(Badge.PUBLICACION_DESTACADA in unlocked(me, listOf(mine)))
    }

    @Test
    fun already_owned_badges_are_not_reported_again_and_are_never_lost() {
        val owner = me.copy(badges = setOf(Badge.PRIMERA_PUBLICACION))
        assertTrue(unlocked(owner, listOf(pub("1"))).isEmpty())
        // aunque se borren las publicaciones, la insignia sigue "desbloqueada" en el progreso
        val progress = BadgeEvaluator.progress(owner, emptyList(), emptyList())
        assertTrue(progress.first { it.badge == Badge.PRIMERA_PUBLICACION }.unlocked)
    }

    @Test
    fun progress_lists_all_seven_badges_with_fractions() {
        val progress = BadgeEvaluator.progress(me.copy(points = 50), emptyList(), comments(5))
        assertEquals(7, progress.size)
        assertEquals(0.5f, progress.first { it.badge == Badge.CIEN_PUNTOS }.fraction, 0.0001f)
        assertEquals(0.5f, progress.first { it.badge == Badge.DIEZ_COMENTARIOS }.fraction, 0.0001f)
    }
}
