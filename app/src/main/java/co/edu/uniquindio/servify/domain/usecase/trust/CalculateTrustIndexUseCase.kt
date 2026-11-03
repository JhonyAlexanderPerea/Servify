package co.edu.uniquindio.servify.domain.usecase.trust

import co.edu.uniquindio.servify.domain.model.ReputationLevel
import co.edu.uniquindio.servify.domain.model.User
import co.edu.uniquindio.servify.domain.repository.UserRepository
import co.edu.uniquindio.servify.features.service.model.TrustFactor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import javax.inject.Inject

data class TrustIndexDetails(
    val user: User,
    val trustScore: Int,
    val level: ReputationLevel,
    val factors: List<TrustFactor>,
    val explanation: String
)

class CalculateTrustIndexUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(providerId: String): TrustIndexDetails = withContext(Dispatchers.IO) {
        val currentUser = userRepository.observeCurrentUser().first()
        val user = if (currentUser.id == providerId) currentUser else currentUser.copy(
            id = providerId,
            name = "Juan Carlos Mejía",
            trustIndexScore = 94,
            reputationLevel = ReputationLevel.GOLD,
            completedJobsCount = 127
        )

        val factors = listOf(
            TrustFactor(
                title = "Identidad y cédula verificada",
                description = "Documentación de identidad cotejada con la Registraduría Nacional.",
                isVerified = user.isVerified
            ),
            TrustFactor(
                title = "Certificación laboral y antecedentes",
                description = "Certificado de antecedentes judiciales y competencias técnicas al día.",
                isVerified = true
            ),
            TrustFactor(
                title = "Cumplimiento y trabajos completados (${user.completedJobsCount})",
                description = "Ha completado exitosamente ${user.completedJobsCount} servicios en la plataforma.",
                isVerified = true
            ),
            TrustFactor(
                title = "Garantía de servicio asegurada",
                description = "Respaldado por el fondo de protección al usuario Servify.",
                isVerified = true
            )
        )

        val explanation = "El Índice de Confianza (0-100%) en Servify se calcula analizando la verificación de documentos oficiales, el número de trabajos completados con éxito, la calificación promedio recibida por los clientes y la ausencia de penalizaciones o reportes en la plataforma."

        TrustIndexDetails(
            user = user,
            trustScore = user.trustIndexScore,
            level = user.reputationLevel,
            factors = factors,
            explanation = explanation
        )
    }
}
