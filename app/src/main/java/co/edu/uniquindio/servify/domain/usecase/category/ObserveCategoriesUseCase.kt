package co.edu.uniquindio.servify.domain.usecase.category

import co.edu.uniquindio.servify.domain.model.Category
import co.edu.uniquindio.servify.domain.repository.CategoryRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Caso de uso para obtener las categorías de servicios disponibles.
 */
class ObserveCategoriesUseCase @Inject constructor(
    private val categoryRepository: CategoryRepository
) {
    operator fun invoke(): Flow<List<Category>> = categoryRepository.observeCategories()
}
