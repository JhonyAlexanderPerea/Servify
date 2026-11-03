package co.edu.uniquindio.servify.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import kotlin.math.max
import kotlin.math.min
import kotlin.math.pow

// -----------------------------------------------------------------------------
// PALETA DE MARCA
//
// Solo vive aquí el color que NO depende del tema (o sea, el que se mantiene
// igual en claro y oscuro). Todo lo que representa un ROL semantico
// (superficie, texto, primario, error...) vive en el ColorScheme de
// Theme.kt y se consume con MaterialTheme.colorScheme.*
// -----------------------------------------------------------------------------

/** Azul Servify: color de marca de la app. */
val ServifyBlue = Color(0xFF1A55E3)

/** Verde Servify: confianza, verificado, éxito. */
val ServifyGreen = Color(0xFF006B53)

/** Morado Servify: nivel Experto. */
val ServifyPurple = Color(0xFF7B2FBE)

/** Verde de presencia (punto "en línea"). */
val ServifyOnline = Color(0xFF22C55E)

// -----------------------------------------------------------------------------
// COLORES EXTRA
//
// Material 3 no modela algunos roles que el producto usa (el dorado de las
// estrellas, el rosa de "Me interesa" y los dos contenedores de nivel).
// Se exponen por CompositionLocal para que cambien con el tema y no queden
// "congelados" en el color de marca claro.
// -----------------------------------------------------------------------------

@Immutable
data class ServifyExtraColors(
    /** True cuando el tema activo es el oscuro. */
    val isDark: Boolean,
    /** Dorado de estrellas y valoraciones. */
    val gold: Color,
    val onGold: Color,
    /** Rosa del corazón de "Me interesa". */
    val favorite: Color,
    /** Ambar de avisos (iconos de recordatorio). */
    val warning: Color,
    /** Degradado del banner hero (imagenes de servicio). */
    val heroGradientStart: Color,
    val heroGradientEnd: Color,
    /** Color del contenido sobre el banner hero. */
    val onHero: Color,
    /** Contenedor del nivel Maestro. */
    val levelMaestroContainer: Color,
    val onLevelMaestroContainer: Color,
    /** Contenedor del nivel Experto. */
    val levelExpertoContainer: Color,
    val onLevelExpertoContainer: Color
)

internal val LightServifyExtraColors = ServifyExtraColors(
    isDark = false,
    gold = Color(0xFFBC8100),
    onGold = Color(0xFF3B2A00),
    favorite = Color(0xFFD6276B),
    warning = Color(0xFFA04807),
    heroGradientStart = Color(0xFF3A63DD),
    heroGradientEnd = Color(0xFF1A55E3),
    onHero = Color.White,
    levelMaestroContainer = Color(0xFFFFF8E8),
    onLevelMaestroContainer = Color(0xFFB45309),
    levelExpertoContainer = Color(0xFFEDE7F6),
    onLevelExpertoContainer = Color(0xFF7B2FBE)
)

internal val DarkServifyExtraColors = ServifyExtraColors(
    isDark = true,
    gold = Color(0xFFFFD700),
    onGold = Color(0xFF3B2A00),
    favorite = Color(0xFFFF8FB1),
    warning = Color(0xFFFFB86B),
    heroGradientStart = Color(0xFF2F4CA0),
    heroGradientEnd = Color(0xFF131E45),
    onHero = Color(0xFFDCE4FF),
    levelMaestroContainer = Color(0xFF5A3F00),
    onLevelMaestroContainer = Color(0xFFFFDEA6),
    levelExpertoContainer = Color(0xFF4A3780),
    onLevelExpertoContainer = Color(0xFFEADDFF)
)

val LocalServifyExtraColors = staticCompositionLocalOf { LightServifyExtraColors }

// -----------------------------------------------------------------------------
// COLOR DE CONTENIDO SOBRE ACENTOS ARBITRARIOS
//
// Los acentos de categorias y proveedores (mock de Home/Explore/Detalle) no
// pertenecen al ColorScheme: son colores libres. El texto que se dibuja
// encima (iniciales del avatar, tint del icono) necesita un color de
// contenido legible, asi que se elige entre tinta oscura y blanco segun el
// mayor contraste real (WCAG 2.1).
// -----------------------------------------------------------------------------

private fun relativeLuminance(color: Color): Double {
    fun linearize(channel: Double): Double =
        if (channel <= 0.04045) channel / 12.92 else ((channel + 0.055) / 1.055).pow(2.4)

    return 0.2126 * linearize(color.red.toDouble()) +
        0.7152 * linearize(color.green.toDouble()) +
        0.0722 * linearize(color.blue.toDouble())
}

private fun contrastRatio(a: Color, b: Color): Double {
    val la = relativeLuminance(a)
    val lb = relativeLuminance(b)
    val high = max(la, lb)
    val low = min(la, lb)
    return (high + 0.05) / (low + 0.05)
}

/**
 * Devuelve el color de contenido mas legible sobre [accent]: blanco o tinta
 * oscura, eligiendo el de mayor contraste. Garantiza que las iniciales del
 * avatar y los tintes de icono no queden invisibles.
 */
fun contentColorOn(accent: Color): Color {
    val ink = Color(0xFF10131A)
    return if (contrastRatio(accent, ink) >= contrastRatio(accent, Color.White)) ink else Color.White
}

/**
 * Acceso theme-aware a los colores que Material 3 no cubre.
 * Se leen dentro de composables: `ServifyExtra.gold`.
 */
object ServifyExtra {

    /** Indica si el tema activo es el oscuro. */
    val isDark: Boolean @Composable get() = LocalServifyExtraColors.current.isDark
    val gold: Color @Composable get() = LocalServifyExtraColors.current.gold
    val onGold: Color @Composable get() = LocalServifyExtraColors.current.onGold
    val favorite: Color @Composable get() = LocalServifyExtraColors.current.favorite
    val warning: Color @Composable get() = LocalServifyExtraColors.current.warning
    val heroGradientStart: Color @Composable get() = LocalServifyExtraColors.current.heroGradientStart
    val heroGradientEnd: Color @Composable get() = LocalServifyExtraColors.current.heroGradientEnd
    val onHero: Color @Composable get() = LocalServifyExtraColors.current.onHero
    val levelMaestroContainer: Color @Composable get() = LocalServifyExtraColors.current.levelMaestroContainer
    val onLevelMaestroContainer: Color @Composable get() = LocalServifyExtraColors.current.onLevelMaestroContainer
    val levelExpertoContainer: Color @Composable get() = LocalServifyExtraColors.current.levelExpertoContainer
    val onLevelExpertoContainer: Color @Composable get() = LocalServifyExtraColors.current.onLevelExpertoContainer
}

// Top-level aliases for MaterialTheme colorScheme
val ServifyPrimary: Color @Composable get() = MaterialTheme.colorScheme.primary
val ServifyOnPrimary: Color @Composable get() = MaterialTheme.colorScheme.onPrimary
val ServifyPrimaryContainer: Color @Composable get() = MaterialTheme.colorScheme.primaryContainer
val ServifyOnPrimaryContainer: Color @Composable get() = MaterialTheme.colorScheme.onPrimaryContainer
val ServifySecondary: Color @Composable get() = MaterialTheme.colorScheme.secondary
val ServifyOnSecondary: Color @Composable get() = MaterialTheme.colorScheme.onSecondary
val ServifySecondaryContainer: Color @Composable get() = MaterialTheme.colorScheme.secondaryContainer
val ServifyOnSecondaryContainer: Color @Composable get() = MaterialTheme.colorScheme.onSecondaryContainer
val ServifyTertiary: Color @Composable get() = MaterialTheme.colorScheme.tertiary
val ServifyOnTertiary: Color @Composable get() = MaterialTheme.colorScheme.onTertiary
val ServifyTertiaryContainer: Color @Composable get() = MaterialTheme.colorScheme.tertiaryContainer
val ServifyOnTertiaryContainer: Color @Composable get() = MaterialTheme.colorScheme.onTertiaryContainer
val ServifyBackground: Color @Composable get() = MaterialTheme.colorScheme.background
val ServifyOnBackground: Color @Composable get() = MaterialTheme.colorScheme.onBackground
val ServifySurface: Color @Composable get() = MaterialTheme.colorScheme.surface
val ServifyOnSurface: Color @Composable get() = MaterialTheme.colorScheme.onSurface
val ServifySurfaceVariant: Color @Composable get() = MaterialTheme.colorScheme.surfaceVariant
val ServifyOnSurfaceVariant: Color @Composable get() = MaterialTheme.colorScheme.onSurfaceVariant
val ServifyOutline: Color @Composable get() = MaterialTheme.colorScheme.outline
val ServifyOutlineVariant: Color @Composable get() = MaterialTheme.colorScheme.outlineVariant

