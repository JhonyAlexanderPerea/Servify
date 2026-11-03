package co.edu.uniquindio.servify.features.statistics

data class PersonalStatisticsUiState(
    val completedServicesCount: Int = 0,
    val totalEarnings: Double = 0.0,
    val averageRating: Double = 0.0,
    val trustIndexScore: Int = 0,
    val monthlyActivity: List<Pair<String, Int>> = emptyList(),
    val isLoading: Boolean = false
)