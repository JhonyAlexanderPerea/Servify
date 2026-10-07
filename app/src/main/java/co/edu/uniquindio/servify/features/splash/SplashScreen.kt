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
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Icon
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import co.edu.uniquindio.servify.ui.components.icons.ServifyIcons
import co.edu.uniquindio.servify.ui.theme.ServifyGold
import co.edu.uniquindio.servify.ui.theme.ServifyPrimary
import co.edu.uniquindio.servify.ui.theme.ServifyTextStyle
import co.edu.uniquindio.servify.ui.theme.cssLinearGradient
import kotlinx.coroutines.delay

private val SplashBackground = cssLinearGradient(
    145f,
    0f to Color(0xFF1A55E3),
    0.6f to Color(0xFF0D3EBD),
    1f to Color(0xFF07288F)
)

@Composable
fun SplashScreen(
    onFinished: () -> Unit
) {

    var visible by remember {
        mutableStateOf(false)
    }

    LaunchedEffect(Unit) {
        visible = true
        delay(2500)
        onFinished()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(SplashBackground)
    ) {

        AnimatedVisibility(
            visible = visible,
            enter = fadeIn(
                animationSpec = tween(
                    durationMillis = 800,
                    easing = EaseIn
                )
            ),
            modifier = Modifier.align(Alignment.Center)
        ) {

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {

                val logoShape = RoundedCornerShape(32.dp)

                Box(
                    modifier = Modifier
                        .size(112.dp)
                        .shadow(
                            elevation = 24.dp,
                            shape = logoShape,
                            clip = false,
                            ambientColor = Color.Black.copy(alpha = 0.25f),
                            spotColor = Color.Black.copy(alpha = 0.25f)
                        )
                        .clip(logoShape)
                        .background(Color.White.copy(alpha = 0.20f))
                        .border(
                            width = 1.dp,
                            color = Color.White.copy(alpha = 0.30f),
                            shape = logoShape
                        ),
                    contentAlignment = Alignment.Center
                ) {

                    Box {

                        Icon(
                            imageVector = ServifyIcons.ShieldWithHeart,
                            contentDescription = "Servify",
                            tint = Color.White,
                            modifier = Modifier.size(56.dp)
                        )

                        Box(
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .offset(x = 4.dp, y = 4.dp)
                                .size(20.dp)
                                .clip(CircleShape)
                                .background(ServifyGold),
                            contentAlignment = Alignment.Center
                        ) {

                            Icon(
                                imageVector = Icons.Filled.LocationOn,
                                contentDescription = null,
                                tint = ServifyPrimary,
                                modifier = Modifier.size(12.dp)
                            )
                        }
                    }
                }

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {

                    Text(
                        text = "Servify",
                        style = ServifyTextStyle.Display,
                        color = Color.White
                    )

                    Text(
                        text = "Servicios locales de confianza",
                        style = ServifyTextStyle.BodyLight,
                        color = Color.White.copy(alpha = 0.70f)
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
            color = Color.White.copy(alpha = 0.40f),
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
            .background(Color.White.copy(alpha = 0.50f))
    )
}
