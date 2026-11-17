package co.edu.uniquindio.servify.domain.usecase.moderation

import co.edu.uniquindio.servify.data.seed.SeedUsers
import co.edu.uniquindio.servify.domain.model.ModerationResult
import co.edu.uniquindio.servify.domain.model.NotificationType
import co.edu.uniquindio.servify.domain.model.Publication
import co.edu.uniquindio.servify.domain.model.PublicationStatus
import co.edu.uniquindio.servify.domain.model.User
import co.edu.uniquindio.servify.testutil.TestEnvironment
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ModerationUseCasesTest {

    private val env = TestEnvironment()

    private suspend fun notificationsFor(publicationId: String) =
        env.notifications.observeNotifications().first().filter { it.targetId == publicationId }

    private fun pendingWithBlankAuthor(id: String = "custom_pending") = Publication(
        id = id,
        title = "Servicio de prueba",
        description = "Descripción de prueba",
        provider = User(id = "provider_1", name = "Proveedor", email = "proveedor@servify.co"),
        authorId = "",
        status = PublicationStatus.PENDIENTE,
        createdAtMillis = env.nowMillis - 60_000L
    )

    @Test
    fun approving_a_pending_publication_updates_it_notifies_and_awards_points() = runBlocking {
        val beforePoints = env.users.getById(SeedUsers.JHONY_ID)!!.points

        val result = env.approvePublication("pub_jhony_tutorias")

        val updated = env.publications.getById("pub_jhony_tutorias")!!
        assertEquals(ModerationResult.Success, result)
        assertEquals(PublicationStatus.VERIFICADA, updated.status)
        assertEquals(env.nowMillis, updated.verifiedAtMillis)
        assertNull(updated.rejectionReason)
        assertEquals(beforePoints + 30, env.users.getById(SeedUsers.JHONY_ID)!!.points)

        val notification = notificationsFor("pub_jhony_tutorias").single()
        assertEquals(SeedUsers.JHONY_ID, notification.userId)
        assertEquals(NotificationType.MODERATION_UPDATE, notification.type)
        assertEquals("Estado de publicación actualizado", notification.title)
        assertTrue(notification.message.contains(updated.title))
        assertTrue(notification.message.contains("aprobada"))
    }

    @Test
    fun approving_clears_a_previous_rejection_reason() = runBlocking {
        env.publications.add(
            Publication(
                id = "pending_with_reason",
                title = "Publicación corregida",
                description = "Descripción",
                authorId = SeedUsers.JHONY_ID,
                status = PublicationStatus.PENDIENTE,
                rejectionReason = "Motivo anterior",
                createdAtMillis = env.nowMillis - 60_000L
            )
        )

        env.approvePublication("pending_with_reason")

        assertNull(env.publications.getById("pending_with_reason")!!.rejectionReason)
    }

    @Test
    fun approving_uses_the_provider_when_the_author_id_is_blank() = runBlocking {
        env.publications.add(pendingWithBlankAuthor())

        env.approvePublication("custom_pending")

        assertEquals("provider_1", notificationsFor("custom_pending").single().userId)
    }

    @Test
    fun approving_only_accepts_pending_publications() = runBlocking {
        listOf(
            "pub_jhony_plomeria" to PublicationStatus.VERIFICADA,
            "pub_jhony_paseo" to PublicationStatus.RECHAZADA,
            "pub_jhony_computadores" to PublicationStatus.FINALIZADA
        ).forEach { (id, status) ->
            assertEquals(ModerationResult.NotAllowed(status), env.approvePublication(id))
            assertEquals(status, env.publications.getById(id)!!.status)
        }
    }

    @Test
    fun approving_an_unknown_publication_reports_not_found() = runBlocking {
        assertEquals(ModerationResult.NotFound, env.approvePublication("no_existe"))
    }

    @Test
    fun rejecting_requires_a_written_reason_before_touching_the_publication() = runBlocking {
        listOf("", "   ", "corto").forEach { reason ->
            assertEquals(ModerationResult.ReasonRequired, env.rejectPublication("pub_jhony_tutorias", reason))
            assertEquals(PublicationStatus.PENDIENTE, env.publications.getById("pub_jhony_tutorias")!!.status)
        }
        assertTrue(notificationsFor("pub_jhony_tutorias").isEmpty())
    }

    @Test
    fun rejecting_a_pending_publication_stores_the_trimmed_reason_and_notifies_the_author() = runBlocking {
        val reason = "  La descripción no coincide con el servicio ofrecido.  "

        val result = env.rejectPublication("pub_jhony_tutorias", reason)

        val updated = env.publications.getById("pub_jhony_tutorias")!!
        assertEquals(ModerationResult.Success, result)
        assertEquals(PublicationStatus.RECHAZADA, updated.status)
        assertEquals(reason.trim(), updated.rejectionReason)

        val notification = notificationsFor("pub_jhony_tutorias").single()
        assertEquals(SeedUsers.JHONY_ID, notification.userId)
        assertEquals(NotificationType.MODERATION_UPDATE, notification.type)
        assertEquals("Publicación rechazada", notification.title)
        assertTrue(notification.message.contains(reason.trim()))
        assertTrue(notification.message.contains(updated.title))
    }

    @Test
    fun rejecting_uses_the_provider_when_the_author_id_is_blank() = runBlocking {
        env.publications.add(pendingWithBlankAuthor())

        env.rejectPublication("custom_pending", "Motivo suficientemente largo")

        assertEquals("provider_1", notificationsFor("custom_pending").single().userId)
    }

    @Test
    fun rejecting_only_accepts_pending_publications() = runBlocking {
        listOf(
            "pub_jhony_plomeria" to PublicationStatus.VERIFICADA,
            "pub_jhony_paseo" to PublicationStatus.RECHAZADA,
            "pub_jhony_computadores" to PublicationStatus.FINALIZADA
        ).forEach { (id, status) ->
            assertEquals(
                ModerationResult.NotAllowed(status),
                env.rejectPublication(id, "Motivo suficientemente largo")
            )
            assertEquals(status, env.publications.getById(id)!!.status)
        }
    }

    @Test
    fun rejecting_an_unknown_publication_reports_not_found() = runBlocking {
        assertEquals(
            ModerationResult.NotFound,
            env.rejectPublication("no_existe", "Motivo suficientemente largo")
        )
    }

    @Test
    fun finalizing_sets_the_finalized_status_and_timestamp() = runBlocking {
        val result = env.finalizePublication("pub_jhony_tutorias")

        val updated = env.publications.getById("pub_jhony_tutorias")!!
        assertEquals(ModerationResult.Success, result)
        assertEquals(PublicationStatus.FINALIZADA, updated.status)
        assertEquals(env.nowMillis, updated.finalizedAtMillis)
    }

    @Test
    fun finalizing_keeps_the_verification_date() = runBlocking {
        val verifiedAt = env.publications.getById("pub_jhony_plomeria")!!.verifiedAtMillis

        env.finalizePublication("pub_jhony_plomeria")

        assertEquals(verifiedAt, env.publications.getById("pub_jhony_plomeria")!!.verifiedAtMillis)
    }

    @Test
    fun finalizing_an_already_finalized_publication_is_not_allowed() = runBlocking {
        assertEquals(
            ModerationResult.NotAllowed(PublicationStatus.FINALIZADA),
            env.finalizePublication("pub_jhony_computadores")
        )
    }

    @Test
    fun finalizing_an_unknown_publication_reports_not_found() = runBlocking {
        assertEquals(ModerationResult.NotFound, env.finalizePublication("no_existe"))
    }

    @Test
    fun pending_publications_are_exposed_oldest_first() = runBlocking {
        val pending = env.observePendingPublications().first()

        assertEquals(listOf("pub_valentina_soporte", "pub_jhony_tutorias"), pending.map { it.id })
        assertTrue(pending.all { it.status == PublicationStatus.PENDIENTE })
    }

    @Test
    fun pending_queue_reflects_changes() = runBlocking {
        env.publications.add(
            Publication(
                id = "older_pending",
                title = "Pendiente antigua",
                description = "Descripción",
                authorId = SeedUsers.JHONY_ID,
                status = PublicationStatus.PENDIENTE,
                createdAtMillis = env.publications.getById("pub_valentina_soporte")!!.createdAtMillis - 1
            )
        )

        val pending = env.observePendingPublications().first()

        assertEquals("older_pending", pending.first().id)
        assertEquals(3, pending.size)
    }

    @Test
    fun metrics_reflect_the_current_publications() = runBlocking {
        val metrics = env.observeModerationMetrics().first()

        assertEquals(2, metrics.pending)
        assertEquals(0, metrics.verifiedToday)
        assertEquals(1, metrics.rejected)
        assertEquals(1, metrics.finalized)
    }

    @Test
    fun verified_today_only_counts_publications_verified_on_the_current_day() = runBlocking {
        env.nowMillis = env.publications.getById("pub_jhony_plomeria")!!.verifiedAtMillis!!

        val metrics = env.observeModerationMetrics().first()

        assertEquals(1, metrics.verifiedToday)
    }

    @Test
    fun metrics_update_after_a_publication_is_approved() = runBlocking {
        env.approvePublication("pub_jhony_tutorias")

        val metrics = env.observeModerationMetrics().first()

        assertEquals(1, metrics.pending)
        assertEquals(1, metrics.verifiedToday)
    }
}
