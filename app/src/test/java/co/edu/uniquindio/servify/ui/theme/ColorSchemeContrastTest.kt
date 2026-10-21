package co.edu.uniquindio.servify.ui.theme

import androidx.compose.ui.graphics.Color
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.math.max
import kotlin.math.min
import kotlin.math.pow

/**
 * Guarda de regresion del modo oscuro.
 *
 * El bug original era que la UI consumia constantes estaticas (ServifyPrimary,
 * ServifyOnSurface, ...) que no reaccionaban al tema: en oscuro el texto del
 * Home quedaba negro sobre fondo oscuro. Estos tests fijan los contrastes
 * minimos (WCAG 2.1) de cada rol del ColorScheme para que el problema no
 * vuelva.
 */
class ColorSchemeContrastTest {

    // --- WCAG 2.1 ---

    private fun linearize(channel: Double): Double {
        return if (channel <= 0.04045) {
            channel / 12.92
        } else {
            ((channel + 0.055) / 1.055).pow(2.4)
        }
    }

    /** Luminancia relativa WCAG. Se usan los canales sRGB (0..1) de Compose. */
    private fun luminance(color: Color): Double {
        return 0.2126 * linearize(color.red.toDouble()) +
            0.7152 * linearize(color.green.toDouble()) +
            0.0722 * linearize(color.blue.toDouble())
    }

    private fun contrastRatio(foreground: Color, background: Color): Double {
        val a = luminance(foreground)
        val b = luminance(background)
        return (max(a, b) + 0.05) / (min(a, b) + 0.05)
    }

    private fun assertContrast(
        message: String,
        foreground: Color,
        background: Color,
        minimum: Double
    ) {
        val ratio = contrastRatio(foreground, background)
        assertTrue(
            "$message -> ratio $ratio:1 (minimo $minimum:1)",
            ratio >= minimum
        )
    }

    // --- Pares que deben cumplir 4.5:1 (texto normal) ---

    private fun assertTextPairs(
        label: String,
        onSurface: Color,
        surface: Color,
        onSurfaceVariant: Color,
        surfaceVariant: Color,
        surfaceContainerHighest: Color,
        onPrimary: Color,
        primary: Color,
        onPrimaryContainer: Color,
        primaryContainer: Color,
        onSecondary: Color,
        secondary: Color,
        onSecondaryContainer: Color,
        secondaryContainer: Color,
        onTertiary: Color,
        tertiary: Color,
        onTertiaryContainer: Color,
        tertiaryContainer: Color,
        onError: Color,
        error: Color,
        onErrorContainer: Color,
        errorContainer: Color
    ) {
        assertContrast("$label onSurface/surface", onSurface, surface, 4.5)
        assertContrast("$label onSurfaceVariant/surface", onSurfaceVariant, surface, 4.5)
        assertContrast("$label onSurfaceVariant/surfaceVariant", onSurfaceVariant, surfaceVariant, 4.5)
        assertContrast("$label onSurfaceVariant/surfaceContainerHighest", onSurfaceVariant, surfaceContainerHighest, 4.5)
        assertContrast("$label onPrimary/primary", onPrimary, primary, 4.5)
        assertContrast("$label onPrimaryContainer/primaryContainer", onPrimaryContainer, primaryContainer, 4.5)
        assertContrast("$label onSecondary/secondary", onSecondary, secondary, 4.5)
        assertContrast("$label onSecondaryContainer/secondaryContainer", onSecondaryContainer, secondaryContainer, 4.5)
        assertContrast("$label onTertiary/tertiary", onTertiary, tertiary, 4.5)
        assertContrast("$label onTertiaryContainer/tertiaryContainer", onTertiaryContainer, tertiaryContainer, 4.5)
        assertContrast("$label onError/error", onError, error, 4.5)
        assertContrast("$label onErrorContainer/errorContainer", onErrorContainer, errorContainer, 4.5)
    }

    @Test
    fun `el esquema claro cumple contraste de texto`() {
        val s = LightColorScheme
        assertTextPairs(
            "CLARO",
            s.onSurface, s.surface,
            s.onSurfaceVariant, s.surfaceVariant, s.surfaceContainerHighest,
            s.onPrimary, s.primary,
            s.onPrimaryContainer, s.primaryContainer,
            s.onSecondary, s.secondary,
            s.onSecondaryContainer, s.secondaryContainer,
            s.onTertiary, s.tertiary,
            s.onTertiaryContainer, s.tertiaryContainer,
            s.onError, s.error,
            s.onErrorContainer, s.errorContainer
        )
    }

    @Test
    fun `el esquema oscuro cumple contraste de texto`() {
        val s = DarkColorScheme
        assertTextPairs(
            "OSCURO",
            s.onSurface, s.surface,
            s.onSurfaceVariant, s.surfaceVariant, s.surfaceContainerHighest,
            s.onPrimary, s.primary,
            s.onPrimaryContainer, s.primaryContainer,
            s.onSecondary, s.secondary,
            s.onSecondaryContainer, s.secondaryContainer,
            s.onTertiary, s.tertiary,
            s.onTertiaryContainer, s.tertiaryContainer,
            s.onError, s.error,
            s.onErrorContainer, s.errorContainer
        )
    }

    // --- Bordes: 3:1 (elementos no textuales) ---

    @Test
    fun `los bordes se distinguen de la superficie en ambos temas`() {
        assertContrast("CLARO outline/surface", LightColorScheme.outline, LightColorScheme.surface, 3.0)
        assertContrast("CLARO outlineVariant/surface", LightColorScheme.outlineVariant, LightColorScheme.surface, 3.0)
        assertContrast("OSCURO outline/surface", DarkColorScheme.outline, DarkColorScheme.surface, 3.0)
        assertContrast("OSCURO outlineVariant/surface", DarkColorScheme.outlineVariant, DarkColorScheme.surface, 3.0)
    }

    // --- Regresiones concretas del bug original ---

    @Test
    fun `el modo oscuro no usa los tokens del modo claro`() {
        assertTrue(
            "onSurface del modo oscuro debe ser claro",
            luminance(DarkColorScheme.onSurface) > 0.5
        )
        assertTrue(
            "el fondo oscuro debe ser mas oscuro que el claro",
            luminance(DarkColorScheme.background) < luminance(LightColorScheme.background)
        )
        assertTrue(
            "la superficie oscura debe ser mas oscura que la clara",
            luminance(DarkColorScheme.surface) < luminance(LightColorScheme.surface)
        )
        assertTrue(
            "el primario oscuro debe ser mas claro que el claro (marca invertida)",
            luminance(DarkColorScheme.primary) > luminance(LightColorScheme.primary)
        )
    }

    @Test
    fun `los colores extra son legibles sobre la superficie de cada tema`() {
        val light = LightServifyExtraColors
        val dark = DarkServifyExtraColors

        assertContrast("CLARO onHero/heroGradientStart", light.onHero, light.heroGradientStart, 4.5)
        assertContrast("CLARO onHero/heroGradientEnd", light.onHero, light.heroGradientEnd, 4.5)
        assertContrast("CLARO badge maestro", light.onLevelMaestroContainer, light.levelMaestroContainer, 4.5)
        assertContrast("CLARO badge experto", light.onLevelExpertoContainer, light.levelExpertoContainer, 4.5)

        assertContrast("OSCURO onHero/heroGradientStart", dark.onHero, dark.heroGradientStart, 4.5)
        assertContrast("OSCURO onHero/heroGradientEnd", dark.onHero, dark.heroGradientEnd, 4.5)
        assertContrast("OSCURO badge maestro", dark.onLevelMaestroContainer, dark.levelMaestroContainer, 4.5)
        assertContrast("OSCURO badge experto", dark.onLevelExpertoContainer, dark.levelExpertoContainer, 4.5)
    }

    @Test
    fun `el color de contenido sobre acentos libres siempre es legible`() {
        // Acentos del mock de categorias/proveedores: no pertenecen al ColorScheme,
        // por eso el color de contenido se elige por contraste real.
        val accents = listOf(
            Color(0xFF1A55E3), Color(0xFF7B2FBE), Color(0xFFEA4C89),
            Color(0xFF0097A7), Color(0xFFF57C00), Color(0xFF388E3C),
            Color(0xFF00C853), Color(0xFFD81B60)
        )
        accents.forEach { accent ->
            val content = contentColorOn(accent)
            assertContrast("acento ${accent.value.toString(16)}", content, accent, 4.5)
        }
    }

    @Test
    fun `el aviso es legible sobre su propio contenedor tintado`() {
        // El badge de logros usa warning.copy(alpha = 0.12f) como contenedor y
        // warning como color del texto: ambos deben quedar legibles.
        listOf(
            "CLARO" to (LightServifyExtraColors.warning to LightColorScheme.surface),
            "OSCURO" to (DarkServifyExtraColors.warning to DarkColorScheme.surface)
        ).forEach { (label, pair) ->
            val (warning, surface) = pair
            val container = Color(
                red = warning.red * 0.12f + surface.red * 0.88f,
                green = warning.green * 0.12f + surface.green * 0.88f,
                blue = warning.blue * 0.12f + surface.blue * 0.88f,
                alpha = 1f
            )
            assertContrast("$label warning/contenedor", warning, container, 4.5)
            assertContrast("$label warning/surface", warning, surface, 4.5)
        }
    }
}
