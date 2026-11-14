package co.edu.uniquindio.servify.data.remote.ai

import co.edu.uniquindio.servify.domain.model.AiClassification
import co.edu.uniquindio.servify.domain.model.CatalogCategory
import com.google.gson.Gson
import java.text.Normalizer

/** Convierte la respuesta de texto del modelo en una clasificación validada contra el catálogo. */
object AiResponseParser {

    fun parse(content: String?, catalog: List<CatalogCategory>): AiClassification? {
        if (content.isNullOrBlank()) return null

        val start = content.indexOf('{')
        val end = content.lastIndexOf('}')
        if (start < 0 || end <= start) return null

        val dto = try {
            Gson().fromJson(content.substring(start, end + 1), AiClassificationDto::class.java)
        } catch (e: Exception) {
            null
        } ?: return null

        val rawCategory = dto.category?.trim().orEmpty()
        if (rawCategory.isEmpty()) return null

        val category = catalog.firstOrNull { normalize(it.name) == normalize(rawCategory) } ?: return null
        val rawSubcategory = dto.subcategory?.trim().orEmpty()
        val subcategory = if (rawSubcategory.isEmpty()) {
            null
        } else {
            category.subcategories.firstOrNull { normalize(it) == normalize(rawSubcategory) }
        }

        return AiClassification(
            category = category.name,
            subcategory = subcategory,
            confidence = (dto.confidence ?: 0).coerceIn(0, 100)
        )
    }

    private fun normalize(text: String): String {
        val decomposed = Normalizer.normalize(text.lowercase(), Normalizer.Form.NFD)
        return decomposed.replace(Regex("\\p{M}+"), "")
    }
}
