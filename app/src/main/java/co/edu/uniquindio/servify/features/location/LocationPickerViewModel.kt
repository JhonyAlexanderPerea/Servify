package co.edu.uniquindio.servify.features.location

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.edu.uniquindio.servify.domain.model.PickedLocation
import co.edu.uniquindio.servify.domain.usecase.location.ReverseGeocodeUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class LocationPickerUiState(
    val latitude: Double = 4.53389,
    val longitude: Double = -75.68111,
    val address: String = "",
    val isLoadingAddress: Boolean = false
)

@HiltViewModel
class LocationPickerViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val reverseGeocodeUseCase: ReverseGeocodeUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(LocationPickerUiState())
    val uiState: StateFlow<LocationPickerUiState> = _uiState.asStateFlow()

    private var geocodeJob: Job? = null

    init {
        val initialLatStr = savedStateHandle.get<String>("initialLat")
        val initialLngStr = savedStateHandle.get<String>("initialLng")
        val initialLat = initialLatStr?.toDoubleOrNull() ?: savedStateHandle.get<Double>("initialLat") ?: 4.53389
        val initialLng = initialLngStr?.toDoubleOrNull() ?: savedStateHandle.get<Double>("initialLng") ?: -75.68111

        if (initialLat != 0.0 && initialLng != 0.0) {
            onCameraMoved(initialLat, initialLng)
        }
    }

    fun onCameraMoved(latitude: Double, longitude: Double) {
        _uiState.update {
            it.copy(
                latitude = latitude,
                longitude = longitude,
                isLoadingAddress = true
            )
        }

        geocodeJob?.cancel()
        geocodeJob = viewModelScope.launch {
            delay(500)
            val resolvedAddress = reverseGeocodeUseCase(latitude, longitude)
            _uiState.update {
                it.copy(
                    address = resolvedAddress,
                    isLoadingAddress = false
                )
            }
        }
    }

    fun getPickedLocation(): PickedLocation {
        val state = _uiState.value
        return PickedLocation(
            latitude = state.latitude,
            longitude = state.longitude,
            address = state.address.ifBlank { "${state.latitude}, ${state.longitude}" }
        )
    }
}
