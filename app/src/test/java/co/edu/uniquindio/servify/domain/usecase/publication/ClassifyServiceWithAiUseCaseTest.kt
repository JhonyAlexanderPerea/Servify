package co.edu.uniquindio.servify.domain.usecase.publication

import co.edu.uniquindio.servify.domain.model.AiClassification
import co.edu.uniquindio.servify.domain.model.CatalogCategory
import co.edu.uniquindio.servify.domain.repository.AiClassifierRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertThrows
import org.junit.Test
import java.io.IOException

class ClassifyServiceWithAiUseCaseTest {

    private val catalog = listOf(
        CatalogCategory("Hogar", listOf("Plomería")),
        CatalogCategory("Tecnología", listOf("Reparación de equipos"))
    )

    @Test
    fun uses_the_remote_result_when_it_responds() = runBlocking {
        val remote = AiClassification("Tecnología", "Reparación de equipos", 90)
        val useCase = ClassifyServiceWithAiUseCase(FakeClassifier(remote))

        val result = useCase("Fuga de agua", "Reparación de tuberías", catalog)

        assertEquals(remote, result)
    }

    @Test
    fun falls_back_to_local_rules_when_remote_returns_null() = runBlocking {
        val useCase = ClassifyServiceWithAiUseCase(FakeClassifier(null))

        val result = useCase("Fuga de agua", "Reparación de tuberías", catalog)

        assertEquals("Hogar", result?.category)
        assertEquals("Plomería", result?.subcategory)
    }

    @Test
    fun falls_back_to_local_rules_when_remote_throws_io_exception() = runBlocking {
        val useCase = ClassifyServiceWithAiUseCase(FakeClassifier(error = IOException("sin red")))

        val result = useCase("Reparación de laptops", "Mantenimiento de computadores", catalog)

        assertEquals("Tecnología", result?.category)
        assertEquals("Reparación de equipos", result?.subcategory)
    }

    @Test
    fun propagates_cancellation_exception() {
        val useCase = ClassifyServiceWithAiUseCase(FakeClassifier(error = CancellationException("cancelado")))

        assertThrows(CancellationException::class.java) {
            runBlocking { useCase("Reparación de laptops", "Mantenimiento", catalog) }
        }
    }

    @Test
    fun returns_null_when_neither_remote_nor_local_can_classify() = runBlocking {
        val useCase = ClassifyServiceWithAiUseCase(FakeClassifier(null))

        val result = useCase("Servicio general", "Acompañamiento integral", catalog)

        assertNull(result)
    }

    private class FakeClassifier(
        private val result: AiClassification? = null,
        private val error: Exception? = null
    ) : AiClassifierRepository {
        override suspend fun classify(
            title: String,
            description: String,
            catalog: List<CatalogCategory>
        ): AiClassification? {
            error?.let { throw it }
            return result
        }
    }
}
