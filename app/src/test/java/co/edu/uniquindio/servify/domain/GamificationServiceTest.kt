package co.edu.uniquindio.servify.domain

import co.edu.uniquindio.servify.data.repository.InMemoryUserRepository
import co.edu.uniquindio.servify.domain.model.Badge
import co.edu.uniquindio.servify.domain.model.Level
import co.edu.uniquindio.servify.domain.model.Publication
import co.edu.uniquindio.servify.domain.model.PublicationStatus
import co.edu.uniquindio.servify.domain.model.User
import co.edu.uniquindio.servify.domain.rules.PointEvent
import co.edu.uniquindio.servify.domain.service.GamificationService
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class GamificationServiceTest {

    private val user = User(id = "u1", name = "Ana", lastName = "Ruiz", email = "a@a.co", city = "Armenia")
    private val repo = InMemoryUserRepository(listOf(user))
    private var pubs = listOf<Publication>()
    private val notified = mutableListOf<Pair<String, Badge>>()

    private val service = GamificationService(
        userRepository = repo,
        publications = { pubs },
        comments = { emptyList() },
        onBadgeUnlocked = { id, badge -> notified += id to badge }
    )

    private fun publication(verified: Boolean = false) = Publication(
        id = "p1", authorId = "u1", title = "t", category = "c", subcategory = "s", description = "d",
        priceRange = "p", zone = "z", city = "c",
        status = if (verified) PublicationStatus.VERIFICADA else PublicationStatus.PENDIENTE,
        verifiedAtMillis = if (verified) 1L else null
    )

    @Test
    fun award_adds_points_and_persists_them() {
        service.award("u1", PointEvent.PUBLICATION_CREATED)
        assertEquals(20, repo.getById("u1")!!.points)
    }

    @Test
    fun award_unknown_user_returns_null() {
        assertNull(service.award("nope", PointEvent.COMMENT_POSTED))
    }

    @Test
    fun crossing_a_threshold_reports_level_up() {
        repo.update("u1") { it.copy(points = 190) }
        val r = service.award("u1", PointEvent.PUBLICATION_CREATED)!!
        assertEquals(Level.PRINCIPIANTE, r.levelBefore)
        assertEquals(Level.PROFESIONAL, r.levelAfter)
        assertTrue(r.leveledUp)
    }

    @Test
    fun unlocking_a_badge_saves_it_and_notifies_exactly_once() {
        pubs = listOf(publication())
        val first = service.award("u1", PointEvent.PUBLICATION_CREATED)!!
        assertEquals(setOf(Badge.PRIMERA_PUBLICACION), first.newBadges)
        assertTrue(Badge.PRIMERA_PUBLICACION in repo.getById("u1")!!.badges)
        assertEquals(listOf("u1" to Badge.PRIMERA_PUBLICACION), notified)

        service.award("u1", PointEvent.COMMENT_POSTED) // no debe volver a notificar
        assertEquals(1, notified.size)
    }

    @Test
    fun hundred_points_badge_unlocks_from_points_alone() {
        repo.update("u1") { it.copy(points = 95) }
        val r = service.award("u1", PointEvent.COMMENT_POSTED)!!
        assertTrue(Badge.CIEN_PUNTOS in r.newBadges)
    }

    @Test
    fun evaluateBadges_without_notify_is_silent() {
        pubs = listOf(publication(verified = true))
        service.evaluateBadges("u1", notify = false)
        assertTrue(notified.isEmpty())
        assertTrue(Badge.PRIMERA_VERIFICADA in repo.getById("u1")!!.badges)
    }

    @Test
    fun progressOf_bundles_level_and_badges() {
        repo.update("u1") { it.copy(points = 1_240) }
        val p = service.progressOf("u1")!!
        assertEquals(Level.EXPERTO, p.level.level)
        assertEquals(7, p.totalCount)
        assertTrue(p.unlockedCount >= 1) // CIEN_PUNTOS por puntos
    }
}
