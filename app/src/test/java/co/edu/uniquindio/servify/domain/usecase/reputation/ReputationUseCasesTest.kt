package co.edu.uniquindio.servify.domain.usecase.reputation

import co.edu.uniquindio.servify.data.repository.InMemoryUserRepository
import co.edu.uniquindio.servify.domain.model.Badge
import co.edu.uniquindio.servify.domain.model.Level
import co.edu.uniquindio.servify.domain.model.Publication
import co.edu.uniquindio.servify.domain.model.PublicationStatus
import co.edu.uniquindio.servify.domain.model.User
import co.edu.uniquindio.servify.domain.rules.PointEvent
import co.edu.uniquindio.servify.domain.service.GamificationService
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ReputationUseCasesTest {

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

    // --- AwardPointsUseCase -------------------------------------------------

    @Test
    fun award_points_adds_and_reports_the_level_up() = runBlocking {
        repo.update("u1") { it.copy(points = 190) }
        val result = AwardPointsUseCase(service)("u1", PointEvent.PUBLICATION_CREATED)!!

        assertEquals(Level.PRINCIPIANTE, result.levelBefore)
        assertEquals(Level.PROFESIONAL, result.levelAfter)
        assertTrue(result.leveledUp)
    }

    @Test
    fun award_points_on_an_unknown_user_returns_null() = runBlocking {
        assertNull(AwardPointsUseCase(service)("nope", PointEvent.COMMENT_POSTED))
    }

    // --- EvaluateAchievementsUseCase ---------------------------------------

    @Test
    fun evaluate_achievements_unlocks_and_notifies_only_once() = runBlocking {
        pubs = listOf(publication())

        val first = EvaluateAchievementsUseCase(service)("u1")
        assertEquals(setOf(Badge.PRIMERA_PUBLICACION), first)
        assertEquals(1, notified.size)

        val second = EvaluateAchievementsUseCase(service)("u1")
        assertTrue(second.isEmpty())
        assertEquals(1, notified.size)
    }

    @Test
    fun evaluate_achievements_can_be_silent() = runBlocking {
        pubs = listOf(publication())

        EvaluateAchievementsUseCase(service)("u1", notify = false)

        assertTrue(notified.isEmpty())
        assertTrue(Badge.PRIMERA_PUBLICACION in repo.getById("u1")!!.badges)
    }

    // --- GetLevelProgressUseCase -------------------------------------------

    @Test
    fun level_progress_reports_the_current_level() = runBlocking {
        repo.update("u1") { it.copy(points = 1_240) }

        val progress = GetLevelProgressUseCase(service)("u1")!!

        assertEquals(Level.EXPERTO, progress.level)
    }

    @Test
    fun level_progress_of_an_unknown_user_is_null() = runBlocking {
        assertNull(GetLevelProgressUseCase(service)("nope"))
    }

    // --- GetUserStatsUseCase -----------------------------------------------

    @Test
    fun user_stats_bundle_user_level_and_badges() = runBlocking {
        repo.update("u1") { it.copy(points = 1_240) }

        val stats = GetUserStatsUseCase(service)("u1")!!

        assertEquals("u1", stats.user.id)
        assertEquals(Level.EXPERTO, stats.level.level)
        assertEquals(7, stats.totalCount)
        assertTrue(stats.unlockedCount >= 1)
    }

    @Test
    fun user_stats_of_an_unknown_user_is_null() = runBlocking {
        assertNull(GetUserStatsUseCase(service)("nope"))
    }

    // --- ObserveAchievementsUseCase ----------------------------------------

    @Test
    fun observe_achievements_emits_the_badges_and_updates_when_points_change() = runBlocking {
        val flow = ObserveAchievementsUseCase(service)("u1")

        val before = flow.first()
        assertEquals(7, before.size)
        assertTrue(before.none { it.badge == Badge.CIEN_PUNTOS && it.unlocked })

        repo.update("u1") { it.copy(points = 120) }
        val after = withTimeout(5_000) {
            flow.first { badges -> badges.any { it.badge == Badge.CIEN_PUNTOS && it.unlocked } }
        }

        assertTrue(after.any { it.badge == Badge.CIEN_PUNTOS })
    }
}
