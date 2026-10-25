package co.edu.uniquindio.servify.data

import co.edu.uniquindio.servify.data.seed.SeedPublications
import co.edu.uniquindio.servify.data.seed.SeedUsers
import co.edu.uniquindio.servify.domain.model.PublicationStatus
import co.edu.uniquindio.servify.domain.rules.CredentialRules
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Los datos semilla son la base de todas las pruebas y de la demo de la app: si se rompen
 * (autor inexistente, estado sin cubrir) fallan muchas pantallas, así que se validan aquí.
 */
class SeedDataTest {

    private val publications = SeedPublications.publications
    private val users = SeedUsers.users

    @Test
    fun ids_are_unique() {
        assertEquals(users.size, users.map { it.id }.toSet().size)
        assertEquals(publications.size, publications.map { it.id }.toSet().size)
    }

    @Test
    fun every_publication_has_an_existing_author() {
        val userIds = users.map { it.id }.toSet()
        assertTrue(publications.all { it.authorId in userIds })
    }

    @Test
    fun the_moderator_never_authors_publications() {
        assertTrue(publications.none { it.authorId == SeedUsers.MODERATOR_ID })
    }

    @Test
    fun every_user_has_valid_credentials() {
        users.forEach { user ->
            assertTrue("${user.email} no es un correo válido", CredentialRules.isValidEmail(user.email))
            val password = SeedUsers.credentials[user.email]
            assertTrue("${user.email} sin contraseña", password != null)
            assertTrue("${user.email} con contraseña débil", CredentialRules.isStrongEnough(password!!))
        }
    }

    @Test
    fun the_main_test_account_covers_the_four_statuses() {
        val statuses = publications.filter { it.authorId == SeedUsers.JHONY_ID }.map { it.status }.toSet()
        assertEquals(PublicationStatus.entries.toSet(), statuses)
    }

    @Test
    fun rejected_publications_always_carry_a_reason() {
        val rejected = publications.filter { it.status == PublicationStatus.RECHAZADA }
        assertTrue(rejected.isNotEmpty())
        assertTrue(rejected.all { !it.rejectionReason.isNullOrBlank() })
    }

    @Test
    fun verified_and_finalized_were_verified_but_pending_and_rejected_were_not() {
        publications.forEach {
            when (it.status) {
                PublicationStatus.VERIFICADA, PublicationStatus.FINALIZADA -> assertTrue(it.wasVerified)
                PublicationStatus.PENDIENTE, PublicationStatus.RECHAZADA -> assertFalse(it.wasVerified)
            }
        }
    }

    @Test
    fun finalized_publications_have_a_finalization_date() {
        publications.filter { it.status == PublicationStatus.FINALIZADA }
            .forEach { assertTrue(it.finalizedAtMillis != null) }
    }

    @Test
    fun nobody_votes_for_their_own_publication() {
        assertTrue(publications.none { it.authorId in it.interestedUserIds })
    }
}
