package co.edu.uniquindio.servify.features.trust

import android.content.res.Configuration
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import co.edu.uniquindio.servify.features.home.component.LevelBadge
import co.edu.uniquindio.servify.features.service.model.TrustFactor
import co.edu.uniquindio.servify.ui.theme.ServifyOnSurface
import co.edu.uniquindio.servify.ui.theme.ServifyOnSurfaceVariant
import co.edu.uniquindio.servify.ui.theme.ServifyOutlineVariant
import co.edu.uniquindio.servify.ui.theme.ServifyPrimary
import co.edu.uniquindio.servify.ui.theme.ServifySurface
import co.edu.uniquindio.servify.ui.theme.ServifySurfaceVariant
import co.edu.uniquindio.servify.ui.theme.ServifyTertiary
import co.edu.uniquindio.servify.ui.theme.ServifyTheme
import co.edu.uniquindio.servify.ui.theme.ServifyTextStyle

@Composable
fun TrustSystemScreen(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: TrustSystemViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .background(ServifySurface)
            .statusBarsPadding()
            .navigationBarsPadding(),
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(ServifySurfaceVariant)
                        .clickable(onClick = onBackClick),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Volver",
                        tint = ServifyOnSurface,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Text(
                    text = "Índice de Confianza y Reputación",
                    style = ServifyTextStyle.TitleTight.copy(fontSize = 18.sp),
                    color = ServifyOnSurface
                )
            }
        }
    ) { paddingValues ->

        if (state.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = ServifyPrimary)
            }
        } else if (state.details != null) {
            val details = state.details!!

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // 1. TARJETA PRINCIPAL DEL GAUGE Y SCORE (0-100%)
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(24.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFE8FFF8)),
                        border = BorderStroke(1.5.dp, ServifyTertiary.copy(alpha = 0.4f))
                    ) {
                        Column(
                            modifier = Modifier
                                .padding(20.dp)
                                .fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Box(
                                modifier = Modifier.size(110.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator(
                                    progress = { details.trustScore / 100f },
                                    modifier = Modifier.fillMaxSize(),
                                    color = ServifyTertiary,
                                    strokeWidth = 10.dp,
                                    trackColor = ServifyTertiary.copy(alpha = 0.2f)
                                )

                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(
                                        text = "${details.trustScore}%",
                                        style = ServifyTextStyle.TitleTight.copy(
                                            fontSize = 28.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = ServifyTertiary
                                        )
                                    )
                                    Text(
                                        text = "Confianza",
                                        style = ServifyTextStyle.Caption,
                                        color = ServifyTertiary
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            Text(
                                text = details.user.name,
                                style = ServifyTextStyle.TitleTight.copy(fontSize = 20.sp),
                                color = ServifyOnSurface
                            )

                            Spacer(modifier = Modifier.height(6.dp))

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                LevelBadge(level = details.level.name)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "• ${details.user.completedJobsCount} Trabajos completados",
                                    style = ServifyTextStyle.Caption.copy(fontWeight = FontWeight.Bold),
                                    color = ServifyOnSurfaceVariant
                                )
                            }
                        }
                    }
                }

                // 2. ENCABEZADO Y LISTA DE FACTORES VERIFICADOS
                item {
                    Text(
                        text = "Factores de Confianza Verificados",
                        style = ServifyTextStyle.TitleTight.copy(fontSize = 18.sp),
                        color = ServifyOnSurface
                    )
                }

                items(details.factors) { factor ->
                    TrustFactorCard(factor = factor)
                }

                // 3. EXPLICACIÓN INFORMATIVA DEL SISTEMA
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = ServifySurfaceVariant)
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.Top
                        ) {
                            Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = null,
                                tint = ServifyPrimary,
                                modifier = Modifier
                                    .padding(top = 2.dp)
                                    .size(20.dp)
                            )

                            Spacer(modifier = Modifier.width(12.dp))

                            Column {
                                Text(
                                    text = "¿Cómo se calcula el score?",
                                    style = ServifyTextStyle.SmallMedium.copy(fontWeight = FontWeight.Bold),
                                    color = ServifyOnSurface
                                )

                                Spacer(modifier = Modifier.height(4.dp))

                                Text(
                                    text = details.explanation,
                                    style = ServifyTextStyle.SmallRelaxed,
                                    color = ServifyOnSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun TrustFactorCard(factor: TrustFactor) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, ServifyOutlineVariant)
    ) {
        Row(
            modifier = Modifier
                .padding(14.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .background(ServifyTertiary.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.VerifiedUser,
                    contentDescription = null,
                    tint = ServifyTertiary,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = factor.title,
                    style = ServifyTextStyle.SmallMedium.copy(fontWeight = FontWeight.Bold),
                    color = ServifyOnSurface
                )

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = factor.description,
                    style = ServifyTextStyle.Caption,
                    color = ServifyOnSurfaceVariant
                )
            }
        }
    }
}

@Preview(name = "Trust System Light", showBackground = true)
@Composable
fun TrustSystemScreenPreviewLight() {
    ServifyTheme(darkTheme = false) {
        TrustSystemScreen(onBackClick = {})
    }
}

@Preview(name = "Trust System Dark", showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun TrustSystemScreenPreviewDark() {
    ServifyTheme(darkTheme = true) {
        TrustSystemScreen(onBackClick = {})
    }
}
