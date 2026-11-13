package co.edu.uniquindio.servify.features.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.edu.uniquindio.servify.domain.model.Publication
import co.edu.uniquindio.servify.domain.usecase.category.ObserveCategoriesUseCase
import co.edu.uniquindio.servify.features.home.model.toCategoryItem
import co.edu.uniquindio.servify.domain.usecase.publication.ObserveServicesUseCase
import co.edu.uniquindio.servify.domain.usecase.publication.ToggleInterestUseCase
import co.edu.uniquindio.servify.features.home.model.CategoryItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import co.edu.uniquindio.servify.domain.rules.FeedRules
import co.edu.uniquindio.servify.domain.usecase.auth.ObserveSessionUseCase
import co.edu.uniquindio.servify.domain.usecase.notification.ObserveNotificationsUseCase
import co.edu.uniquindio.servify.features.home.model.HomeMockData
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
    val userMessage: String? = null,
    val userName: String = "",
    val userInitials: String = "",
    val userAvatarUrl: String? = null,
    val unreadCount: Int = 0
)

/** Filtros rápidos de Home. "Cerca de mí" no filtra (aún no hay geolocalización real). */
val HomeQuickFilters = listOf("Cerca de mí", "Ciudad", "Precio", "Mejor valorados", "Verificados")

private data class HomeFilters(val query: String, val categoryId: String?, val chip: String)
private data class HomeUserInfo(
    val name: String,
    val initials: String,
    val avatarUrl: String?,
    val unread: Int
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    observeServicesUseCase: ObserveServicesUseCase,
    observeCategoriesUseCase: ObserveCategoriesUseCase,
    observeSessionUseCase: ObserveSessionUseCase,
    observeNotificationsUseCase: ObserveNotificationsUseCase,
    private val toggleInterestUseCase: ToggleInterestUseCase
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    private val _selectedCategoryId = MutableStateFlow<String?>(null)
    private val _selectedFilterChip = MutableStateFlow("Cerca de mí")
    private val _isMapView = MutableStateFlow(false)

    private val filters = combine(_searchQuery, _selectedCategoryId, _selectedFilterChip) { query, categoryId, chip ->
        HomeFilters(query, categoryId, chip)
    }

    // Saludo, iniciales y contador de notificaciones salen de la sesión real (antes eran datos fijos).
    private val userInfo = combine(observeSessionUseCase(), observeNotificationsUseCase()) { user, notifications ->
        val fullName = user?.name.orEmpty()
        HomeUserInfo(
            name = fullName.substringBefore(' '),
            initials = fullName.split(' ').filter { it.isNotBlank() }.take(2)
                .joinToString("") { it.first().uppercase() },
            avatarUrl = user?.avatarUrl,
            unread = notifications.count { !it.isRead }
        )
    }

    val uiState: StateFlow<HomeUiState> = combine(
        observeServicesUseCase(),
        observeCategoriesUseCase().map { list -> list.map { it.toCategoryItem() } },
        filters,
        userInfo
    ) { allServices, categories, filters, user ->

        // El feed público solo muestra publicaciones verificadas (las pendientes esperan al moderador).
        val services = FeedRules.publicFeed(allServices)

        val filtered = filterPublications(
            services = services,
            query = filters.query,
            categoryId = filters.categoryId,
            filterChip = filters.chip
        )

        HomeUiState(
            services = services,
            filteredServices = filtered,
            categories = categories,
            selectedCategoryId = filters.categoryId,
            searchQuery = filters.query,
            selectedFilterChip = filters.chip,
            isMapView = _isMapView.value,
            isLoading = false,
            userName = user.name,
            userInitials = user.initials,
            userAvatarUrl = user.avatarUrl,
            unreadCount = user.unread
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

    fun onClearFilters() {
        _selectedFilterChip.value = HomeQuickFilters.first()
        _selectedCategoryId.value = null
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
            "Ciudad" -> {
                val city = HomeMockData.currentUserCity.substringBefore(",").trim()
                result.filter {
                    it.locationName.contains(city, ignoreCase = true) || it.city.contains(city, ignoreCase = true)
                }
            }
            else -> result
        }
    }
}
