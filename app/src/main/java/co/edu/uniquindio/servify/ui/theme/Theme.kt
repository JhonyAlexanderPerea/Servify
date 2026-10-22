package co.edu.uniquindio.servify.ui.theme

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

/**
 * Esquema CLARO.
 * Los contenedores se derivan de la paleta de marca y mantienen el mismo
 * contraste que el diseño original (azul #1A55E3 + fondos casi blancos).
 */
internal val LightColorScheme = lightColorScheme(
    primary = ServifyBlue,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFD6E2FF),
    onPrimaryContainer = Color(0xFF001B3D),
    inversePrimary = Color(0xFFB4C5FF),

    secondary = ServifyGreen,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFDCFCE7),
    onSecondaryContainer = Color(0xFF00281A),

    tertiary = ServifyPurple,
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFFEDE7F6),
    onTertiaryContainer = Color(0xFF3B1E6B),

    background = Color(0xFFFEFBFF),
    onBackground = Color(0xFF1B1B1F),
    surface = Color.White,
    onSurface = Color(0xFF1B1B1F),
    surfaceVariant = Color(0xFFE3E1EC),
    onSurfaceVariant = Color(0xFF45464F),
    surfaceContainerLowest = Color.White,
    surfaceContainerLow = Color(0xFFF7F2FA),
    surfaceContainer = Color(0xFFF3EDF7),
    surfaceContainerHigh = Color(0xFFECE6F0),
    surfaceContainerHighest = Color(0xFFE6E0EA),
    surfaceTint = ServifyBlue,

    inverseSurface = Color(0xFF303036),
    inverseOnSurface = Color(0xFFF3EFF4),

    outline = Color(0xFF787680),
    outlineVariant = Color(0xFF94909E),
    scrim = Color(0xFF000000),

    error = Color(0xFFBA1A1A),
    onError = Color.White,
    errorContainer = Color(0xFFFFDAD6),
    onErrorContainer = Color(0xFF410002)
)

/**
 * Esquema OSCURO.
 * Cada rol tiene su propia contraparte legible sobre superficie oscura:
 * el azul de marca pasa a un azul claro (#B4C5FF) y los textos dejan de ser
 * negros puros, que era el problema principal del modo oscuro anterior.
 */
internal val DarkColorScheme = darkColorScheme(
    primary = Color(0xFFB4C5FF),
    onPrimary = Color(0xFF002A78),
    primaryContainer = Color(0xFF003FA7),
    onPrimaryContainer = Color(0xFFD6E2FF),
    inversePrimary = ServifyBlue,

    secondary = Color(0xFF53DBC5),
    onSecondary = Color(0xFF00382B),
    secondaryContainer = Color(0xFF005139),
    onSecondaryContainer = Color(0xFF6DF7C4),

    tertiary = Color(0xFFD3BBFF),
    onTertiary = Color(0xFF3B1E6B),
    tertiaryContainer = Color(0xFF4A3780),
    onTertiaryContainer = Color(0xFFEADDFF),

    background = Color(0xFF111318),
    onBackground = Color(0xFFE2E2EC),
    surface = Color(0xFF191C22),
    onSurface = Color(0xFFE2E2EC),
    surfaceVariant = Color(0xFF434750),
    onSurfaceVariant = Color(0xFFC4C6D0),
    surfaceContainerLowest = Color(0xFF0C0E13),
    surfaceContainerLow = Color(0xFF191C22),
    surfaceContainer = Color(0xFF1D2027),
    surfaceContainerHigh = Color(0xFF282A31),
    surfaceContainerHighest = Color(0xFF33353C),
    surfaceTint = Color(0xFFB4C5FF),

    inverseSurface = Color(0xFFE2E2EC),
    inverseOnSurface = Color(0xFF111318),

    outline = Color(0xFF8E9099),
    outlineVariant = Color(0xFF6E7078),
    scrim = Color(0xFF000000),

    error = Color(0xFFFFB4AB),
    onError = Color(0xFF690005),
    errorContainer = Color(0xFF93000A),
    onErrorContainer = Color(0xFFFFDAD6)
)

@Composable
fun ServifyTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    val extraColors = if (darkTheme) DarkServifyExtraColors else LightServifyExtraColors

    ApplySystemBarAppearance(darkTheme = darkTheme)

    CompositionLocalProvider(LocalServifyExtraColors provides extraColors) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            content = content
        )
    }
}

/**
 * La apariencia de los iconos de las barras del sistema la decide el TEMA de la app,
 * no el tema de la ventana: asi el modo claro muestra iconos oscuros y el modo oscuro
 * iconos claros, sin depender de cuando se ejecute [MainActivity.enableEdgeToEdge].
 *
 * Sin esto, el splash de plataforma (API 31+) restauraba al salir el valor del tema XML
 * y dejaba los iconos blancos sobre el fondo claro (iconos invisibles) al arrancar en frio.
 */
@Composable
private fun ApplySystemBarAppearance(darkTheme: Boolean) {
    val view = LocalView.current
    if (view.isInEditMode) return

    SideEffect {
        val window = view.context.findActivity()?.window ?: return@SideEffect
        WindowCompat.getInsetsController(window, view).apply {
            isAppearanceLightStatusBars = !darkTheme
            isAppearanceLightNavigationBars = !darkTheme
        }
    }
}

/** Desenvuelve el [ContextWrapper] hasta llegar a la [Activity] que hospeda la composable. */
private fun Context.findActivity(): Activity? {
    var context: Context = this
    while (context is ContextWrapper) {
        if (context is Activity) return context
        context = context.baseContext
    }
    return context as? Activity
}
