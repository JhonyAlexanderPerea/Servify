package co.edu.uniquindio.servify.domain.usecase.notification

import co.edu.uniquindio.servify.domain.model.NotificationType
import co.edu.uniquindio.servify.testutil.TestEnvironment
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class GenerateRejectionNotificationTest {

    private val env = TestEnvironment()

    private suspend fun rejectionNotifications() =
        env.notifications.observeNotifications().first()
            .filter { it.title == "Publicación rechazada" }

    @Test
    fun rejection_notification_contains_the_reason_and_targets_the_publication() = runBlocking {
        env.generateNotification.generateRejectionNotification(
            userId = "user_1",
            serviceTitle = "Clases de matemáticas",
            reason = "La descripción no es suficiente.",
            publicationId = "pub_1"
        )

        val notification = rejectionNotifications().single()
        assertEquals("user_1", notification.userId)
        assertEquals(NotificationType.MODERATION_UPDATE, notification.type)
        assertEquals("pub_1", notification.targetId)
        assertEquals("Publicación rechazada", notification.title)
        assertEquals(
            "Tu publicación 'Clases de matemáticas' fue rechazada. Motivo: La descripción no es suficiente.",
            notification.message
        )
        assertFalse(notification.isRead)
    }

    @Test
    fun each_rejection_notification_has_its_own_id() = runBlocking {
        env.generateNotification.generateRejectionNotification(
            userId = "user_1",
            serviceTitle = "Servicio A",
            reason = "Motivo suficientemente largo",
            publicationId = "pub_1"
        )
        env.generateNotification.generateRejectionNotification(
            userId = "user_1",
            serviceTitle = "Servicio B",
            reason = "Otro motivo suficientemente largo",
            publicationId = "pub_2"
        )

        val notifications = rejectionNotifications()
        assertEquals(2, notifications.size)
        assertEquals(2, notifications.map { it.id }.toSet().size)
        notifications.forEach { assertTrue(it.id.startsWith("notif_")) }
    }

    @Test
    fun rejection_notifications_are_prepended_to_the_list() = runBlocking {
        env.generateNotification.generateRejectionNotification(
            userId = "user_1",
            serviceTitle = "Servicio",
            reason = "Motivo suficientemente largo",
            publicationId = "pub_new"
        )

        val first = env.notifications.observeNotifications().first().first()

        assertEquals("pub_new", first.targetId)
    }
}
