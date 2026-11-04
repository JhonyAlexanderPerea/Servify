package co.edu.uniquindio.servify.navigation

import org.junit.Assert.assertEquals
import org.junit.Test

class ScreenTest {

    @Test
    fun moderator_review_route_exposes_the_publication_id() {
        assertEquals("moderator_review/{publicationId}", Screen.ModeratorReview.route)
        assertEquals("moderator_review/pub_1", Screen.ModeratorReview.createRoute("pub_1"))
    }
}
