package co.edu.uniquindio.servify.domain.usecase.publication

import co.edu.uniquindio.servify.data.seed.SeedUsers
import co.edu.uniquindio.servify.domain.model.PublicationActionResult
import co.edu.uniquindio.servify.domain.model.PublicationStatus
import co.edu.uniquindio.servify.testutil.TestEnvironment
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class PublicationUseCasesTest {

    private val env = TestEnvironment(nowMillis = 1_900_000_000_000L)

    // ----- ObserveMyPublications -----

    @Test
    fun observe_returns_only_my_publications_newest_first() = runBlocking {
        val list = env.observeMyPublications(SeedUsers.JHONY_ID).first()

        assertEquals(5, list.size)
        assertTrue(list.all { it.authorId == SeedUsers.JHONY_ID })
        assertEquals(list.sortedByDescending { it.createdAtMillis }, list)
    }

    @Test
    fun observe_reflects_a_new_deletion() = runBlocking {
        env.deletePublication(SeedUsers.JHONY_ID, "pub_jhony_paseo")

        assertEquals(4, env.observeMyPublications(SeedUsers.JHONY_ID).first().size)
    }

    // ----- MarkPublicationCompleted -----

    @Test
    fun owner_can_finalize_a_verified_publication() {
        val result = env.markCompleted(SeedUsers.JHONY_ID, "pub_jhony_plomeria")

        val updated = env.publications.getById("pub_jhony_plomeria")
        assertEquals(PublicationActionResult.Success, result)
        assertEquals(PublicationStatus.FINALIZADA, updated?.status)
        assertEquals(1_900_000_000_000L, updated?.finalizedAtMillis)
    }

    @Test
    fun finalizing_keeps_the_verification_date_so_it_still_counts_as_verified() {
        env.markCompleted(SeedUsers.JHONY_ID, "pub_jhony_plomeria")

        assertTrue(env.publications.getById("pub_jhony_plomeria")!!.wasVerified)
    }

    @Test
    fun cannot_finalize_a_pending_rejected_or_already_finalized_publication() {
        listOf(
            "pub_jhony_tutorias" to PublicationStatus.PENDIENTE,
            "pub_jhony_paseo" to PublicationStatus.RECHAZADA,
            "pub_jhony_computadores" to PublicationStatus.FINALIZADA
        ).forEach { (id, status) ->
            assertEquals(
                PublicationActionResult.NotAllowed(status),
                env.markCompleted(SeedUsers.JHONY_ID, id)
            )
            assertEquals(status, env.publications.getById(id)?.status)
        }
    }

    @Test
    fun cannot_finalize_someone_elses_publication() {
        assertEquals(
            PublicationActionResult.NotOwner,
            env.markCompleted(SeedUsers.JUAN_ID, "pub_jhony_plomeria")
        )
        assertEquals(PublicationStatus.VERIFICADA, env.publications.getById("pub_jhony_plomeria")?.status)
    }

    @Test
    fun finalizing_an_unknown_publication_reports_not_found() {
        assertEquals(PublicationActionResult.NotFound, env.markCompleted(SeedUsers.JHONY_ID, "no_existe"))
    }

    // ----- DeletePublication -----

    @Test
    fun owner_can_delete_in_any_status() {
        listOf("pub_jhony_plomeria", "pub_jhony_tutorias", "pub_jhony_computadores", "pub_jhony_paseo")
            .forEach { id ->
                assertEquals(PublicationActionResult.Success, env.deletePublication(SeedUsers.JHONY_ID, id))
                assertNull(env.publications.getById(id))
            }
    }

    @Test
    fun cannot_delete_someone_elses_publication() {
        assertEquals(PublicationActionResult.NotOwner, env.deletePublication(SeedUsers.JUAN_ID, "pub_jhony_paseo"))
        assertNotNull(env.publications.getById("pub_jhony_paseo"))
    }

    @Test
    fun deleting_twice_reports_not_found_the_second_time() {
        env.deletePublication(SeedUsers.JHONY_ID, "pub_jhony_paseo")

        assertEquals(PublicationActionResult.NotFound, env.deletePublication(SeedUsers.JHONY_ID, "pub_jhony_paseo"))
    }
}
