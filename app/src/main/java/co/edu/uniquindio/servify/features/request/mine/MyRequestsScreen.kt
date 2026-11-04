package co.edu.uniquindio.servify.features.request.mine

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Assignment
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import co.edu.uniquindio.servify.domain.model.RequestStatus
import co.edu.uniquindio.servify.ui.components.button.ServifyIconButton
import co.edu.uniquindio.servify.ui.components.state.EmptyState
import co.edu.uniquindio.servify.ui.components.state.LoadingState
import co.edu.uniquindio.servify.ui.theme.ServifyTextStyle
import co.edu.uniquindio.servify.ui.theme.ServifyTheme

/** Mis solicitudes: servicios que el usuario pidió y el estado de cada solicitud. */
@Composable
fun MyRequestsScreen(
    onBackClick: () -> Unit,
    onRequestClick: (publicationId: String) -> Unit,
    onExploreClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: MyRequestsViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()
    MyRequestsContent(
        state = state,
        onBackClick = onBackClick,
        onRequestClick = onRequestClick,
        onExploreClick = onExploreClick,
        modifier = modifier
    )
}

@Composable
fun MyRequestsContent(
    state: MyRequestsUiState,
    onBackClick: () -> Unit,
    onRequestClick: (String) -> Unit,
    onExploreClick: () -> Unit,
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
                    text = "Mis solicitudes",
                    style = ServifyTextStyle.Title,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(start = 4.dp)
                )
            }
        }
    ) { paddingValues ->
        when {
            state.isLoading -> LoadingState(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                message = "Cargando tus solicitudes..."
            )

            state.requests.isEmpty() -> EmptyState(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                icon = Icons.Outlined.Assignment,
                title = "Todavía no tienes solicitudes",
                description = "Cuando solicites un servicio, aquí verás su estado.",
                actionText = "Explorar servicios",
                onActionClick = onExploreClick
            )

            else -> LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                items(state.requests, key = { it.id }) { item ->
                    RequestCard(item = item, onClick = { onRequestClick(item.publicationId) })
                }
            }
        }
    }
}

@Composable
private fun RequestCard(item: RequestItem, onClick: () -> Unit) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Row(verticalAlignment = Alignment.Top) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = item.title,
                        style = ServifyTextStyle.BodyMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = item.providerName,
                        style = ServifyTextStyle.Caption,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                RequestStatusChip(item.status)
            }
            Text(
                text = "Fecha preferida: ${item.dateLabel}",
                style = ServifyTextStyle.Caption,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = "Valor estimado: ${item.amountLabel}",
                style = ServifyTextStyle.Caption,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            item.note?.let {
                Text(
                    text = it,
                    style = ServifyTextStyle.Small,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}

@Composable
private fun RequestStatusChip(status: RequestStatus) {
    val (label, container, content) = when (status) {
        RequestStatus.PENDING -> Triple(
            "Pendiente", MaterialTheme.colorScheme.tertiaryContainer, MaterialTheme.colorScheme.onTertiaryContainer
        )
        RequestStatus.ACCEPTED -> Triple(
            "Aceptada", MaterialTheme.colorScheme.primaryContainer, MaterialTheme.colorScheme.onPrimaryContainer
        )
        RequestStatus.COMPLETED -> Triple(
            "Completada", MaterialTheme.colorScheme.secondaryContainer, MaterialTheme.colorScheme.onSecondaryContainer
        )
        RequestStatus.REJECTED -> Triple(
            "Rechazada", MaterialTheme.colorScheme.errorContainer, MaterialTheme.colorScheme.onErrorContainer
        )
        RequestStatus.CANCELLED -> Triple(
            "Cancelada", MaterialTheme.colorScheme.surfaceVariant, MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
    Surface(shape = CircleShape, color = container as Color) {
        Text(
            text = label,
            style = ServifyTextStyle.Caption,
            color = content as Color,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
        )
    }
}

// ---------------------------------------------------------------------------
// Previews
// ---------------------------------------------------------------------------

private val previewState = MyRequestsUiState(
    isLoading = false,
    requests = listOf(
        RequestItem(
            "r1", "p1", "Plomero residencial", "Juan Pérez", RequestStatus.PENDING,
            "12/11/2026 09:00", "$80.000", "Fuga en el baño principal"
        ),
        RequestItem(
            "r2", "p2", "Instalaciones eléctricas certificadas", "Luis Torres", RequestStatus.ACCEPTED,
            "15/11/2026 14:30", "$120.000", null
        ),
        RequestItem(
            "r3", "p3", "Paseo de mascotas", "María Ríos", RequestStatus.REJECTED,
            "10/11/2026 07:00", "$25.000", null
        )
    )
)

@Preview(name = "Solicitudes claro", showBackground = true)
@Composable
private fun MyRequestsPreviewLight() {
    ServifyTheme { MyRequestsContent(previewState, onBackClick = {}, onRequestClick = {}, onExploreClick = {}) }
}

@Preview(name = "Solicitudes vacío", showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun MyRequestsPreviewEmpty() {
    ServifyTheme {
        MyRequestsContent(MyRequestsUiState(isLoading = false), onBackClick = {}, onRequestClick = {}, onExploreClick = {})
    }
}
