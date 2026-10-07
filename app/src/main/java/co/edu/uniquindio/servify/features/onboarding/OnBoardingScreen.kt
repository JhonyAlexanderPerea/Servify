package co.edu.uniquindio.servify.features.onboarding

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.RocketLaunch
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private data class OnboardingPage(
    val title: String,
    val description: String,
    val color: Color,
    val background: List<Color>,
    val icon: androidx.compose.ui.graphics.vector.ImageVector
)

private val pages = listOf(

    OnboardingPage(
        title = "Encuentra servicios cerca de ti",
        description =
            "Descubre proveedores de confianza en tu ciudad y barrio. Plomeros, electricistas, tutores y más, a unos pasos de tu hogar.",
        color = Color(0xFF1A55E3),
        background = listOf(
            Color(0xFFE8F0FF),
            Color(0xFFC7D8FF)
        ),
        icon = Icons.Filled.LocationOn
    ),

    OnboardingPage(
        title = "Contrata proveedores confiables",
        description =
            "Todos los proveedores están verificados. Consulta su índice de confianza, calificaciones y reseñas de otros usuarios.",
        color = Color(0xFF006B53),
        background = listOf(
            Color(0xFFE8FFF8),
            Color(0xFFB3F0DC)
        ),
        icon = Icons.Filled.VerifiedUser
    ),

    OnboardingPage(
        title = "Agenda citas y construye tu reputación",
        description =
            "Reserva horarios en tiempo real, publica tus servicios, acumula puntos y sube de nivel. De Principiante a Maestro, tu reputación habla por ti.",
        color = Color(0xFFB45309),
        background = listOf(
            Color(0xFFFFF8E8),
            Color(0xFFFFE4B3)
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

        // Omitir
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    top = 8.dp,
                    end = 16.dp
                )
        ) {

            if (!isLast) {

                TextButton(
                    onClick = onFinish,
                    modifier = Modifier.align(Alignment.CenterEnd)
                ) {

                    Text(
                        text = "Omitir",
                        color = Color(0xFF1A55E3)
                    )
                }
            }
        }

        // Ilustración + texto
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            Box(
                modifier = Modifier
                    .size(256.dp)
                    .clip(RoundedCornerShape(48.dp))
                    .background(
                        Brush.linearGradient(
                            page.background
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {

                Box(
                    modifier = Modifier
                        .size(144.dp)
                        .clip(CircleShape)
                        .background(
                            page.color.copy(alpha = 0.13f)
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

            Spacer(
                modifier = Modifier.height(32.dp)
            )

            Text(
                text = page.title,
                color = Color(0xFF1B1B1F),
                fontSize = 24.sp,
                lineHeight = 29.sp,
                fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
            )

            Spacer(
                modifier = Modifier.height(12.dp)
            )

            Text(
                text = page.description,
                color = Color(0xFF45464F),
                fontSize = 16.sp,
                lineHeight = 24.sp
            )
        }

        // Controles
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
                horizontalArrangement = Arrangement.Center
            ) {

                pages.forEachIndexed { index, _ ->

                    Box(
                        modifier = Modifier
                            .padding(horizontal = 4.dp)
                            .size(
                                width =
                                    if (index == currentPage) 24.dp
                                    else 8.dp,
                                height = 8.dp
                            )
                            .clip(RoundedCornerShape(50))
                            .background(
                                if (index == currentPage)
                                    Color(0xFF1A55E3)
                                else
                                    Color(0xFFC8C5D0)
                            )
                    )
                }
            }

            Button(
                onClick = {
                    if (isLast) {
                        onFinish()
                    } else {
                        currentPage++
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(50),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF1A55E3),
                    contentColor = Color.White
                )
            ) {

                Icon(
                    imageVector =
                        if (isLast)
                            Icons.Filled.RocketLaunch
                        else
                            Icons.Filled.ArrowForward,
                    contentDescription = null
                )

                Spacer(
                    modifier = Modifier.size(8.dp)
                )

                Text(
                    text =
                        if (isLast)
                            "Comenzar"
                        else
                            "Continuar"
                )
            }

            if (isLast) {

                OutlinedButton(
                    onClick = onFinish,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(50)
                ) {

                    Text(
                        text = "Ya tengo una cuenta",
                        color = Color(0xFF1A55E3)
                    )
                }
            }
        }
    }
}