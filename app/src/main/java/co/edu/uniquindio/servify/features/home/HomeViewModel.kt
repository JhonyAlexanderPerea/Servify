package co.edu.uniquindio.servify.features.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.edu.uniquindio.servify.domain.model.Publication
import co.edu.uniquindio.servify.domain.usecase.category.ObserveCategoriesUseCase
import co.edu.uniquindio.servify.domain.usecase.publication.ObserveServicesUseCase
import co.edu.uniquindio.servify.domain.usecase.publication.ToggleInterestUseCase
import co.edu.uniquindio.servify.features.home.model.CategoryItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class HomeUiState(
    val services: List<Publication> = emptyList(),
    val filteredServices: List<Publication> = emptyList(),
    val categories: List<CategoryItem> = emptyList(),
    val selectedCategoryId: String? = null,
    val searchQuery: String = "",
    val selectedFilterChip: String = "Cerca de mí",
    val isMapView: Boolean = false,
    val isLoading: Boolean = false,
    val userMessage: String? = null
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    observeServicesUseCase: ObserveServicesUseCase,
    observeCategoriesUseCase: ObserveCategoriesUseCase,
    private val toggleInterestUseCase: ToggleInterestUseCase
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    private val _selectedCategoryId = MutableStateFlow<String?>(null)
    private val _selectedFilterChip = MutableStateFlow("Cerca de mí")
    private val _isMapView = MutableStateFlow(false)

    val uiState: StateFlow<HomeUiState> = combine(
        observeServicesUseCase(),
        observeCategoriesUseCase(),
        _searchQuery,
        _selectedCategoryId,
        _selectedFilterChip
    ) { services, categories, query, categoryId, filterChip ->

        val filtered = filterPublications(
            services = services,
            query = query,
            categoryId = categoryId,
            filterChip = filterChip
        )

        HomeUiState(
            services = services,
            filteredServices = filtered,
            categories = categories,
            selectedCategoryId = categoryId,
            searchQuery = query,
            selectedFilterChip = filterChip,
            isMapView = _isMapView.value,
            isLoading = false
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = HomeUiState(isLoading = true)
    )

    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
    }

    fun onCategorySelected(categoryId: String?) {
        _selectedCategoryId.update { current ->
            if (current == categoryId) null else categoryId
        }
    }

    fun onFilterChipSelected(filterLabel: String) {
        _selectedFilterChip.value = filterLabel
    }

    fun onToggleMapView(isMap: Boolean) {
        _isMapView.value = isMap
    }

    fun onToggleInterest(serviceId: String) {
        viewModelScope.launch {
            toggleInterestUseCase(serviceId)
        }
    }

    private fun filterPublications(
        services: List<Publication>,
        query: String,
        categoryId: String?,
        filterChip: String
    ): List<Publication> {
        var result = services

        // 1. Filtrado por categoría
        if (!categoryId.isNullOrBlank()) {
            result = result.filter { pub ->
                pub.categoryId.equals(categoryId, ignoreCase = true)
            }
        }

        // 2. Filtrado por texto de búsqueda
        if (query.isNotBlank()) {
            val q = query.trim().lowercase()
            result = result.filter { pub ->
                pub.title.lowercase().contains(q) ||
                        pub.description.lowercase().contains(q) ||
                        pub.locationName.lowercase().contains(q) ||
                        (pub.subcategoryId?.lowercase()?.contains(q) == true) ||
                        pub.provider.name.lowercase().contains(q)
            }
        }

        // 3. Ordenamiento / Filtrado rápido por Chip
        return when (filterChip) {
            "Mejor valorados" -> result.sortedByDescending { it.ratingAverage }
            "Verificados" -> result.filter { it.provider.isVerified }
            "Precio" -> result.sortedBy { it.price }
            else -> result
        }
    }
}
