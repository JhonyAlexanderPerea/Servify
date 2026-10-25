package co.edu.uniquindio.servify.features.profile

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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ListAlt
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.RequestQuote
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import co.edu.uniquindio.servify.domain.model.Badge
import co.edu.uniquindio.servify.features.home.component.LevelBadge
import co.edu.uniquindio.servify.ui.components.feedback.ServifyProgressBar
import co.edu.uniquindio.servify.ui.theme.ServifyTextStyle
import co.edu.uniquindio.servify.ui.theme.ServifyTheme

/** Acciones de navegación del Perfil: una por sección del mockup (pantalla 17). */
data class ProfileActions(
    val onMyPublicationsClick: () -> Unit = {},
    val onMyRequestsClick: () -> Unit = {},
    val onAchievementsClick: () -> Unit = {},
    val onStatisticsClick: () -> Unit = {},
    val onSettingsClick: () -> Unit = {},
    val onLogoutClick: () -> Unit = {},
    val onNavigateToEditProfile: () -> Unit = {},
    val onNavigateToStatistics: () -> Unit = {},
)

@Composable
fun ProfileScreen(
    actions: ProfileActions,
    modifier: Modifier = Modifier,
    viewModel: ProfileViewModel = hiltViewModel()

) {
    val state by viewModel.uiState.collectAsState()
    ProfileContent(state = state, actions = actions, modifier = modifier)
}

@Composable
fun ProfileContent(
    state: ProfileUiState,
    actions: ProfileActions,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Mi Perfil & Reputación",
                    style = ServifyTextStyle.TitleTight.copy(fontSize = 22.sp),
                    color = MaterialTheme.colorScheme.onBackground
                )

                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .clickable(onClick = actions.onNavigateToEditProfile),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Editar perfil",
                        tint = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    ) { paddingValues ->

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item { ProfileHeaderCard(state) }
            item { BadgesSection(state) }
            item { ProfileMenu(actions) }
        }
    }
}

// -----------------------------------------------------------------------------
// CABECERA: avatar, nombre, nivel, puntos y progreso hacia el siguiente nivel
// -----------------------------------------------------------------------------

@Composable
private fun ProfileHeaderCard(state: ProfileUiState) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = state.initials,
                        style = ServifyTextStyle.Title.copy(
                            color = MaterialTheme.colorScheme.onPrimary,
                            fontSize = 22.sp
                        )
                    )
                }

                Spacer(modifier = Modifier.width(14.dp))

                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = state.fullName,
                            style = ServifyTextStyle.TitleTight.copy(fontSize = 18.sp),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        if (state.levelLabel.isNotEmpty()) LevelBadge(level = state.levelLabel)
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Place,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(2.dp))
                        Text(
                            text = state.city,
                            style = ServifyTextStyle.Small,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Progreso hacia el siguiente nivel
            ServifyProgressBar(progress = state.levelFraction)
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = state.nextLevelLabel
                    ?.let { "${state.pointsToNext} puntos para ser $it" }
                    ?: "¡Alcanzaste el nivel máximo!",
                style = ServifyTextStyle.Caption,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .padding(12.dp),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                StatItem(
                    value = formatPoints(state.points),
                    label = "Puntos Servify",
                    highlight = true
                )
                StatDivider()
                StatItem(value = state.publishedCount.toString(), label = "Publicados")
                StatDivider()
                StatItem(value = state.finalizedCount.toString(), label = "Finalizados")
            }
        }
    }
}

/** 1240 -> "1.240" (separador de miles de Colombia, independiente del idioma del teléfono). */
internal fun formatPoints(points: Int): String =
    points.toString().reversed().chunked(3).joinToString(".").reversed()

@Composable
private fun StatItem(value: String, label: String, highlight: Boolean = false) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            style = ServifyTextStyle.SmallMedium.copy(fontWeight = FontWeight.Bold),
            color = if (highlight) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = label,
            style = ServifyTextStyle.Caption,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun StatDivider() {
    Box(
        modifier = Modifier
            .width(1.dp)
            .height(28.dp)
            .background(MaterialTheme.colorScheme.outlineVariant)
    )
}

// -----------------------------------------------------------------------------
// INSIGNIAS DESBLOQUEADAS
// -----------------------------------------------------------------------------

@Composable
private fun BadgesSection(state: ProfileUiState) {
    Column {
        Text(
            text = "Insignias (${state.unlockedBadges.size}/${state.totalBadges})",
            style = ServifyTextStyle.TitleTight.copy(fontSize = 18.sp),
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        if (state.unlockedBadges.isEmpty()) {
            Text(
                text = "Aún no tienes insignias. Publica tu primer servicio para conseguir la primera.",
                style = ServifyTextStyle.Small,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        } else {
            // Dos por fila; se agrupa en filas para no depender de FlowRow (API experimental).
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                state.unlockedBadges.chunked(2).forEach { rowBadges ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        rowBadges.forEach { badge ->
                            AchievementBadgeChip(
                                badge = badge,
                                modifier = Modifier.weight(1f)
                            )
                        }
                        if (rowBadges.size == 1) Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
        }
    }
}

@Composable
private fun AchievementBadgeChip(badge: Badge, modifier: Modifier = Modifier) {
    val color = MaterialTheme.colorScheme.secondary
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(color.copy(alpha = 0.12f))
            .padding(horizontal = 10.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Default.WorkspacePremium,
            contentDescription = null,
            tint = color,
            modifier = Modifier.size(18.dp)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = badge.title,
            style = ServifyTextStyle.Caption.copy(fontWeight = FontWeight.Bold),
            color = color
        )
    }
}

// -----------------------------------------------------------------------------
// MENÚ: todas las secciones del mockup
// -----------------------------------------------------------------------------

@Composable
private fun ProfileMenu(actions: ProfileActions) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Column(modifier = Modifier.padding(vertical = 8.dp)) {
            ProfileMenuItem(Icons.Default.ListAlt, "Mis publicaciones", onClick = actions.onMyPublicationsClick)
            ProfileMenuItem(Icons.Default.RequestQuote, "Mis solicitudes", onClick = actions.onMyRequestsClick)
            ProfileMenuItem(Icons.Default.WorkspacePremium, "Mis logros", onClick = actions.onAchievementsClick)
            ProfileMenuItem(Icons.Default.BarChart, "Estadísticas", onClick = actions.onStatisticsClick)
            ProfileMenuItem(Icons.Default.Settings, "Configuración", onClick = actions.onSettingsClick)
            ProfileMenuItem(
                icon = Icons.AutoMirrored.Filled.Logout,
                title = "Cerrar sesión",
                textColor = MaterialTheme.colorScheme.error,
                onClick = actions.onLogoutClick
            )
        }
    }
}

@Composable
private fun ProfileMenuItem(
    icon: ImageVector,
    title: String,
    textColor: Color = MaterialTheme.colorScheme.onSurface,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = textColor,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = title,
                style = ServifyTextStyle.SmallMedium,
                color = textColor
            )
        }

        Icon(
            imageVector = Icons.Default.ChevronRight,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(20.dp)
        )
    }
}

// -----------------------------------------------------------------------------
// PREVIEWS (con estado de ejemplo: no necesitan Hilt)
// -----------------------------------------------------------------------------

private val previewState = ProfileUiState(
    isLoading = false,
    fullName = "Jhony Ramírez",
    initials = "JR",
    city = "Armenia, Quindío",
    levelLabel = "Experto",
    points = 1_240,
    nextLevelLabel = "Maestro",
    pointsToNext = 260,
    levelFraction = 0.71f,
    publishedCount = 3,
    finalizedCount = 1,
    unlockedBadges = listOf(Badge.PRIMERA_PUBLICACION, Badge.CIEN_PUNTOS, Badge.USUARIO_CONFIABLE)
)

@Preview(name = "Profile Screen Light", showBackground = true)
@Composable
fun ProfileScreenPreviewLight() {
    ServifyTheme(darkTheme = false) {
        ProfileContent(state = previewState, actions = ProfileActions())
    }
}

@Preview(name = "Profile Screen Dark", showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun ProfileScreenPreviewDark() {
    ServifyTheme(darkTheme = true) {
        ProfileContent(state = previewState, actions = ProfileActions())
    }
}
