package co.edu.uniquindio.servify.domain.usecase.publication

import co.edu.uniquindio.servify.domain.model.AiClassification
import co.edu.uniquindio.servify.domain.model.CatalogCategory
import co.edu.uniquindio.servify.domain.repository.AiClassifierRepository
import co.edu.uniquindio.servify.domain.rules.KeywordCategoryRules
import kotlinx.coroutines.CancellationException
import javax.inject.Inject

class ClassifyServiceWithAiUseCase @Inject constructor(
    private val aiClassifierRepository: AiClassifierRepository
) {
    suspend operator fun invoke(
        title: String,
        description: String,
        catalog: List<CatalogCategory>
    ): AiClassification? {
        val remote = try {
            aiClassifierRepository.classify(title, description, catalog)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            null
        }
        return remote ?: KeywordCategoryRules.classify(title, description, catalog)
    }
}
