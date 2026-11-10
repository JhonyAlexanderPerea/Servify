package co.edu.uniquindio.servify.features.profile.achievements

import co.edu.uniquindio.servify.domain.rules.BadgeProgress

/**Estado de la pantalla de Logros **/

data class AchievementsUiState(
    val isLoading: Boolean = true,
    val badges: List<BadgeProgress> = emptyList()
) {
    val unlocked: List<BadgeProgress> get() = badges.filter { it.unlocked }
    val locked: List<BadgeProgress> get() = badges.filterNot { it.unlocked }
    val totalCount: Int get() = badges.size

    /** Progreso global: insignias desbloqueadas / total. */
    val globalFraction: Float
        get() = if (badges.isEmpty()) 0f else unlocked.size.toFloat() / totalCount

    /** La insignia bloqueada más cercana a desbloquearse ("siguiente logro"). */
    val nextBadge: BadgeProgress? get() = locked.maxByOrNull { it.fraction }
}