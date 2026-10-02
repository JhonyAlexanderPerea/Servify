package co.edu.uniquindio.servify.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

@Immutable
data class ColorPair(val container: Color, val content: Color)

@Immutable
data class StatusColors(
    val pending: ColorPair,
    val verified: ColorPair,
    val rejected: ColorPair,
    val completed: ColorPair,
)

@Immutable
data class LevelColors(
    val beginner: ColorPair,
    val professional: ColorPair,
    val expert: ColorPair,
    val master: ColorPair,
)

@Immutable
data class CategoryColors(
    val home: ColorPair,
    val education: ColorPair,
    val pets: ColorPair,
    val tech: ColorPair,
    val transport: ColorPair,
    val health: ColorPair,
)

@Immutable
data class OnboardingPageColors(val gradient: List<Color>, val accent: Color)

@Immutable
data class OnboardingColors(
    val page1: OnboardingPageColors,
    val page2: OnboardingPageColors,
    val page3: OnboardingPageColors,
)

@Immutable
data class ServifyColors(
    val status: StatusColors,
    val level: LevelColors,
    val category: CategoryColors,
    val onboarding: OnboardingColors,
    val warning: Color,
    val star: Color,
    val online: Color,
    val selectedTint: Color,
    val filledCardTint: Color,
    val disabledContent: Color,
    val moderatorHeader: Color,
    val splashGradient: List<Color>,
    val splashBadge: Color,
)

// ───────────── CLARO (del mockup) ─────────────
val LightServifyColors = ServifyColors(
    status = StatusColors(
        pending = ColorPair(Color(0xFFFEF3C7), Color(0xFF92400E)),
        verified = ColorPair(Color(0xFFDCFCE7), Color(0xFF166534)),
        rejected = ColorPair(Color(0xFFFEE2E2), Color(0xFF991B1B)),
        completed = ColorPair(Color(0xFFDBEAFE), Color(0xFF1E40AF)),
    ),
    level = LevelColors(
        beginner = ColorPair(Color(0xFFF5F5F5), Color(0xFF757575)),
        professional = ColorPair(Color(0xFFE3F2FD), Color(0xFF1A55E3)),
        expert = ColorPair(Color(0xFFEDE7F6), Color(0xFF7B2FBE)),
        master = ColorPair(Color(0xFFFFF8E1), Color(0xFFB45309)),
    ),
    category = CategoryColors(
        home = ColorPair(Color(0xFFD6E2FF), Color(0xFF1A55E3)),
        education = ColorPair(Color(0xFFEDE7F6), Color(0xFF7B2FBE)),
        pets = ColorPair(Color(0xFFFCE4EC), Color(0xFFEA4C89)),
        tech = ColorPair(Color(0xFFE0F7FA), Color(0xFF0097A7)),
        transport = ColorPair(Color(0xFFFFF3E0), Color(0xFFF57C00)),
        health = ColorPair(Color(0xFFE8F5E9), Color(0xFF388E3C)),
    ),
    onboarding = OnboardingColors(
        page1 = OnboardingPageColors(listOf(Color(0xFFE8F0FF), Color(0xFFC7D8FF)), Color(0xFF1A55E3)),
        page2 = OnboardingPageColors(listOf(Color(0xFFE8FFF8), Color(0xFFB3F0DC)), Color(0xFF006B53)),
        page3 = OnboardingPageColors(listOf(Color(0xFFFFF8E8), Color(0xFFFFE4B3)), Color(0xFFB45309)),
    ),
    warning = Color(0xFFB45309),
    star = Color(0xFFF59E0B),
    online = Color(0xFF22C55E),
    selectedTint = Color(0xFFEEF2FF),
    filledCardTint = Color(0xFFE8EEFF),
    disabledContent = Color(0xFF9E9EAF),
    moderatorHeader = Color(0xFF1B1B1F),
    splashGradient = listOf(Color(0xFF1A55E3), Color(0xFF0D3EBD), Color(0xFF07288F)),
    splashBadge = Color(0xFFFFD700),
)

// ───────────── OSCURO (derivado) ─────────────
val DarkServifyColors = ServifyColors(
    status = StatusColors(
        pending = ColorPair(Color(0xFF652A06), Color(0xFFFFDBCB)),
        verified = ColorPair(Color(0xFF00451F), Color(0xFFADF3B9)),
        rejected = ColorPair(Color(0xFF682621), Color(0xFFFFDAD6)),
        completed = ColorPair(Color(0xFF273774), Color(0xFFDDE1FF)),
    ),
    level = LevelColors(
        beginner = ColorPair(Color(0xFF353438), Color(0xFFC8C5CA)),
        professional = ColorPair(Color(0xFF253774), Color(0xFFDCE1FF)),
        expert = ColorPair(Color(0xFF4C2C6A), Color(0xFFF0DBFF)),
        master = ColorPair(Color(0xFF642B01), Color(0xFFFFDBCA)),
    ),
    category = CategoryColors(
        home = ColorPair(Color(0xFF253774), Color(0xFFB6C4FF)),
        education = ColorPair(Color(0xFF4C2C6A), Color(0xFFDEB7FF)),
        pets = ColorPair(Color(0xFF66243C), Color(0xFFFFB1C6)),
        tech = ColorPair(Color(0xFF00424A), Color(0xFF74D5E4)),
        transport = ColorPair(Color(0xFF612D00), Color(0xFFFFB786)),
        health = ColorPair(Color(0xFF0F4515), Color(0xFF9BD594)),
    ),
    onboarding = OnboardingColors(
        page1 = OnboardingPageColors(listOf(Color(0xFF011957), Color(0xFF182C68)), Color(0xFFB6C4FF)),
        page2 = OnboardingPageColors(listOf(Color(0xFF00251B), Color(0xFF00382A)), Color(0xFF82D7B9)),
        page3 = OnboardingPageColors(listOf(Color(0xFF391500), Color(0xFF532200)), Color(0xFFFFB68E)),
    ),
    warning = Color(0xFFFFB68E),
    star = Color(0xFFF59E0B),
    online = Color(0xFF22C55E),
    selectedTint = Color(0xFF232A48),
    filledCardTint = Color(0xFF262B43),
    disabledContent = Color(0xFF777680),
    moderatorHeader = Color(0xFF2A2A2D),
    splashGradient = listOf(Color(0xFF1A55E3), Color(0xFF0D3EBD), Color(0xFF07288F)), // igual: es la marca
    splashBadge = Color(0xFFFFD700),
)

val LocalServifyColors = staticCompositionLocalOf { LightServifyColors }

val MaterialTheme.servifyColors: ServifyColors
    @Composable
    @ReadOnlyComposable
    get() = LocalServifyColors.current