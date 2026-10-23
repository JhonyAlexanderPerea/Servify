package co.edu.uniquindio.servify.features.publication.create

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.edu.uniquindio.servify.domain.model.Publication
import co.edu.uniquindio.servify.domain.model.PublicationStatus
import co.edu.uniquindio.servify.domain.usecase.publication.CreatePublicationUseCase
import co.edu.uniquindio.servify.domain.usecase.publication.GetPublicationByIdUseCase
import co.edu.uniquindio.servify.domain.usecase.publication.UpdatePublicationUseCase
import co.edu.uniquindio.servify.domain.usecase.user.GetCurrentUserUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
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
    val price: String = "",
    val priceError: String? = null,
    val priceUnit: String = "por trabajo",
    val locationName: String = "Armenia, Quindío",
    val locationError: String? = null,
    val images: List<String> = emptyList(),
    val currentStep: Int = 1,
    val isFormValid: Boolean = false,
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val isAiAnalyzing: Boolean = false,
    val showAiSuggestion: Boolean = false,
    val aiSuggestedTitle: String = "",
    val aiSuggestedDescription: String = "",
    val aiSuggestedCategory: String = "",
    val aiSuggestedPrice: String = "",
    val userMessage: String? = null
)

@HiltViewModel
class PublicationFlowViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getPublicationByIdUseCase: GetPublicationByIdUseCase,
    private val createPublicationUseCase: CreatePublicationUseCase,
    private val updatePublicationUseCase: UpdatePublicationUseCase,
    private val getCurrentUserUseCase: GetCurrentUserUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(PublicationFormUiState())
    val uiState: StateFlow<PublicationFormUiState> = _uiState.asStateFlow()

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
                        categoryId = existing.categoryId,
                        subcategoryId = existing.subcategoryId ?: "",
                        price = existing.price.toInt().toString(),
                        priceUnit = existing.priceUnit,
                        locationName = existing.locationName,
                        images = existing.images,
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
    }

    fun onDescriptionChanged(newDescription: String) {
        _uiState.update {
            it.copy(
                description = newDescription,
                descriptionError = if (newDescription.length < 20) "La descripción debe tener al menos 20 caracteres" else null
            )
        }
    }

    fun onCategoryChanged(category: String) {
        _uiState.update {
            it.copy(
                categoryId = category,
                categoryError = if (category.isBlank()) "Selecciona una categoría" else null,
                subcategoryId = ""
            )
        }
    }

    fun onSubcategoryChanged(subcategory: String) {
        _uiState.update { it.copy(subcategoryId = subcategory) }
    }

    fun onPriceChanged(priceStr: String) {
        val parsed = priceStr.toDoubleOrNull()
        val error = when {
            priceStr.isBlank() -> "Ingresa un precio"
            parsed == null || parsed <= 0 -> "Ingresa un valor numérico válido"
            else -> null
        }
        _uiState.update {
            it.copy(
                price = priceStr,
                priceError = error
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

    fun onApplyAiSuggestion(title: String, desc: String, category: String, price: String) {
        _uiState.update {
            it.copy(
                title = title,
                description = desc,
                categoryId = category,
                price = price.replace(Regex("[^0-9]"), "").ifBlank { "80000" },
                titleError = null,
                descriptionError = null,
                categoryError = null,
                priceError = null
            )
        }
    }

    fun validateForm(): Boolean {
        val state = _uiState.value
        val titleErr = if (state.title.trim().length < 5) "El título debe tener al menos 5 caracteres" else null
        val descErr = if (state.description.trim().length < 20) "La descripción debe tener al menos 20 caracteres" else null
        val catErr = if (state.categoryId.isBlank()) "Selecciona una categoría" else null
        val locErr = if (state.locationName.isBlank()) "La ubicación es obligatoria" else null
        val priceVal = state.price.toDoubleOrNull()
        val priceErr = if (priceVal == null || priceVal <= 0) "Ingresa un precio válido" else null

        val isValid = titleErr == null && descErr == null && catErr == null && locErr == null && priceErr == null

        _uiState.update {
            it.copy(
                titleError = titleErr,
                descriptionError = descErr,
                categoryError = catErr,
                locationError = locErr,
                priceError = priceErr,
                isFormValid = isValid
            )
        }

        return isValid
    }

    fun onNextStep() {
        val state = _uiState.value
        if (state.currentStep == 1) {
            if (validateForm()) {
                _uiState.update { it.copy(currentStep = 2) }
            }
        } else if (state.currentStep == 2) {
            if (validateForm()) {
                _uiState.update { it.copy(currentStep = 3) }
            }
        }
    }

    fun onPreviousStep() {
        _uiState.update {
            if (it.currentStep > 1) it.copy(currentStep = it.currentStep - 1) else it
        }
    }

    fun onSavePublication() {
        if (!validateForm()) return

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            val state = _uiState.value
            val currentUser = getCurrentUserUseCase()

            val isEditing = !state.publicationId.isNullOrBlank()
            val pubId = state.publicationId ?: "pub_${System.currentTimeMillis()}"

            val publication = Publication(
                id = pubId,
                title = state.title.trim(),
                description = state.description.trim(),
                categoryId = state.categoryId,
                subcategoryId = state.subcategoryId.ifBlank { null },
                price = state.price.toDoubleOrNull() ?: 80000.0,
                priceUnit = state.priceUnit,
                images = if (state.images.isNotEmpty()) state.images else listOf("https://images.unsplash.com/photo-1504307651254-35680f356dfd"),
                provider = currentUser,
                status = PublicationStatus.APPROVED,
                ratingAverage = 5.0,
                reviewsCount = 1,
                locationName = state.locationName
            )

            if (isEditing) {
                updatePublicationUseCase(publication)
            } else {
                createPublicationUseCase(publication)
            }

            _uiState.update {
                it.copy(
                    isLoading = false,
                    isSuccess = true,
                    currentStep = 3
                )
            }
        }
    }
}
