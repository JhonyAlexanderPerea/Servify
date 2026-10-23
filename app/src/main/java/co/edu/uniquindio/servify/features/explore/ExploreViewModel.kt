package co.edu.uniquindio.servify.features.explore

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

data class ExploreUiState(
    val nearbyServices: List<Publication> = emptyList(),
    val categories: List<CategoryItem> = emptyList(),
    val selectedCategoryId: String? = null,
    val searchQuery: String = "",
    val selectedService: Publication? = null,
    val isBottomSheetExpanded: Boolean = false,
    val isLoading: Boolean = false
)

@HiltViewModel
class ExploreViewModel @Inject constructor(
    observeServicesUseCase: ObserveServicesUseCase,
    observeCategoriesUseCase: ObserveCategoriesUseCase,
    private val toggleInterestUseCase: ToggleInterestUseCase
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    private val _selectedCategoryId = MutableStateFlow<String?>(null)
    private val _selectedService = MutableStateFlow<Publication?>(null)

    val uiState: StateFlow<ExploreUiState> = combine(
        observeServicesUseCase(),
        observeCategoriesUseCase(),
        _searchQuery,
        _selectedCategoryId,
        _selectedService
    ) { services, categories, query, categoryId, selected ->

        val filtered = services.filter { pub ->
            val matchesCategory = categoryId.isNullOrBlank() || pub.categoryId.equals(categoryId, ignoreCase = true)
            val matchesQuery = query.isBlank() ||
                    pub.title.contains(query, ignoreCase = true) ||
                    pub.description.contains(query, ignoreCase = true) ||
                    pub.locationName.contains(query, ignoreCase = true) ||
                    pub.provider.name.contains(query, ignoreCase = true)

            matchesCategory && matchesQuery
        }

        ExploreUiState(
            nearbyServices = filtered,
            categories = categories,
            selectedCategoryId = categoryId,
            searchQuery = query,
            selectedService = selected,
            isBottomSheetExpanded = selected != null,
            isLoading = false
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = ExploreUiState(isLoading = true)
    )

    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
    }

    fun onCategorySelected(categoryId: String?) {
        _selectedCategoryId.update { current ->
            if (current == categoryId) null else categoryId
        }
    }

    fun onMarkerSelected(service: Publication?) {
        _selectedService.value = service
    }

    fun onDismissSelectedService() {
        _selectedService.value = null
    }

    fun onToggleInterest(serviceId: String) {
        viewModelScope.launch {
            toggleInterestUseCase(serviceId)
        }
    }
}
