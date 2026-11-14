package co.edu.uniquindio.servify.features.publication.create

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.edu.uniquindio.servify.domain.model.AiClassification
import co.edu.uniquindio.servify.domain.model.CatalogCategory
import co.edu.uniquindio.servify.domain.model.Publication
import co.edu.uniquindio.servify.domain.model.PublicationStatus
import co.edu.uniquindio.servify.domain.rules.PointEvent
import co.edu.uniquindio.servify.domain.rules.PublicationRules
import co.edu.uniquindio.servify.domain.usecase.publication.ClassifyServiceWithAiUseCase
import co.edu.uniquindio.servify.domain.usecase.reputation.AwardPointsUseCase
import co.edu.uniquindio.servify.domain.usecase.publication.CreatePublicationUseCase
import co.edu.uniquindio.servify.domain.usecase.publication.GetPublicationByIdUseCase
import co.edu.uniquindio.servify.domain.usecase.publication.UpdatePublicationUseCase
import co.edu.uniquindio.servify.domain.usecase.user.GetCurrentUserUseCase
import co.edu.uniquindio.servify.features.publication.model.CreatePublicationMockData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class PublicationFormUiState(
    val publicationId: String? = null,
    val title: String = "",
    val titleError: String? = null,
    val description: String = "",
    val descriptionError: String? = null,
    val categoryId: String = "",
    val categoryError: String? = null,
    val subcategoryId: String = "",
    val subcategoryError: String? = null,
    val minPrice: String = "",
    val minPriceError: String? = null,
    val maxPrice: String = "",
    val maxPriceError: String? = null,
    val priceUnit: String = "por trabajo",
    val locationName: String = "Armenia, Quindío",
    val locationError: String? = null,
    val images: List<String> = emptyList(),
    val currentStep: Int = 1,
    val isFormValid: Boolean = false,
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    /** Id de la publicación que se acaba de crear o reenviar (para abrir "Publicación enviada"). */
    val savedPublicationId: String? = null,
    val isAiAnalyzing: Boolean = false,
    val aiSuggestion: AiClassification? = null,
    val userMessage: String? = null
)

@HiltViewModel
class PublicationFlowViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getPublicationByIdUseCase: GetPublicationByIdUseCase,
    private val createPublicationUseCase: CreatePublicationUseCase,
    private val updatePublicationUseCase: UpdatePublicationUseCase,
    private val getCurrentUserUseCase: GetCurrentUserUseCase,
    private val awardPointsUseCase: AwardPointsUseCase,
    private val classifyServiceWithAiUseCase: ClassifyServiceWithAiUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(PublicationFormUiState())
    val uiState: StateFlow<PublicationFormUiState> = _uiState.asStateFlow()

    private val catalog = CreatePublicationMockData.categoriesList.map {
        CatalogCategory(it.name, it.subcategories)
    }

    private var classifyJob: Job? = null
    private var lastClassifiedKey: String? = null

    init {
        val editingId: String? = savedStateHandle.get<String>("publicationId")
        if (!editingId.isNullOrBlank()) {
            loadPublicationForEditing(editingId)
        }
    }

    private fun loadPublicationForEditing(id: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val existing = getPublicationByIdUseCase(id)
            if (existing != null) {
                _uiState.update {
                    it.copy(
                        publicationId = existing.id,
                        title = existing.title,
                        description = existing.description,
                        categoryId = existing.categoryId.ifBlank { existing.category },
                        subcategoryId = existing.subcategoryId ?: existing.subcategory,
                        minPrice = initialMinPrice(existing).toString(),
                        maxPrice = initialMaxPrice(existing).toString(),
                        priceUnit = existing.priceUnit,
                        locationName = existing.locationName.ifBlank { existing.city.ifBlank { "Armenia, Quindío" } },
                        images = existing.imageUrls,
                        isLoading = false
                    )
                }
            } else {
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }

    fun onTitleChanged(newTitle: String) {
        _uiState.update {
            it.copy(
                title = newTitle,
                titleError = if (newTitle.length < 5) "El título debe tener al menos 5 caracteres" else null
            )
        }
        scheduleClassification()
    }

    fun onDescriptionChanged(newDescription: String) {
        _uiState.update {
            it.copy(
                description = newDescription,
                descriptionError = if (newDescription.length < 20) "La descripción debe tener al menos 20 caracteres" else null
            )
        }
        scheduleClassification()
    }

    fun onCategoryChanged(category: String) {
        _uiState.update {
            it.copy(
                categoryId = category,
                categoryError = if (category.isBlank()) "Selecciona una categoría" else null,
                subcategoryId = "",
                subcategoryError = null
            )
        }
    }

    fun onSubcategoryChanged(subcategory: String) {
        _uiState.update {
            it.copy(
                subcategoryId = subcategory,
                subcategoryError = if (subcategory.isBlank()) "Selecciona una subcategoría" else null
            )
        }
    }

    fun onMinPriceChanged(minStr: String) {
        val clean = minStr.filter { it.isDigit() }
        val parsed = clean.toDoubleOrNull()
        val error = when {
            clean.isBlank() -> "Ingresa el precio mínimo"
            parsed == null || parsed <= 0 -> "Ingresa un valor válido"
            else -> null
        }
        _uiState.update {
            it.copy(
                minPrice = clean,
                minPriceError = error
            )
        }
    }

    fun onMaxPriceChanged(maxStr: String) {
        val clean = maxStr.filter { it.isDigit() }
        val parsed = clean.toDoubleOrNull()
        val minParsed = _uiState.value.minPrice.toDoubleOrNull() ?: 0.0
        val error = when {
            clean.isBlank() -> "Ingresa el precio máximo"
            parsed == null || parsed <= 0 -> "Ingresa un valor válido"
            minParsed > 0 && parsed < minParsed -> "El máximo no puede ser menor al mínimo"
            else -> null
        }
        _uiState.update {
            it.copy(
                maxPrice = clean,
                maxPriceError = error
            )
        }
    }

    fun onPriceUnitChanged(unit: String) {
        _uiState.update { it.copy(priceUnit = unit) }
    }

    fun onLocationChanged(location: String) {
        _uiState.update {
            it.copy(
                locationName = location,
                locationError = if (location.isBlank()) "La ubicación es obligatoria" else null
            )
        }
    }

    fun onAddImage(imageUrl: String) {
        _uiState.update { state ->
            if (state.images.size < 5) {
                state.copy(images = state.images + imageUrl)
            } else state
        }
    }

    fun onRemoveImage(index: Int) {
        _uiState.update { state ->
            val updated = state.images.toMutableList().apply {
                if (index in indices) removeAt(index)
            }
            state.copy(images = updated)
        }
    }

    fun onUseAiCategory() {
        val suggestion = _uiState.value.aiSuggestion ?: return
        _uiState.update {
            it.copy(
                categoryId = suggestion.category,
                subcategoryId = suggestion.subcategory.orEmpty(),
                categoryError = null,
                subcategoryError = null,
                aiSuggestion = null
            )
        }
    }

    fun onDismissAiSuggestion() {
        _uiState.update { it.copy(aiSuggestion = null) }
    }

    private fun scheduleClassification() {
        classifyJob?.cancel()
        val state = _uiState.value
        val title = state.title.trim()
        val description = state.description.trim()
        if (title.length < 5 || description.length < 20) {
            _uiState.update { it.copy(isAiAnalyzing = false, aiSuggestion = null) }
            return
        }
        val key = "$title|$description"
        if (key == lastClassifiedKey) return
        classifyJob = viewModelScope.launch {
            delay(800)
            _uiState.update { it.copy(isAiAnalyzing = true, aiSuggestion = null) }
            val suggestion = runCatching {
                classifyServiceWithAiUseCase(title, description, catalog)
            }.getOrNull()
            lastClassifiedKey = key
            _uiState.update { it.copy(isAiAnalyzing = false, aiSuggestion = suggestion) }
        }
    }

    fun validateForm(): Boolean {
        val state = _uiState.value
        val titleErr = if (state.title.trim().length < 5) "El título debe tener al menos 5 caracteres" else null
        val descErr = if (state.description.trim().length < 20) "La descripción debe tener al menos 20 caracteres" else null
        val catErr = if (state.categoryId.isBlank()) "Selecciona una categoría" else null
        val locErr = if (state.locationName.isBlank()) "La ubicación es obligatoria" else null

        val minVal = state.minPrice.toDoubleOrNull()
        val maxVal = state.maxPrice.toDoubleOrNull()
        val minErr = if (minVal == null || minVal <= 0) "Ingresa un precio mínimo válido" else null
        val maxErr = when {
            maxVal == null || maxVal <= 0 -> "Ingresa un precio máximo válido"
            minVal != null && maxVal < minVal -> "El máximo no puede ser menor al mínimo"
            else -> null
        }

        val isValid = titleErr == null && descErr == null && catErr == null && locErr == null && minErr == null && maxErr == null

        _uiState.update {
            it.copy(
                titleError = titleErr,
                descriptionError = descErr,
                categoryError = catErr,
                locationError = locErr,
                minPriceError = minErr,
                maxPriceError = maxErr,
                isFormValid = isValid
            )
        }

        return isValid
    }

    fun onNextStep() {
        val state = _uiState.value
        if (state.currentStep == 1) {
            val titleErr = if (state.title.trim().length < 5) "El título debe tener al menos 5 caracteres" else null
            val isValid = titleErr == null

            _uiState.update {
                it.copy(
                    titleError = titleErr,
                    isFormValid = isValid
                )
            }

            if (isValid) {
                _uiState.update { it.copy(currentStep = 2) }
            }
        } else if (state.currentStep == 2) {
            val descErr = if (state.description.trim().length < 20) "La descripción debe tener al menos 20 caracteres" else null
            val catErr = if (state.categoryId.isBlank()) "Selecciona una categoría" else null
            val subcatErr = if (state.subcategoryId.isBlank()) "Selecciona una subcategoría" else null
            val locErr = if (state.locationName.isBlank()) "La ubicación es obligatoria" else null

            val minVal = state.minPrice.toDoubleOrNull()
            val maxVal = state.maxPrice.toDoubleOrNull()
            val minErr = if (minVal == null || minVal <= 0) "Ingresa un precio mínimo válido" else null
            val maxErr = when {
                maxVal == null || maxVal <= 0 -> "Ingresa un precio máximo válido"
                minVal != null && maxVal < minVal -> "El máximo no puede ser menor al mínimo"
                else -> null
            }

            val isValid = descErr == null && catErr == null && subcatErr == null &&
                minErr == null && maxErr == null && locErr == null

            _uiState.update {
                it.copy(
                    descriptionError = descErr,
                    categoryError = catErr,
                    subcategoryError = subcatErr,
                    minPriceError = minErr,
                    maxPriceError = maxErr,
                    locationError = locErr,
                    isFormValid = isValid
                )
            }

            if (isValid) {
                _uiState.update { it.copy(currentStep = 3) }
            }
        }
    }

    fun onPreviousStep() {
        _uiState.update {
            if (it.currentStep > 1) it.copy(currentStep = it.currentStep - 1) else it
        }
    }

    fun onMessageShown() {
        _uiState.update { it.copy(userMessage = null) }
    }

    /**
     * Crear: la publicación nace PENDIENTE (toda publicación debe pasar por moderación) con el
     * usuario en sesión como autor, y se le suman los puntos de crear.
     * Editar: solo si es del autor y está PENDIENTE o RECHAZADA; al guardar vuelve a PENDIENTE,
     * se limpia el motivo de rechazo y queda otra vez en la cola del moderador.
     */
    fun onSavePublication() {
        if (!validateForm()) return

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            val state = _uiState.value
            val currentUser = getCurrentUserUseCase()
            val minVal = state.minPrice.toDoubleOrNull() ?: 80000.0
            val maxVal = state.maxPrice.toDoubleOrNull() ?: (minVal * 1.5)
            val avgPrice = (minVal + maxVal) / 2.0
            val priceRangeStr = formatPriceRange(minVal, maxVal, state.priceUnit)
            val images = state.images.ifEmpty { listOf(DEFAULT_IMAGE) }
            val editingId = state.publicationId

            val savedId: String
            if (editingId.isNullOrBlank()) {
                savedId = "pub_${System.currentTimeMillis()}"
                val publication = Publication(
                    id = savedId,
                    title = state.title.trim(),
                    description = state.description.trim(),
                    categoryId = state.categoryId,
                    category = state.categoryId,
                    subcategoryId = state.subcategoryId.ifBlank { null },
                    subcategory = state.subcategoryId,
                    price = avgPrice,
                    priceRange = priceRangeStr,
                    priceUnit = state.priceUnit,
                    imageUrls = images,
                    provider = currentUser,
                    authorId = currentUser.id,
                    status = PublicationStatus.PENDIENTE,
                    ratingAverage = 5.0,
                    reviewsCount = 1,
                    locationName = state.locationName,
                    city = state.locationName
                )
                createPublicationUseCase(publication)
                awardPointsUseCase(currentUser.id, PointEvent.PUBLICATION_CREATED)
            } else {
                savedId = editingId
                val existing = getPublicationByIdUseCase(editingId)
                val isOwner = existing != null &&
                    (existing.authorId.isBlank() || existing.authorId == currentUser.id)
                if (existing == null || !isOwner || !PublicationRules.canEdit(existing.status)) {
                    _uiState.update {
                        it.copy(isLoading = false, userMessage = "Esta publicación ya no se puede editar")
                    }
                    return@launch
                }
                updatePublicationUseCase(
                    existing.copy(
                        title = state.title.trim(),
                        description = state.description.trim(),
                        categoryId = state.categoryId,
                        category = state.categoryId,
                        subcategoryId = state.subcategoryId.ifBlank { null },
                        subcategory = state.subcategoryId,
                        price = avgPrice,
                        priceRange = priceRangeStr,
                        priceUnit = state.priceUnit,
                        imageUrls = images,
                        locationName = state.locationName,
                        city = state.locationName,
                        status = PublicationStatus.PENDIENTE,
                        rejectionReason = null
                    )
                )
            }

            _uiState.update {
                it.copy(isLoading = false, isSuccess = true, currentStep = 3, savedPublicationId = savedId)
            }
        }
    }
}

private const val DEFAULT_IMAGE = "https://images.unsplash.com/photo-1504307651254-35680f356dfd"

private fun formatPriceRange(min: Double, max: Double, unit: String): String {
    val minF = "$" + "%,d".format(min.toInt()).replace(',', '.')
    val maxF = "$" + "%,d".format(max.toInt()).replace(',', '.')
    return "$minF – $maxF $unit"
}

private fun initialMinPrice(publication: Publication): Int {
    if (publication.price > 0 && publication.priceRange.isBlank()) return publication.price.toInt()
    val parts = publication.priceRange.split("–", "-")
    if (parts.isNotEmpty()) {
        val minDigits = parts[0].replace(Regex("[^0-9]"), "").toIntOrNull()
        if (minDigits != null && minDigits > 0) return minDigits
    }
    return if (publication.price > 0) publication.price.toInt() else 80000
}

private fun initialMaxPrice(publication: Publication): Int {
    val parts = publication.priceRange.split("–", "-")
    if (parts.size > 1) {
        val maxDigits = parts[1].replace(Regex("[^0-9]"), "").toIntOrNull()
        if (maxDigits != null && maxDigits > 0) return maxDigits
    }
    val min = initialMinPrice(publication)
    return (min * 1.5).toInt().coerceAtLeast(120000)
}
