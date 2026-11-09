package co.edu.uniquindio.servify.data.repository.memory

import co.edu.uniquindio.servify.data.seed.SeedCategories
import co.edu.uniquindio.servify.domain.model.Category
import co.edu.uniquindio.servify.domain.repository.CategoryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class InMemoryCategoryRepository @Inject constructor() : CategoryRepository {

    override fun observeCategories(): Flow<List<Category>> = flowOf(SeedCategories.categories)
}
