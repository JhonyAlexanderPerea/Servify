package co.edu.uniquindio.servify.features.publication.mine

import android.content.res.Configuration
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.outlined.ListAlt
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PrimaryScrollableTabRow
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import co.edu.uniquindio.servify.data.seed.SeedPublications
import co.edu.uniquindio.servify.data.seed.SeedUsers
import co.edu.uniquindio.servify.domain.model.Publication
import co.edu.uniquindio.servify.domain.model.PublicationStatus
import co.edu.uniquindio.servify.domain.rules.PublicationAction
import co.edu.uniquindio.servify.domain.rules.PublicationRules
import co.edu.uniquindio.servify.domain.rules.PublicationTab
import co.edu.uniquindio.servify.features.publication.component.PublicationStatusBadge
import co.edu.uniquindio.servify.ui.components.button.ServifyFilledButton
import co.edu.uniquindio.servify.ui.components.button.ServifyIconButton
import co.edu.uniquindio.servify.ui.theme.ServifyTextStyle
import co.edu.uniquindio.servify.ui.theme.ServifyTheme

/** Navegación que dispara la pantalla; Eliminar/Finalizar las resuelve el ViewModel. */
data class MyPublicationsActions(
    val onBackClick: () -> Unit = {},
    val onViewClick: (publicationId: String) -> Unit = {},
    val onEditClick: (publicationId: String) -> Unit = {},
    val onCreateClick: () -> Unit = {}
)

@Composable
fun MyPublicationsScreen(
    actions: MyPublicationsActions,
    modifier: Modifier = Modifier,
    viewModel: MyPublicationsViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(state.message) {
        state.message?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.onMessageShown()
        }
    }

    MyPublicationsContent(
        state = state,
        actions = actions,
        snackbarHostState = snackbarHostState,
        onTabSelected = viewModel::onTabSelected,
        onActionRequested = viewModel::onActionRequested,
        onConfirm = viewModel::onConfirmPending,
        onDismiss = viewModel::onDismissPending,
        modifier = modifier
    )
}

@Composable
fun MyPublicationsContent(
    state: MyPublicationsUiState,
    actions: MyPublicationsActions,
    snackbarHostState: SnackbarHostState,
    onTabSelected: (PublicationTab) -> Unit,
    onActionRequested: (PublicationAction, Publication) -> Unit,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        snackbarHost = { SnackbarHost(snackbarHostState) },
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
                    onClick = actions.onBackClick
                )
                Text(
                    text = "Mis publicaciones",
                    style = ServifyTextStyle.Title,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(start = 4.dp)
                )
            }
        }
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (state.isEmpty) {
                EmptyState(
                    title = "Todavía no tienes publicaciones",
                    description = "Publica tu primer servicio y comienza a construir tu reputación.",
                    actionText = "Crear publicación",
                    onAction = actions.onCreateClick
                )
            } else {
                val counts = state.counts
                PrimaryScrollableTabRow(
                    selectedTabIndex = state.selectedTab.ordinal,
                    edgePadding = 16.dp,
                    containerColor = MaterialTheme.colorScheme.background
                ) {
                    PublicationTab.entries.forEach { tab ->
                        Tab(
                            selected = tab == state.selectedTab,
                            onClick = { onTabSelected(tab) },
                            text = { Text("${tab.label} (${counts[tab] ?: 0})") }
                        )
                    }
                }

                val visible = state.visiblePublications
                if (visible.isEmpty()) {
                    EmptyState(
                        title = "Sin publicaciones aquí",
                        description = "No tienes publicaciones en la pestaña “${state.selectedTab.label}”."
                    )
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(visible, key = { it.id }) { publication ->
                            PublicationCard(
                                publication = publication,
                                onView = { actions.onViewClick(publication.id) },
                                onEdit = { actions.onEditClick(publication.id) },
                                onActionRequested = { onActionRequested(it, publication) }
                            )
                        }
                    }
                }
            }
        }
    }

    state.pendingConfirmation?.let { pending ->
        ConfirmActionDialog(pending = pending, onConfirm = onConfirm, onDismiss = onDismiss)
    }
}

// -----------------------------------------------------------------------------
// TARJETA DE PUBLICACIÓN
// -----------------------------------------------------------------------------

@Composable
private fun PublicationCard(
    publication: Publication,
    onView: () -> Unit,
    onEdit: () -> Unit,
    onActionRequested: (PublicationAction) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = publication.title,
                        style = ServifyTextStyle.TitleTight,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "${publication.category} > ${publication.subcategory}",
                        style = ServifyTextStyle.Caption,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                PublicationMenu(
                    status = publication.status,
                    onView = onView,
                    onEdit = onEdit,
                    onActionRequested = onActionRequested
                )
            }

            Spacer(modifier = Modifier.height(8.dp))
            PublicationStatusBadge(status = publication.status)
            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = publication.priceRange,
                style = ServifyTextStyle.SmallMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(4.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Place,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = publication.zone,
                    style = ServifyTextStyle.Caption,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.weight(1f)
                )
                Icon(
                    imageVector = Icons.Default.Favorite,
                    contentDescription = "Personas interesadas",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = publication.interestedCount.toString(),
                    style = ServifyTextStyle.Caption,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            if (publication.status == PublicationStatus.RECHAZADA) {
                Spacer(modifier = Modifier.height(12.dp))
                RejectionBanner(
                    reason = publication.rejectionReason,
                    onEdit = onEdit
                )
            }
        }
    }
}

/** Menú ⋮ con las acciones permitidas para el estado (las define PublicationRules, no la UI). */
@Composable
private fun PublicationMenu(
    status: PublicationStatus,
    onView: () -> Unit,
    onEdit: () -> Unit,
    onActionRequested: (PublicationAction) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Box {
        IconButton(onClick = { expanded = true }) {
            Icon(
                imageVector = Icons.Default.MoreVert,
                contentDescription = "Opciones de la publicación",
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            PublicationRules.actionsFor(status).forEach { action ->
                DropdownMenuItem(
                    text = { Text(action.menuLabel()) },
                    leadingIcon = { Icon(action.icon(), contentDescription = null) },
                    onClick = {
                        expanded = false
                        when (action) {
                            PublicationAction.VER -> onView()
                            PublicationAction.EDITAR -> onEdit()
                            else -> onActionRequested(action)
                        }
                    }
                )
            }
        }
    }
}

private fun PublicationAction.menuLabel(): String = when (this) {
    PublicationAction.VER -> "Ver"
    PublicationAction.EDITAR -> "Editar"
    PublicationAction.FINALIZAR -> "Finalizar"
    PublicationAction.ELIMINAR -> "Eliminar"
}

private fun PublicationAction.icon(): ImageVector = when (this) {
    PublicationAction.VER -> Icons.Default.Visibility
    PublicationAction.EDITAR -> Icons.Default.Edit
    PublicationAction.FINALIZAR -> Icons.Default.Check
    PublicationAction.ELIMINAR -> Icons.Default.Delete
}

// -----------------------------------------------------------------------------
// BANNER DE RECHAZO, DIÁLOGO DE CONFIRMACIÓN Y ESTADO VACÍO
// -----------------------------------------------------------------------------

@Composable
private fun RejectionBanner(reason: String?, onEdit: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(MaterialTheme.colorScheme.errorContainer)
            .padding(12.dp)
    ) {
        Text(
            text = "Publicación rechazada",
            style = ServifyTextStyle.SmallMedium.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onErrorContainer
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "Motivo del rechazo: ${reason ?: "no se indicó un motivo."}",
            style = ServifyTextStyle.Small,
            color = MaterialTheme.colorScheme.onErrorContainer
        )
        Spacer(modifier = Modifier.height(10.dp))
        ServifyFilledButton(
            text = "Editar publicación",
            icon = Icons.Default.Edit,
            onClick = onEdit,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
private fun ConfirmActionDialog(
    pending: PendingConfirmation,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    val isDelete = pending.action == PublicationAction.ELIMINAR
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (isDelete) "¿Eliminar publicación?" else "¿Finalizar publicación?") },
        text = {
            Text(
                if (isDelete) {
                    "“${pending.publicationTitle}” se eliminará y dejará de aparecer. Esta acción no se puede deshacer."
                } else {
                    "“${pending.publicationTitle}” se marcará como finalizada y dejará de recibir nuevas solicitudes."
                }
            )
        },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text(
                    text = if (isDelete) "Eliminar" else "Finalizar",
                    color = if (isDelete) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
                )
            }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancelar") } }
    )
}

@Composable
private fun EmptyState(
    title: String,
    description: String,
    modifier: Modifier = Modifier,
    actionText: String? = null,
    onAction: () -> Unit = {}
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(32.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Outlined.ListAlt,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(48.dp)
        )
        Text(
            text = title,
            style = ServifyTextStyle.Title,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center
        )
        Text(
            text = description,
            style = ServifyTextStyle.SmallRelaxed,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
        if (actionText != null) {
            Spacer(modifier = Modifier.height(4.dp))
            ServifyFilledButton(text = actionText, onClick = onAction)
        }
    }
}

// -----------------------------------------------------------------------------
// PREVIEWS: usan los mismos datos semilla que la app
// -----------------------------------------------------------------------------

private val previewState = MyPublicationsUiState(
    isLoading = false,
    publications = SeedPublications.publications.filter { it.authorId == SeedUsers.JHONY_ID }
)

@Preview(name = "Mis publicaciones Light", showBackground = true)
@Composable
fun MyPublicationsPreviewLight() {
    ServifyTheme(darkTheme = false) {
        MyPublicationsContent(
            state = previewState,
            actions = MyPublicationsActions(),
            snackbarHostState = remember { SnackbarHostState() },
            onTabSelected = {}, onActionRequested = { _, _ -> }, onConfirm = {}, onDismiss = {}
        )
    }
}

@Preview(name = "Mis publicaciones Dark", showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun MyPublicationsPreviewDark() {
    ServifyTheme(darkTheme = true) {
        MyPublicationsContent(
            state = previewState.copy(selectedTab = PublicationTab.RECHAZADAS),
            actions = MyPublicationsActions(),
            snackbarHostState = remember { SnackbarHostState() },
            onTabSelected = {}, onActionRequested = { _, _ -> }, onConfirm = {}, onDismiss = {}
        )
    }
}

@Preview(name = "Mis publicaciones vacío", showBackground = true)
@Composable
fun MyPublicationsPreviewEmpty() {
    ServifyTheme(darkTheme = false) {
        MyPublicationsContent(
            state = MyPublicationsUiState(isLoading = false),
            actions = MyPublicationsActions(),
            snackbarHostState = remember { SnackbarHostState() },
            onTabSelected = {}, onActionRequested = { _, _ -> }, onConfirm = {}, onDismiss = {}
        )
    }
}
