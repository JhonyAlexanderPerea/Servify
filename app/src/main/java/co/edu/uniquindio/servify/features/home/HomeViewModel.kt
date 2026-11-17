package co.edu.uniquindio.servify.features.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.edu.uniquindio.servify.domain.location.LocationTracker
import co.edu.uniquindio.servify.domain.model.Publication
import co.edu.uniquindio.servify.domain.rules.FeedRules
import co.edu.uniquindio.servify.domain.rules.matchesCategory
import co.edu.uniquindio.servify.domain.usecase.auth.ObserveSessionUseCase
import co.edu.uniquindio.servify.domain.usecase.category.ObserveCategoriesUseCase
import co.edu.uniquindio.servify.domain.usecase.notification.ObserveNotificationsUseCase
import co.edu.uniquindio.servify.domain.usecase.publication.ObserveServicesUseCase
import co.edu.uniquindio.servify.domain.usecase.publication.ToggleInterestUseCase
import co.edu.uniquindio.servify.domain.util.GeoDistanceCalculator
import co.edu.uniquindio.servify.features.home.model.CategoryItem
import co.edu.uniquindio.servify.features.home.model.HomeMockData
import co.edu.uniquindio.servify.features.home.model.toCategoryItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
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
    val selectedFilterChips: Set<String> = emptySet(),
    val isMapView: Boolean = false,
    val isLoading: Boolean = false,
    val userMessage: String? = null,
    val userName: String = "",
    val userInitials: String = "",
    val userAvatarUrl: String? = null,
    val unreadCount: Int = 0,
    val userLatitude: Double? = null,
    val userLongitude: Double? = null
)

/** Filtros rápidos de Home con geolocalización real. */
val HomeQuickFilters = listOf("Cerca de mí", "Ciudad", "Precio", "Mejor valorados", "Verificados")

private data class HomeFilters(val query: String, val categoryId: String?, val chips: Set<String>)
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
    private val toggleInterestUseCase: ToggleInterestUseCase,
    private val locationTracker: LocationTracker,
    private val geoDistanceCalculator: GeoDistanceCalculator
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    private val _selectedCategoryId = MutableStateFlow<String?>(null)
    private val _selectedFilterChips = MutableStateFlow<Set<String>>(emptySet())
    private val _isMapView = MutableStateFlow(false)
    private val _userLocation = MutableStateFlow<Pair<Double, Double>?>(null)

    init {
        fetchUserLocation()
    }

    fun fetchUserLocation() {
        viewModelScope.launch {
            val loc = locationTracker.getCurrentLocation()
            if (loc != null) {
                _userLocation.value = loc
            }
        }
    }

    fun onUserLocationUpdated(latitude: Double, longitude: Double) {
        _userLocation.value = Pair(latitude, longitude)
    }

    fun onLocationPermissionGranted() {
        fetchUserLocation()
    }

    private val filters = combine(_searchQuery, _selectedCategoryId, _selectedFilterChips) { query, categoryId, chips ->
        HomeFilters(query, categoryId, chips)
    }

    private val userInfo = combine(observeSessionUseCase(), observeNotificationsUseCase()) { user, notifications ->
        val fullName = user?.name.orEmpty()
        val userId = user?.id.orEmpty()
        HomeUserInfo(
            name = fullName.substringBefore(' '),
            initials = fullName.split(' ').filter { it.isNotBlank() }.take(2)
                .joinToString("") { it.first().uppercase() },
            avatarUrl = user?.avatarUrl,
            unread = notifications.count { it.userId == userId && !it.isRead }
        )
    }

    val uiState: StateFlow<HomeUiState> = combine(
        observeServicesUseCase(),
        observeCategoriesUseCase().map { list -> list.map { it.toCategoryItem() } },
        filters,
        userInfo,
        _userLocation
    ) { allServices, categories, filters, user, userLoc ->

        // El feed público solo muestra publicaciones verificadas.
        val publicServices = FeedRules.publicFeed(allServices)

        // Asocia distancias calculadas con Haversine a cada publicación.
        val servicesWithDistance = FeedRules.attachDistances(
            publications = publicServices,
            userLat = userLoc?.first,
            userLng = userLoc?.second,
            calculator = geoDistanceCalculator
        )

        val selectedCategoryLabel = categories
            .find { it.id == filters.categoryId }
            ?.label

        val filtered = filterPublications(
            services = servicesWithDistance,
            query = filters.query,
            categoryId = filters.categoryId,
            categoryLabel = selectedCategoryLabel,
            filterChips = filters.chips,
            userLat = userLoc?.first,
            userLng = userLoc?.second
        )

        HomeUiState(
            services = servicesWithDistance,
            filteredServices = filtered,
            categories = categories,
            selectedCategoryId = filters.categoryId,
            searchQuery = filters.query,
            selectedFilterChips = filters.chips,
            isMapView = _isMapView.value,
            isLoading = false,
            userName = user.name,
            userInitials = user.initials,
            userAvatarUrl = user.avatarUrl,
            unreadCount = user.unread,
            userLatitude = userLoc?.first,
            userLongitude = userLoc?.second
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
        _selectedFilterChips.update { current ->
            if (current.contains(filterLabel)) {
                current - filterLabel
            } else {
                current + filterLabel
            }
        }
        if (filterLabel == "Cerca de mí" && !_selectedFilterChips.value.contains("Cerca de mí") && _userLocation.value == null) {
            fetchUserLocation()
        }
    }

    fun onClearFilters() {
        _selectedFilterChips.value = emptySet()
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
        categoryLabel: String?,
        filterChips: Set<String>,
        userLat: Double?,
        userLng: Double?
    ): List<Publication> {
        var result = services

        // 1. Filtrado por categoría
        result = result.filter { pub ->
            pub.matchesCategory(categoryId, categoryLabel)
        }

        // 2. Motor de búsqueda robusto con normalización (ignorando tildes y mayúsculas)
        if (query.isNotBlank()) {
            val q = query.trim().normalizeSearch()
            result = result.filter { pub ->
                pub.title.normalizeSearch().contains(q) ||
                        pub.description.normalizeSearch().contains(q) ||
                        pub.locationName.normalizeSearch().contains(q) ||
                        (pub.subcategoryId?.normalizeSearch()?.contains(q) == true) ||
                        pub.provider.name.normalizeSearch().contains(q) ||
                        pub.category.normalizeSearch().contains(q)
            }
        }

        // 3. Aplicación simultánea de múltiples filtros (Chips independientes)
        if (filterChips.contains("Cerca de mí")) {
            result = FeedRules.filterNearMe(result, userLat, userLng, maxRadiusKm = 50.0)
        }
        if (filterChips.contains("Verificados")) {
            result = result.filter { it.provider.isVerified || it.wasVerified }
        }
        if (filterChips.contains("Mejor valorados")) {
            result = result.sortedByDescending { it.ratingAverage }
        }
        if (filterChips.contains("Precio")) {
            result = result.sortedBy { it.price }
        }
        if (filterChips.contains("Ciudad")) {
            val city = HomeMockData.currentUserCity.substringBefore(",").trim()
            result = result.filter {
                it.locationName.contains(city, ignoreCase = true) || it.city.contains(city, ignoreCase = true)
            }
        }

        return result
    }

    private fun String.normalizeSearch(): String {
        val decomposed = java.text.Normalizer.normalize(this.lowercase(), java.text.Normalizer.Form.NFD)
        return decomposed.replace(Regex("\\p{M}+"), "")
    }
}
