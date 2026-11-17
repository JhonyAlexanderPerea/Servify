package co.edu.uniquindio.servify.domain.model

/** Categoría del catálogo que se le ofrece a la IA (nombres tal como los ve el usuario). */
data class CatalogCategory(val name: String, val subcategories: List<String>)

/** Resultado de clasificar una publicación. [confidence] va de 0 a 100. */
data class AiClassification(val category: String, val subcategory: String?, val confidence: Int)
