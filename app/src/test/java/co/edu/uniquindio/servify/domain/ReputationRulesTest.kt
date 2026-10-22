package co.edu.uniquindio.servify.domain

import co.edu.uniquindio.servify.domain.model.Level
import co.edu.uniquindio.servify.domain.rules.PointEvent
import co.edu.uniquindio.servify.domain.rules.ReputationRules
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class ReputationRulesTest {

    @Test
    fun level_thresholds_are_respected_at_the_boundaries() {
        assertEquals(Level.PRINCIPIANTE, Level.fromPoints(0))
        assertEquals(Level.PRINCIPIANTE, Level.fromPoints(199))
        assertEquals(Level.PROFESIONAL, Level.fromPoints(200))
        assertEquals(Level.PROFESIONAL, Level.fromPoints(599))
        assertEquals(Level.EXPERTO, Level.fromPoints(600))
        assertEquals(Level.EXPERTO, Level.fromPoints(1_499))
        assertEquals(Level.MAESTRO, Level.fromPoints(1_500))
        assertEquals(Level.MAESTRO, Level.fromPoints(99_999))
    }

    @Test
    fun mockup_profile_with_1240_points_is_experto() {
        assertEquals(Level.EXPERTO, ReputationRules.levelFor(1_240))
    }

    @Test
    fun points_per_event() {
        assertEquals(20, ReputationRules.applyEvent(0, PointEvent.PUBLICATION_CREATED))
        assertEquals(5, ReputationRules.applyEvent(0, PointEvent.COMMENT_POSTED))
        assertEquals(2, ReputationRules.applyEvent(0, PointEvent.VOTE_RECEIVED))
        assertEquals(30, ReputationRules.applyEvent(0, PointEvent.PUBLICATION_VERIFIED))
    }

    @Test
    fun points_never_go_below_zero() {
        assertEquals(0, ReputationRules.applyEvent(1, PointEvent.VOTE_REVOKED))
    }

    @Test
    fun vote_received_then_revoked_returns_to_the_same_points() {
        val after = ReputationRules.applyEvent(
            ReputationRules.applyEvent(100, PointEvent.VOTE_RECEIVED), PointEvent.VOTE_REVOKED
        )
        assertEquals(100, after)
    }

    @Test
    fun commenting_on_own_publication_gives_no_points() {
        assertNull(ReputationRules.eventForComment("u1", "u1"))
        assertEquals(PointEvent.COMMENT_POSTED, ReputationRules.eventForComment("u2", "u1"))
    }

    @Test
    fun progress_inside_a_level() {
        val p = ReputationRules.progress(1_240) // Experto: 600..1500
        assertEquals(Level.EXPERTO, p.level)
        assertEquals(Level.MAESTRO, p.next)
        assertEquals(640, p.pointsIntoLevel)
        assertEquals(260, p.pointsToNext)
        assertEquals(640f / 900f, p.fraction, 0.0001f)
    }

    @Test
    fun progress_at_max_level_is_full() {
        val p = ReputationRules.progress(2_000)
        assertEquals(Level.MAESTRO, p.level)
        assertNull(p.next)
        assertEquals(0, p.pointsToNext)
        assertEquals(1f, p.fraction, 0f)
    }

    @Test
    fun progress_at_zero() {
        val p = ReputationRules.progress(0)
        assertEquals(0f, p.fraction, 0f)
        assertEquals(200, p.pointsToNext)
    }
}
