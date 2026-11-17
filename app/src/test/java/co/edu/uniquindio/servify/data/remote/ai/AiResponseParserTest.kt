package co.edu.uniquindio.servify.data.remote.ai

import co.edu.uniquindio.servify.domain.model.CatalogCategory
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class AiResponseParserTest {

    private val catalog = listOf(
        CatalogCategory("Hogar", listOf("Plomería", "Electricidad")),
        CatalogCategory("Tecnología", listOf("Reparación de equipos", "Redes"))
    )

    @Test
    fun parses_clean_json() {
        val result = AiResponseParser.parse(
            """{"category":"Tecnología","subcategory":"Reparación de equipos","confidence":90}""",
            catalog
        )

        assertEquals("Tecnología", result?.category)
        assertEquals("Reparación de equipos", result?.subcategory)
        assertEquals(90, result?.confidence)
    }

    @Test
    fun parses_json_inside_a_markdown_block() {
        val result = AiResponseParser.parse(
            "```json\n{\"category\":\"Hogar\",\"subcategory\":\"Plomería\",\"confidence\":75}\n```",
            catalog
        )

        assertEquals("Hogar", result?.category)
        assertEquals("Plomería", result?.subcategory)
    }

    @Test
    fun parses_json_with_text_before_and_after() {
        val result = AiResponseParser.parse(
            "Claro, aquí está: {\"category\":\"Hogar\",\"subcategory\":\"Electricidad\",\"confidence\":80} Espero que sirva.",
            catalog
        )

        assertEquals("Hogar", result?.category)
        assertEquals("Electricidad", result?.subcategory)
    }

    @Test
    fun returns_null_for_an_unknown_category() {
        val result = AiResponseParser.parse(
            """{"category":"Deportes","subcategory":"Fútbol","confidence":80}""",
            catalog
        )

        assertNull(result)
    }

    @Test
    fun clears_an_invalid_subcategory_but_keeps_the_category() {
        val result = AiResponseParser.parse(
            """{"category":"Tecnología","subcategory":"Jardinería","confidence":70}""",
            catalog
        )

        assertEquals("Tecnología", result?.category)
        assertNull(result?.subcategory)
        assertEquals(70, result?.confidence)
    }

    @Test
    fun clamps_confidence_to_one_hundred() {
        val result = AiResponseParser.parse(
            """{"category":"Hogar","subcategory":"Plomería","confidence":150}""",
            catalog
        )

        assertEquals(100, result?.confidence)
    }

    @Test
    fun returns_null_for_broken_json() {
        val result = AiResponseParser.parse(
            """{"category":"Hogar","subcategory":""",
            catalog
        )

        assertNull(result)
    }

    @Test
    fun maps_case_and_accents_to_the_exact_catalog_names() {
        val result = AiResponseParser.parse(
            """{"category":"tecnologia","subcategory":"reparacion de equipos","confidence":60}""",
            catalog
        )

        assertEquals("Tecnología", result?.category)
        assertEquals("Reparación de equipos", result?.subcategory)
    }

    @Test
    fun returns_null_for_an_empty_category() {
        val result = AiResponseParser.parse(
            """{"category":"","subcategory":"","confidence":0}""",
            catalog
        )

        assertNull(result)
    }

    @Test
    fun returns_null_when_there_are_no_braces() {
        assertNull(AiResponseParser.parse("No pude clasificar el servicio", catalog))
    }
}
