package co.edu.uniquindio.servify.domain.repository

import co.edu.uniquindio.servify.domain.model.Category
import kotlinx.coroutines.flow.Flow

/** Catálogo de categorías de servicios. */
interface CategoryRepository {
    fun observeCategories(): Flow<List<Category>>
}
