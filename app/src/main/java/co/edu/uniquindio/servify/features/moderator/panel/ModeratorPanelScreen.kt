package co.edu.uniquindio.servify.features.moderator.panel

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Image
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import co.edu.uniquindio.servify.domain.model.ModerationMetrics
import co.edu.uniquindio.servify.domain.model.PublicationStatus
import co.edu.uniquindio.servify.features.publication.component.PublicationStatusBadge
import co.edu.uniquindio.servify.ui.components.button.ServifyFilledButton
import co.edu.uniquindio.servify.ui.components.button.ServifyIconButton
import co.edu.uniquindio.servify.ui.components.dialog.ConfirmDialog
import co.edu.uniquindio.servify.ui.components.state.LoadingState
import co.edu.uniquindio.servify.ui.theme.ServifyTextStyle
import co.edu.uniquindio.servify.ui.theme.ServifyTheme

/** Panel de moderación (pantalla 22): métricas del día y cola de publicaciones pendientes. */
@Composable
fun ModeratorPanelScreen(
    onReviewClick: (publicationId: String) -> Unit,
    onLogoutClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ModeratorPanelViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()
    var showLogoutConfirmation by remember { mutableStateOf(false) }

    if (showLogoutConfirmation) {
        ConfirmDialog(
            title = "Cerrar sesión",
            message = "¿Estás seguro de que deseas cerrar la sesión de moderador en Servify?",
            confirmText = "Cerrar sesión",
            dismissText = "Cancelar",
            isDestructive = true,
            icon = Icons.AutoMirrored.Filled.Logout,
            onConfirm = {
                showLogoutConfirmation = false
                onLogoutClick()
            },
            onDismiss = { showLogoutConfirmation = false }
        )
    }

    ModeratorPanelContent(
        state = state,
        onReviewClick = onReviewClick,
        onLogoutClick = { showLogoutConfirmation = true },
        modifier = modifier
    )
}

@Composable
fun ModeratorPanelContent(
    state: ModeratorPanelUiState,
    onReviewClick: (String) -> Unit,
    onLogoutClick: () -> Unit,
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
                    .padding(start = 16.dp, end = 8.dp, top = 8.dp, bottom = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Panel de moderación",
                        style = ServifyTextStyle.Title,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = state.moderatorEmail,
                        style = ServifyTextStyle.Small,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                ServifyIconButton(
                    icon = Icons.AutoMirrored.Filled.Logout,
                    contentDescription = "Cerrar sesión",
                    onClick = onLogoutClick
                )
            }
        }
    ) { paddingValues ->
        if (state.isLoading) {
            LoadingState(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                message = "Cargando panel..."
            )
            return@Scaffold
        }

        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            item { MetricsGrid(state.metrics) }

            item {
                Text(
                    text = "Pendientes de revisión (${state.pending.size})",
                    style = ServifyTextStyle.BodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }

            if (state.pending.isEmpty()) {
                item { EmptyQueueCard() }
            } else {
                items(state.pending, key = { it.id }) { item ->
                    PendingCard(item = item, onReviewClick = { onReviewClick(item.id) })
                }
            }
        }
    }
}

@Composable
private fun MetricsGrid(metrics: ModerationMetrics) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            MetricCard("Pendientes", metrics.pending, Modifier.weight(1f))
            MetricCard("Verificadas hoy", metrics.verifiedToday, Modifier.weight(1f))
        }
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            MetricCard("Rechazadas", metrics.rejected, Modifier.weight(1f))
            MetricCard("Finalizadas", metrics.finalized, Modifier.weight(1f))
        }
    }
}

@Composable
private fun MetricCard(label: String, value: Int, modifier: Modifier = Modifier) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
        modifier = modifier
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = value.toString(),
                style = ServifyTextStyle.Title,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            Text(
                text = label,
                style = ServifyTextStyle.Caption,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }
    }
}

@Composable
private fun EmptyQueueCard() {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "No hay publicaciones por revisar",
                style = ServifyTextStyle.BodyMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
            Text(
                text = "Cuando un usuario publique un servicio aparecerá aquí.",
                style = ServifyTextStyle.Caption,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
}

@Composable
private fun PendingCard(item: PendingItem, onReviewClick: () -> Unit) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(verticalAlignment = Alignment.Top) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(64.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Icon(
                        imageVector = Icons.Default.Image,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 12.dp)
                ) {
                    Text(
                        text = item.title,
                        style = ServifyTextStyle.BodyMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = item.category,
                        style = ServifyTextStyle.Caption,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "${item.providerName} · ${item.location}",
                        style = ServifyTextStyle.Caption,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp)
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    PublicationStatusBadge(status = PublicationStatus.PENDIENTE)
                    Text(
                        text = "#${item.position} · ${item.waitingLabel} · ${item.dateLabel}",
                        style = ServifyTextStyle.Caption,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
                ServifyFilledButton(text = "Revisar", onClick = onReviewClick)
            }
        }
    }
}

// ---------------------------------------------------------------------------
// Previews
// ---------------------------------------------------------------------------

private val previewState = ModeratorPanelUiState(
    isLoading = false,
    moderatorEmail = "moderador@servify.co",
    metrics = ModerationMetrics(pending = 2, verifiedToday = 5, rejected = 1, finalized = 3),
    pending = listOf(
        PendingItem(
            "p1", 1, "Clases de matemáticas a domicilio", "Educación > Tutorías",
            "Laura Gómez", "Armenia, Quindío", "29/10/2026", "En espera hace 5 d"
        ),
        PendingItem(
            "p2", 2, "Paseo de mascotas", "Mascotas > Paseo de mascotas",
            "Andrés Rojas", "Armenia, Quindío", "02/11/2026", "En espera hace 1 d"
        )
    )
)

@Preview(name = "Panel claro", showBackground = true)
@Composable
private fun ModeratorPanelPreviewLight() {
    ServifyTheme { ModeratorPanelContent(previewState, onReviewClick = {}, onLogoutClick = {}) }
}

@Preview(name = "Panel oscuro", showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun ModeratorPanelPreviewDark() {
    ServifyTheme { ModeratorPanelContent(previewState, onReviewClick = {}, onLogoutClick = {}) }
}
