package co.edu.uniquindio.servify.data.remote.ai

import co.edu.uniquindio.servify.domain.util.TimeProvider
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AiRequestLimiter @Inject constructor(private val timeProvider: TimeProvider) {

    private val timestamps = ArrayDeque<Long>()

    /** true si todavía se puede hacer una petición (máx. [MAX_REQUESTS] por [WINDOW_MILLIS]); la registra. */
    @Synchronized
    fun tryAcquire(): Boolean {
        val now = timeProvider.now()
        while (timestamps.isNotEmpty() && now - timestamps.first() > WINDOW_MILLIS) {
            timestamps.removeFirst()
        }
        if (timestamps.size >= MAX_REQUESTS) return false
        timestamps.addLast(now)
        return true
    }

    companion object {
        const val MAX_REQUESTS = 20
        const val WINDOW_MILLIS = 10 * 60 * 1000L
    }
}
