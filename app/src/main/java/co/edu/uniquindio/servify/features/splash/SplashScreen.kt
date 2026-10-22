package co.edu.uniquindio.servify.features.splash

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.EaseIn
import androidx.compose.animation.core.StartOffset
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import co.edu.uniquindio.servify.ui.components.display.ServifyLogo
import co.edu.uniquindio.servify.ui.theme.ServifyTextStyle
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    onFinished: () -> Unit
) {

    var visible by remember {
        mutableStateOf(false)
    }

    LaunchedEffect(Unit) {
        visible = true
        delay(1800)
        onFinished()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {

        AnimatedVisibility(
            visible = visible,
            enter = fadeIn(
                animationSpec = tween(
                    durationMillis = 600,
                    easing = EaseIn
                )
            ),
            modifier = Modifier
                .align(Alignment.Center)
                // Respeta el alto de la barra de estado: el contenido no debe quedar debajo
                .statusBarsPadding()
        ) {

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {

                // Logo oficial en azul Servify, con la variante del modo actual
                ServifyLogo(
                    size = 200.dp
                )

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {

                    Text(
                        text = "Servify",
                        style = ServifyTextStyle.Display,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Text(
                        text = "Servicios locales de confianza",
                        style = ServifyTextStyle.BodyLight,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 64.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {

            repeat(3) { index ->

                PulsingDot(
                    delayMillis = index * 200
                )
            }
        }

        Text(
            text = "v1.0.0",
            style = ServifyTextStyle.Caption,
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.50f),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 32.dp)
        )
    }
}

@Composable
private fun PulsingDot(
    delayMillis: Int
) {

    val transition = rememberInfiniteTransition(label = "dot")

    val pulse by transition.animateFloat(
        initialValue = 1f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = keyframes {
                durationMillis = 2000
                1f at 0 using CubicBezierEasing(0.4f, 0f, 0.6f, 1f)
                0.5f at 1000 using CubicBezierEasing(0.4f, 0f, 0.6f, 1f)
                1f at 2000
            },
            initialStartOffset = StartOffset(delayMillis)
        ),
        label = "pulse"
    )

    Box(
        modifier = Modifier
            .size(8.dp)
            .graphicsLayer { alpha = pulse }
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.80f))
    )
}
