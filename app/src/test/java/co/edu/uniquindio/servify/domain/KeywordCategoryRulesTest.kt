package co.edu.uniquindio.servify.domain

import co.edu.uniquindio.servify.domain.model.CatalogCategory
import co.edu.uniquindio.servify.domain.rules.KeywordCategoryRules
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class KeywordCategoryRulesTest {

    private val catalog = listOf(
        CatalogCategory(
            "Hogar",
            listOf("Plomería", "Electricidad", "Limpieza", "Pintura", "Jardinería", "Carpintería")
        ),
        CatalogCategory(
            "Educación",
            listOf("Clases particulares", "Idiomas", "Música", "Refuerzo escolar")
        ),
        CatalogCategory(
            "Mascotas",
            listOf("Paseo de mascotas", "Cuidado en casa", "Peluquería canina")
        ),
        CatalogCategory(
            "Tecnología",
            listOf("Reparación de equipos", "Soporte técnico", "Diseño web", "Redes")
        ),
        CatalogCategory(
            "Transporte",
            listOf("Mudanzas", "Mensajería express", "Acarreos")
        ),
        CatalogCategory(
            "Salud y Bienestar",
            listOf("Enfermería a domicilio", "Fisioterapia", "Masajes relajantes")
        )
    )

    @Test
    fun classifies_computers_as_technology_equipment_repair() {
        val result = KeywordCategoryRules.classify(
            title = "Reparación de computadores y laptops",
            description = "Formateo, mantenimiento e instalación de programas.",
            catalog = catalog
        )

        assertEquals("Tecnología", result?.category)
        assertEquals("Reparación de equipos", result?.subcategory)
    }

    @Test
    fun classifies_a_water_leak_as_home_plumbing() {
        val result = KeywordCategoryRules.classify(
            title = "Fuga de agua urgente",
            description = "Reparación de tuberías y grifos.",
            catalog = catalog
        )

        assertEquals("Hogar", result?.category)
        assertEquals("Plomería", result?.subcategory)
    }

    @Test
    fun returns_null_when_there_are_no_recognizable_keywords() {
        val result = KeywordCategoryRules.classify(
            title = "Acompañamiento general",
            description = "Ofrezco un servicio integral para el hogar.",
            catalog = catalog
        )

        assertNull(result)
    }

    @Test
    fun returns_the_exact_catalog_names() {
        val customCatalog = listOf(
            CatalogCategory("Tecnología Avanzada", listOf("Reparación de equipos"))
        )

        val result = KeywordCategoryRules.classify(
            title = "Reparación de laptops",
            description = "Mantenimiento de computadores.",
            catalog = customCatalog
        )

        assertEquals("Tecnología Avanzada", result?.category)
        assertEquals("Reparación de equipos", result?.subcategory)
    }

    @Test
    fun ignores_keywords_whose_subcategory_is_not_in_the_catalog() {
        val onlyTechnology = listOf(
            CatalogCategory("Tecnología", listOf("Reparación de equipos"))
        )

        val result = KeywordCategoryRules.classify(
            title = "Jardinería y poda de césped",
            description = "Mantenimiento de plantas y jardines.",
            catalog = onlyTechnology
        )

        assertNull(result)
    }
}
