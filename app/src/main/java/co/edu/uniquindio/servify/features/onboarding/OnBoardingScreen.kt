package co.edu.uniquindio.servify.features.onboarding

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowForward
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material.icons.outlined.RocketLaunch
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import co.edu.uniquindio.servify.ui.components.button.ServifyFilledButton
import co.edu.uniquindio.servify.ui.components.button.ServifyOutlinedButton
import co.edu.uniquindio.servify.ui.components.button.ServifyTextButton
import co.edu.uniquindio.servify.ui.theme.ServifyOnSurface
import co.edu.uniquindio.servify.ui.theme.ServifyOnSurfaceVariant
import co.edu.uniquindio.servify.ui.theme.ServifyOutlineVariant
import co.edu.uniquindio.servify.ui.theme.ServifyPrimary
import co.edu.uniquindio.servify.ui.theme.ServifyTextStyle
import co.edu.uniquindio.servify.ui.theme.cssLinearGradient

private data class OnboardingPage(
    val title: String,
    val description: String,
    val color: Color,
    val background: Brush,
    val icon: ImageVector
)

private val pages = listOf(

    OnboardingPage(
        title = "Encuentra servicios cerca de ti",
        description =
            "Descubre proveedores de confianza en tu ciudad y barrio. Plomeros, electricistas, tutores y más, a unos pasos de tu hogar.",
        color = Color(0xFF1A55E3),
        background = cssLinearGradient(
            160f,
            0f to Color(0xFFE8F0FF),
            1f to Color(0xFFC7D8FF)
        ),
        icon = Icons.Filled.LocationOn
    ),

    OnboardingPage(
        title = "Contrata proveedores confiables",
        description =
            "Todos los proveedores están verificados. Consulta su índice de confianza, calificaciones y reseñas de otros usuarios.",
        color = Color(0xFF006B53),
        background = cssLinearGradient(
            160f,
            0f to Color(0xFFE8FFF8),
            1f to Color(0xFFB3F0DC)
        ),
        icon = Icons.Filled.VerifiedUser
    ),

    OnboardingPage(
        title = "Agenda citas y construye tu reputación",
        description =
            "Reserva horarios en tiempo real, publica tus servicios, acumula puntos y sube de nivel. De Principiante a Maestro, tu reputación habla por ti.",
        color = Color(0xFFB45309),
        background = cssLinearGradient(
            160f,
            0f to Color(0xFFFFF8E8),
            1f to Color(0xFFFFE4B3)
        ),
        icon = Icons.Filled.WorkspacePremium
    )
)

@Composable
fun OnboardingScreen(
    onFinish: () -> Unit
) {

    var currentPage by rememberSaveable {
        mutableIntStateOf(0)
    }

    val page = pages[currentPage]
    val isLast = currentPage == pages.lastIndex

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    start = 16.dp,
                    end = 16.dp,
                    top = 4.dp
                ),
            contentAlignment = Alignment.CenterEnd
        ) {

            if (!isLast) {

                ServifyTextButton(
                    text = "Omitir",
                    onClick = onFinish
                )
            }
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(
                32.dp,
                Alignment.CenterVertically
            )
        ) {

            Box(
                modifier = Modifier
                    .size(256.dp)
                    .clip(RoundedCornerShape(48.dp))
                    .background(page.background),
                contentAlignment = Alignment.Center
            ) {

                Box(
                    modifier = Modifier
                        .size(144.dp)
                        .clip(CircleShape)
                        // color + "22" en hex = 34/255 de opacidad
                        .background(
                            page.color.copy(alpha = 34f / 255f)
                        ),
                    contentAlignment = Alignment.Center
                ) {

                    Icon(
                        imageVector = page.icon,
                        contentDescription = null,
                        tint = page.color,
                        modifier = Modifier.size(72.dp)
                    )
                }
            }

            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Text(
                    text = page.title,
                    style = ServifyTextStyle.TitleTight,
                    color = ServifyOnSurface,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(
                    modifier = Modifier.height(12.dp)
                )

                Text(
                    text = page.description,
                    style = ServifyTextStyle.BodyRelaxed,
                    color = ServifyOnSurfaceVariant,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    start = 24.dp,
                    end = 24.dp,
                    bottom = 40.dp
                ),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {


            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(
                    8.dp,
                    Alignment.CenterHorizontally
                )
            ) {

                pages.indices.forEach { index ->

                    PageDot(
                        selected = index == currentPage
                    )
                }
            }

            ServifyFilledButton(
                text = if (isLast) "Comenzar" else "Continuar",
                icon =
                    if (isLast) Icons.Outlined.RocketLaunch
                    else Icons.AutoMirrored.Outlined.ArrowForward,
                onClick = {
                    if (isLast) {
                        onFinish()
                    } else {
                        currentPage++
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )

            if (isLast) {

                ServifyOutlinedButton(
                    text = "Ya tengo una cuenta",
                    onClick = onFinish,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Composable
private fun PageDot(
    selected: Boolean
) {

    val width: Dp by animateDpAsState(
        targetValue = if (selected) 24.dp else 8.dp,
        animationSpec = tween(150),
        label = "dotWidth"
    )

    Box(
        modifier = Modifier
            .size(width = width, height = 8.dp)
            .clip(CircleShape)
            .background(
                if (selected) ServifyPrimary
                else ServifyOutlineVariant
            )
    )
}
