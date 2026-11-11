package co.edu.uniquindio.servify.data

import co.edu.uniquindio.servify.data.repository.InMemoryPublicationRepository
import co.edu.uniquindio.servify.data.seed.SeedPublications
import co.edu.uniquindio.servify.data.seed.SeedUsers
import co.edu.uniquindio.servify.domain.model.Publication
import co.edu.uniquindio.servify.domain.model.PublicationStatus
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class InMemoryPublicationRepositoryTest {

    private fun newRepo() = InMemoryPublicationRepository(SeedPublications.publications)

    private fun newPublication(id: String = "pub_new") = Publication(
        id = id, authorId = SeedUsers.JUAN_ID, title = "Nueva", category = "Hogar",
        subcategory = "Limpieza", description = "d", priceRange = "$1", zone = "z", city = "c"
    )

    @Test
    fun starts_with_the_seed() {
        assertEquals(SeedPublications.publications.size, newRepo().snapshot().size)
    }

    @Test
    fun getById_returns_the_publication_or_null() {
        val repo = newRepo()
        assertNotNull(repo.getById("pub_jhony_plomeria"))
        assertNull(repo.getById("no_existe"))
    }

    @Test
    fun observeByAuthor_only_emits_that_authors_publications() = runBlocking {
        val mine = newRepo().observeByAuthor(SeedUsers.JHONY_ID).first()

        assertEquals(5, mine.size)
        assertTrue(mine.all { it.authorId == SeedUsers.JHONY_ID })
    }

    @Test
    fun add_makes_the_publication_visible_to_observers() = runBlocking {
        val repo = newRepo()
        repo.add(newPublication())

        assertEquals(SeedPublications.publications.size + 1, repo.publications.first().size)
        assertNotNull(repo.getById("pub_new"))
    }

    @Test
    fun update_applies_the_transform_and_returns_the_result() {
        val repo = newRepo()
        val updated = repo.update("pub_jhony_tutorias") { it.copy(status = PublicationStatus.VERIFICADA) }

        assertEquals(PublicationStatus.VERIFICADA, updated?.status)
        assertEquals(PublicationStatus.VERIFICADA, repo.getById("pub_jhony_tutorias")?.status)
    }

    @Test
    fun update_of_an_unknown_id_changes_nothing() {
        val repo = newRepo()
        assertNull(repo.update("no_existe") { it.copy(title = "x") })
        assertEquals(SeedPublications.publications.size, repo.snapshot().size)
    }

    @Test
    fun delete_removes_the_publication_once() {
        val repo = newRepo()

        assertTrue(repo.delete("pub_jhony_paseo"))
        assertNull(repo.getById("pub_jhony_paseo"))
        assertFalse(repo.delete("pub_jhony_paseo"))
    }
}
