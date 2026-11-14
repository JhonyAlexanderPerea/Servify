package co.edu.uniquindio.servify.data.repository.memory

import co.edu.uniquindio.servify.domain.model.AiClassification
import co.edu.uniquindio.servify.domain.model.CatalogCategory
import co.edu.uniquindio.servify.domain.repository.AiClassifierRepository
import co.edu.uniquindio.servify.domain.rules.KeywordCategoryRules

class InMemoryAiClassifierRepository : AiClassifierRepository {
    override suspend fun classify(
        title: String,
        description: String,
        catalog: List<CatalogCategory>
    ): AiClassification? = KeywordCategoryRules.classify(title, description, catalog)
}
