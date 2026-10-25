package co.edu.uniquindio.servify.features.profile

import co.edu.uniquindio.servify.domain.model.Badge
import co.edu.uniquindio.servify.domain.model.Publication
import co.edu.uniquindio.servify.domain.model.PublicationStatus
import co.edu.uniquindio.servify.domain.service.UserProgress

data class ProfileUiState(
    val isLoading: Boolean = true,
    val fullName: String = "",
    val initials: String = "",
    val city: String = "",
    val levelLabel: String = "",
    val points: Int = 0,
    /** Siguiente nivel (null si ya es Maestro). */
    val nextLevelLabel: String? = null,
    val pointsToNext: Int = 0,
    /** Avance dentro del nivel actual, de 0f a 1f. */
    val levelFraction: Float = 0f,
    /** Publicaciones que ya pasaron la verificación de un moderador (visibles o finalizadas). */
    val publishedCount: Int = 0,
    val finalizedCount: Int = 0,
    val unlockedBadges: List<Badge> = emptyList(),
    val totalBadges: Int = Badge.entries.size
)

/** Convierte el progreso del dominio y las publicaciones del usuario en estado de pantalla. */
internal fun toProfileUiState(progress: UserProgress, publications: List<Publication>): ProfileUiState =
    ProfileUiState(
        isLoading = false,
        fullName = progress.user.fullName,
        initials = progress.user.initials,
        city = progress.user.city,
        levelLabel = progress.level.level.label,
        points = progress.level.points,
        nextLevelLabel = progress.level.next?.label,
        pointsToNext = progress.level.pointsToNext,
        levelFraction = progress.level.fraction,
        publishedCount = publications.count { it.wasVerified },
        finalizedCount = publications.count { it.status == PublicationStatus.FINALIZADA },
        unlockedBadges = progress.badges.filter { it.unlocked }.map { it.badge },
        totalBadges = progress.totalCount
    )
