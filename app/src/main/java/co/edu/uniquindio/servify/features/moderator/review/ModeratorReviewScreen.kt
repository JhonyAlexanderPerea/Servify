package co.edu.uniquindio.servify.features.moderator.review

import android.content.res.Configuration
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Image
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import co.edu.uniquindio.servify.domain.model.Publication
import co.edu.uniquindio.servify.domain.model.PublicationStatus
import co.edu.uniquindio.servify.domain.model.RejectionReason
import co.edu.uniquindio.servify.domain.rules.ModerationRules
import co.edu.uniquindio.servify.features.publication.component.PublicationStatusBadge
import co.edu.uniquindio.servify.ui.components.button.ServifyFilledButton
import co.edu.uniquindio.servify.ui.components.button.ServifyIconButton
import co.edu.uniquindio.servify.ui.components.button.ServifyOutlinedButton
import co.edu.uniquindio.servify.ui.components.dialog.ConfirmDialog
import co.edu.uniquindio.servify.ui.components.state.LoadingState
import co.edu.uniquindio.servify.ui.theme.ServifyTextStyle
import co.edu.uniquindio.servify.ui.theme.ServifyTheme

/** Revisar publicación (pantalla 23): datos completos + verificar / rechazar / finalizar. */
@Composable
fun ModeratorReviewScreen(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ModeratorReviewViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(state.finished) {
        if (state.finished) onBackClick()
    }
    LaunchedEffect(state.message) {
        state.message?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.onMessageShown()
        }
    }

    ModeratorReviewContent(
        state = state,
        snackbarHostState = snackbarHostState,
        onBackClick = onBackClick,
        onApprove = viewModel::onApproveClick,
        onRejectClick = viewModel::onRejectClick,
        onFinalizeClick = viewModel::onFinalizeClick,
        modifier = modifier
    )

    if (state.showRejectSheet) {
        RejectReasonSheet(
            reasonText = state.rejectText,
            errorMessage = state.rejectError,
            isConfirmEnabled = state.isRejectValid && !state.isProcessing,
            onReasonSelected = viewModel::onReasonSelected,
            onReasonTextChange = viewModel::onRejectTextChange,
            onConfirm = viewModel::onConfirmReject,
            onDismiss = viewModel::onDismissRejectSheet
        )
    }

    if (state.showApproveDialog) {
        ConfirmDialog(
            title = "Verificar publicación",
            message = "La publicación será visible en el marketplace y el autor recibirá la notificación de verificación. ¿Deseas continuar?",
            confirmText = "Verificar",
            icon = Icons.Default.Check,
            onConfirm = viewModel::onConfirmApprove,
            onDismiss = viewModel::onDismissApproveDialog
        )
    }

    if (state.showFinalizeDialog) {
        ConfirmDialog(
            title = "Marcar como finalizada",
            message = "La publicación quedará como resuelta y dejará de recibir solicitudes.",
            confirmText = "Finalizar",
            onConfirm = viewModel::onConfirmFinalize,
            onDismiss = viewModel::onDismissFinalizeDialog
        )
    }
}

@Composable
fun ModeratorReviewContent(
    state: ModeratorReviewUiState,
    snackbarHostState: SnackbarHostState,
    onBackClick: () -> Unit,
    onApprove: () -> Unit,
    onRejectClick: () -> Unit,
    onFinalizeClick: () -> Unit,
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
                    onClick = onBackClick
                )
                Text(
                    text = "Revisar publicación",
                    style = ServifyTextStyle.Title,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(start = 4.dp)
                )
            }
        }
    ) { paddingValues ->
        val publication = state.publication
        when {
            state.isLoading -> LoadingState(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                message = "Cargando publicación..."
            )

            publication == null -> Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "La publicación ya no existe",
                    style = ServifyTextStyle.Body,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            else -> Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                PublicationInfo(publication)
                ReviewActions(
                    state = state,
                    onApprove = onApprove,
                    onRejectClick = onRejectClick,
                    onFinalizeClick = onFinalizeClick
                )
            }
        }
    }
}

@Composable
private fun PublicationInfo(publication: Publication) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxWidth()
            .height(180.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Icon(
            imageVector = Icons.Default.Image,
            contentDescription = "Imagen de la publicación",
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }

    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        PublicationStatusBadge(status = publication.status)
        Text(
            text = publication.title,
            style = ServifyTextStyle.Title,
            color = MaterialTheme.colorScheme.onSurface
        )
        val categoryLabel = publication.category.ifBlank { publication.categoryId }
        Text(
            text = if (publication.subcategory.isNotBlank()) "$categoryLabel > ${publication.subcategory}" else categoryLabel,
            style = ServifyTextStyle.Caption,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }

    InfoCard(title = "Descripción") {
        Text(
            text = publication.description,
            style = ServifyTextStyle.Body,
            color = MaterialTheme.colorScheme.onSurface
        )
    }

    InfoCard(title = "Detalles") {
        DetailRow("Precio", publication.priceLabel())
        DetailRow("Proveedor", publication.provider.name)
        DetailRow("Ubicación", publication.city.ifBlank { publication.locationName })
        if (publication.zone.isNotBlank()) DetailRow("Zona de cobertura", publication.zone)
        DetailRow("Fecha de envío", ModerationRules.formatDate(publication.createdAtMillis))
    }

    val reason = publication.rejectionReason
    if (publication.status == PublicationStatus.RECHAZADA && !reason.isNullOrBlank()) {
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Motivo del rechazo",
                    style = ServifyTextStyle.BodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onErrorContainer
                )
                Text(
                    text = reason,
                    style = ServifyTextStyle.Body,
                    color = MaterialTheme.colorScheme.onErrorContainer,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }
    }
}

@Composable
private fun InfoCard(title: String, content: @Composable () -> Unit) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = title,
                style = ServifyTextStyle.BodyMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
            content()
        }
    }
}

@Composable
private fun DetailRow(label: String, value: String) {
    Row(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = label,
            style = ServifyTextStyle.Caption,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.weight(0.4f)
        )
        Text(
            text = value,
            style = ServifyTextStyle.Body,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.weight(0.6f)
        )
    }
}

@Composable
private fun ReviewActions(
    state: ModeratorReviewUiState,
    onApprove: () -> Unit,
    onRejectClick: () -> Unit,
    onFinalizeClick: () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        if (state.canReview) {
            ServifyFilledButton(
                text = "Verificar publicación",
                icon = Icons.Default.Check,
                onClick = onApprove,
                enabled = !state.isProcessing,
                modifier = Modifier.fillMaxWidth()
            )
            DangerOutlinedButton(
                text = "Rechazar publicación",
                onClick = onRejectClick,
                enabled = !state.isProcessing
            )
        }
        if (state.canFinalize) {
            ServifyOutlinedButton(
                text = "Marcar como finalizada",
                icon = Icons.Default.Done,
                onClick = onFinalizeClick,
                enabled = !state.isProcessing,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun DangerOutlinedButton(text: String, onClick: () -> Unit, enabled: Boolean) {
    OutlinedButton(
        onClick = onClick,
        enabled = enabled,
        shape = CircleShape,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.error),
        colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error),
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
    ) {
        Icon(imageVector = Icons.Default.Close, contentDescription = null)
        Text(text = text, modifier = Modifier.padding(start = 8.dp))
    }
}

private fun Publication.priceLabel(): String =
    priceRange.ifBlank { "$" + "%,d".format(price.toInt()).replace(',', '.') + " " + priceUnit }

// ---------------------------------------------------------------------------
// Previews
// ---------------------------------------------------------------------------

private val previewPublication = Publication(
    id = "p1",
    title = "Clases de matemáticas a domicilio",
    description = "Refuerzo escolar en álgebra, geometría y cálculo para estudiantes de bachillerato.",
    category = "Educación",
    subcategory = "Tutorías",
    priceRange = "$40.000 – $70.000",
    zone = "Armenia y Calarcá",
    city = "Armenia, Quindío",
    status = PublicationStatus.PENDIENTE
)

@Preview(name = "Revisión claro", showBackground = true)
@Composable
private fun ReviewPreviewLight() {
    ServifyTheme {
        ModeratorReviewContent(
            state = ModeratorReviewUiState(isLoading = false, publication = previewPublication),
            snackbarHostState = remember { SnackbarHostState() },
            onBackClick = {}, onApprove = {}, onRejectClick = {}, onFinalizeClick = {}
        )
    }
}

@Preview(name = "Sheet de rechazo", showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun RejectSheetPreview() {
    ServifyTheme {
        RejectReasonSheet(
            reasonText = RejectionReason.INSUFFICIENT_INFO.suggestedText,
            errorMessage = null,
            isConfirmEnabled = true,
            onReasonSelected = {}, onReasonTextChange = {}, onConfirm = {}, onDismiss = {}
        )
    }
}
