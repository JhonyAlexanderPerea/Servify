package co.edu.uniquindio.servify.ui.motion

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.interaction.InteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.graphics.graphicsLayer
import androidx.navigation.NavBackStackEntry

/**
 * Lenguaje de movimiento de Servify: sutil, corto y con presencia.
 *  - Pantallas que se apilan: fundido + desplazamiento horizontal corto (1/10 del ancho).
 *  - Cambio entre pestañas: fundido con un leve escalado (sin deslizar, no hay "dirección").
 *  - Elementos de lista: reubicación animada; botones: escala 0.97 al pulsar.
 */
object ServifyMotion {
    const val SHORT = 150
    const val MEDIUM = 260
    const val LONG = 340

    /** Entra rápido y se asienta suave. */
    val Decelerate = CubicBezierEasing(0.2f, 0f, 0f, 1f)

    /** Sale con aceleración corta, para que no estorbe lo que entra. */
    val Accelerate = CubicBezierEasing(0.4f, 0f, 1f, 1f)

    private const val SLIDE_IN_FRACTION = 10
    private const val SLIDE_OUT_FRACTION = 12
}

// ---------------------------------------------------------------------------
// Transiciones de NavHost
// ---------------------------------------------------------------------------

fun AnimatedContentTransitionScope<NavBackStackEntry>.servifyEnter(): EnterTransition =
    fadeIn(tween(ServifyMotion.MEDIUM, delayMillis = 40, easing = ServifyMotion.Decelerate)) +
        slideInHorizontally(tween(ServifyMotion.LONG, easing = ServifyMotion.Decelerate)) { it / 10 }

fun AnimatedContentTransitionScope<NavBackStackEntry>.servifyExit(): ExitTransition =
    fadeOut(tween(ServifyMotion.SHORT, easing = ServifyMotion.Accelerate)) +
        slideOutHorizontally(tween(ServifyMotion.LONG, easing = ServifyMotion.Decelerate)) { -it / 12 }

fun AnimatedContentTransitionScope<NavBackStackEntry>.servifyPopEnter(): EnterTransition =
    fadeIn(tween(ServifyMotion.MEDIUM, delayMillis = 40, easing = ServifyMotion.Decelerate)) +
        slideInHorizontally(tween(ServifyMotion.LONG, easing = ServifyMotion.Decelerate)) { -it / 12 }

fun AnimatedContentTransitionScope<NavBackStackEntry>.servifyPopExit(): ExitTransition =
    fadeOut(tween(ServifyMotion.SHORT, easing = ServifyMotion.Accelerate)) +
        slideOutHorizontally(tween(ServifyMotion.LONG, easing = ServifyMotion.Decelerate)) { it / 10 }

/** Cambio entre pestañas del dashboard. */
fun tabEnter(): EnterTransition =
    fadeIn(tween(ServifyMotion.MEDIUM, delayMillis = 60, easing = ServifyMotion.Decelerate)) +
        scaleIn(tween(ServifyMotion.MEDIUM, delayMillis = 60, easing = ServifyMotion.Decelerate), initialScale = 0.98f)

fun tabExit(): ExitTransition =
    fadeOut(tween(120, easing = ServifyMotion.Accelerate))

fun isTabSwitch(initialRoute: String?, targetRoute: String?, tabRoutes: Set<String>): Boolean =
    initialRoute in tabRoutes && targetRoute in tabRoutes

// ---------------------------------------------------------------------------
// Micro-interacción de pulsación
// ---------------------------------------------------------------------------

/** Escala suave mientras el elemento está presionado. */
fun Modifier.pressScale(
    interactionSource: InteractionSource,
    pressedScale: Float = 0.97f
): Modifier = composed {
    val pressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (pressed) pressedScale else 1f,
        animationSpec = tween(ServifyMotion.SHORT, easing = ServifyMotion.Decelerate),
        label = "pressScale"
    )
    graphicsLayer {
        scaleX = scale
        scaleY = scale
    }
}
