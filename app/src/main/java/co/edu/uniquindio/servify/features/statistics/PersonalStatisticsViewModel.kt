package co.edu.uniquindio.servify.features.statistics

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.edu.uniquindio.servify.domain.repository.ServiceRequestRepository
import co.edu.uniquindio.servify.domain.repository.UserRepository
import co.edu.uniquindio.servify.domain.usecase.trust.CalculateTrustIndexUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PersonalStatisticsViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val serviceRequestRepository: ServiceRequestRepository,
    private val calculateTrustIndexUseCase: CalculateTrustIndexUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(PersonalStatisticsUiState(isLoading = true))
    val uiState: StateFlow<PersonalStatisticsUiState> = _uiState.asStateFlow()

    init {
        loadStatistics()
    }

    private fun loadStatistics() {
        viewModelScope.launch {
            try {
                val currentUser = userRepository.observeCurrentUser().first()
                val trustDetails = calculateTrustIndexUseCase(currentUser.id)

                _uiState.update {
                    it.copy(
                        completedServicesCount = currentUser.completedJobsCount,
                        totalEarnings = currentUser.completedJobsCount * 150000.0,
                        averageRating = 4.8,
                        trustIndexScore = trustDetails.trustScore,
                        monthlyActivity = listOf(
                            "Mar" to 2,
                            "Abr" to 5,
                            "May" to 3,
                            "Jun" to 7,
                            "Jul" to 4,
                            "Ago" to 8
                        ),
                        isLoading = false
                    )
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }
}