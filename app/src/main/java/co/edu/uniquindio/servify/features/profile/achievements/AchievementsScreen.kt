package co.edu.uniquindio.servify.features.profile.achievements

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.filled.ChatBubble
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PostAdd
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Verified
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import co.edu.uniquindio.servify.domain.model.Badge
import co.edu.uniquindio.servify.domain.rules.BadgeProgress
import co.edu.uniquindio.servify.ui.components.button.ServifyIconButton
import co.edu.uniquindio.servify.ui.components.feedback.ServifyProgressBar
import co.edu.uniquindio.servify.ui.components.state.LoadingState
import co.edu.uniquindio.servify.ui.theme.ServifyTextStyle
import co.edu.uniquindio.servify.ui.theme.ServifyTheme

@Composable
fun AchievementsScreen(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: AchievementsViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()
    AchievementsContent(state = state, onBackClick = onBackClick, modifier = modifier)
}

@Composable
fun AchievementsContent(
    state: AchievementsUiState,
    onBackClick: () -> Unit,
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
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                ServifyIconButton(
                    icon = Icons.AutoMirrored.Outlined.ArrowBack,
                    contentDescription = "Atrás",
                    onClick = onBackClick
                )
                Text(
                    text = "Mis logros",
                    style = ServifyTextStyle.Title,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(start = 4.dp)
                )
            }
        }
    ) { paddingValues ->
        if (state.isLoading) {
            LoadingState(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                message = "Cargando tus logros..."
            )
            return@Scaffold
        }

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            contentPadding = PaddingValues(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            item(span = { GridItemSpan(maxLineSpan) }) { SummaryCard(state) }

            state.nextBadge?.let { next ->
                item(span = { GridItemSpan(maxLineSpan) }) { NextAchievementCard(next) }
            }

            if (state.unlocked.isNotEmpty()) {
                item(span = { GridItemSpan(maxLineSpan) }) {
                    SectionTitle("Desbloqueadas (${state.unlocked.size})")
                }
                items(state.unlocked, key = { it.badge.name }) { BadgeCard(it) }
            }

            if (state.locked.isNotEmpty()) {
                item(span = { GridItemSpan(maxLineSpan) }) {
                    SectionTitle("Bloqueadas (${state.locked.size})")
                }
                items(state.locked, key = { it.badge.name }) { BadgeCard(it) }
            }
        }
    }
}

@Composable
private fun SummaryCard(state: AchievementsUiState) {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                text = "${state.unlocked.size} de ${state.totalCount} insignias",
                style = ServifyTextStyle.Title,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            Text(
                text = "Sigue publicando, comentando y recibiendo votos para desbloquear más.",
                style = ServifyTextStyle.Caption,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                modifier = Modifier.padding(top = 4.dp, bottom = 12.dp)
            )
            ServifyProgressBar(progress = state.globalFraction, modifier = Modifier.fillMaxWidth())
        }
    }
}

@Composable
private fun NextAchievementCard(item: BadgeProgress) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Tu siguiente logro",
                style = ServifyTextStyle.Caption,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = item.badge.title,
                style = ServifyTextStyle.BodyMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(top = 2.dp)
            )
            Text(
                text = item.badge.requirement,
                style = ServifyTextStyle.Caption,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(vertical = 6.dp)
            )
            ServifyProgressBar(progress = item.fraction, modifier = Modifier.fillMaxWidth())
            Text(
                text = "${item.current} / ${item.target}",
                style = ServifyTextStyle.Caption,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
}

@Composable
private fun SectionTitle(text: String) {
    Text(
        text = text,
        style = ServifyTextStyle.BodyMedium,
        fontWeight = FontWeight.SemiBold,
        color = MaterialTheme.colorScheme.onSurface,
        modifier = Modifier.padding(top = 4.dp)
    )
}

@Composable
private fun BadgeCard(item: BadgeProgress) {
    val unlocked = item.unlocked
    val container = if (unlocked) MaterialTheme.colorScheme.primaryContainer
    else MaterialTheme.colorScheme.surfaceVariant
    val content = if (unlocked) MaterialTheme.colorScheme.onPrimaryContainer
    else MaterialTheme.colorScheme.onSurfaceVariant

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = container),
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 176.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(
                        if (unlocked) MaterialTheme.colorScheme.primary
                        else MaterialTheme.colorScheme.outlineVariant
                    )
            ) {
                Icon(
                    imageVector = if (unlocked) item.badge.icon() else Icons.Default.Lock,
                    contentDescription = if (unlocked) "Insignia desbloqueada" else "Insignia bloqueada",
                    tint = if (unlocked) MaterialTheme.colorScheme.onPrimary
                    else MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(28.dp)
                )
            }
            Spacer(modifier = Modifier.width(0.dp))
            Text(
                text = item.badge.title,
                style = ServifyTextStyle.BodyMedium,
                fontWeight = FontWeight.SemiBold,
                color = content,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 10.dp)
            )
            Text(
                text = item.badge.requirement,
                style = ServifyTextStyle.Caption,
                color = content,
                textAlign = TextAlign.Center,
                maxLines = 4,
                modifier = Modifier.padding(top = 4.dp)
            )
            if (!unlocked) {
                ServifyProgressBar(
                    progress = item.fraction,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp)
                )
                Text(
                    text = "${item.current} / ${item.target}",
                    style = ServifyTextStyle.Caption,
                    color = content,
                    modifier = Modifier.padding(top = 2.dp)
                )
            }
        }
    }
}

private fun Badge.icon(): ImageVector = when (this) {
    Badge.PRIMERA_PUBLICACION -> Icons.Default.PostAdd
    Badge.PRIMERA_VERIFICADA -> Icons.Default.Verified
    Badge.DIEZ_VERIFICADAS -> Icons.Default.WorkspacePremium
    Badge.CIEN_PUNTOS -> Icons.Default.Star
    Badge.PUBLICACION_DESTACADA -> Icons.Default.EmojiEvents
    Badge.DIEZ_COMENTARIOS -> Icons.Default.ChatBubble
    Badge.USUARIO_CONFIABLE -> Icons.Default.Shield
}

// ---------------------------------------------------------------------------
// Previews
// ---------------------------------------------------------------------------

private val previewState = AchievementsUiState(
    isLoading = false,
    badges = listOf(
        BadgeProgress(Badge.PRIMERA_PUBLICACION, 1, 1, true),
        BadgeProgress(Badge.PRIMERA_VERIFICADA, 1, 1, true),
        BadgeProgress(Badge.CIEN_PUNTOS, 100, 100, true),
        BadgeProgress(Badge.DIEZ_VERIFICADAS, 4, 10, false),
        BadgeProgress(Badge.PUBLICACION_DESTACADA, 2, 5, false),
        BadgeProgress(Badge.DIEZ_COMENTARIOS, 7, 10, false),
        BadgeProgress(Badge.USUARIO_CONFIABLE, 1, 3, false)
    )
)

@Preview(name = "Logros claro", showBackground = true)
@Composable
private fun AchievementsPreviewLight() {
    ServifyTheme { AchievementsContent(state = previewState, onBackClick = {}) }
}

@Preview(
    name = "Logros oscuro",
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
@Composable
private fun AchievementsPreviewDark() {
    ServifyTheme { AchievementsContent(state = previewState, onBackClick = {}) }
}