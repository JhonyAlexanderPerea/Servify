package co.edu.uniquindio.servify.domain.usecase.category

import co.edu.uniquindio.servify.features.home.model.CategoryItem
import co.edu.uniquindio.servify.features.home.model.HomeMockData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject

/**
 * Caso de uso para obtener las categorías de servicios disponibles.
 */
class ObserveCategoriesUseCase @Inject constructor() {
    operator fun invoke(): Flow<List<CategoryItem>> = flowOf(HomeMockData.categories)
}
