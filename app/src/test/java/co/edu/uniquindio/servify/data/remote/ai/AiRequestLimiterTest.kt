package co.edu.uniquindio.servify.data.remote.ai

import co.edu.uniquindio.servify.domain.util.TimeProvider
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class AiRequestLimiterTest {

    private var now = 1_000_000L
    private val limiter = AiRequestLimiter(TimeProvider { now })

    @Test
    fun allows_twenty_requests_in_the_window() {
        repeat(AiRequestLimiter.MAX_REQUESTS) {
            assertTrue(limiter.tryAcquire())
        }
    }

    @Test
    fun rejects_the_twenty_first_request() {
        repeat(AiRequestLimiter.MAX_REQUESTS) {
            limiter.tryAcquire()
        }

        assertFalse(limiter.tryAcquire())
    }

    @Test
    fun allows_again_after_the_window_passes() {
        repeat(AiRequestLimiter.MAX_REQUESTS) {
            limiter.tryAcquire()
        }

        now += AiRequestLimiter.WINDOW_MILLIS + 1

        assertTrue(limiter.tryAcquire())
    }

    @Test
    fun keeps_rejecting_inside_the_window() {
        repeat(AiRequestLimiter.MAX_REQUESTS) {
            limiter.tryAcquire()
        }

        now += AiRequestLimiter.WINDOW_MILLIS / 2

        assertFalse(limiter.tryAcquire())
    }
}
