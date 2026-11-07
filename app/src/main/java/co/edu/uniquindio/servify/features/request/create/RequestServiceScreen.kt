package co.edu.uniquindio.servify.features.request.create

import android.content.res.Configuration
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.EditCalendar
import androidx.compose.material.icons.filled.EventAvailable
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale
import co.edu.uniquindio.servify.features.home.component.LevelBadge
import co.edu.uniquindio.servify.features.home.model.toServiceItem
import co.edu.uniquindio.servify.ui.components.button.ServifyFilledButton
import co.edu.uniquindio.servify.ui.components.dialog.ConfirmDialog
import co.edu.uniquindio.servify.ui.components.input.ServifyTextField
import co.edu.uniquindio.servify.ui.theme.ServifyOnSurface
import co.edu.uniquindio.servify.ui.theme.ServifyOnSurfaceVariant
import co.edu.uniquindio.servify.ui.theme.ServifyOutlineVariant
import co.edu.uniquindio.servify.ui.theme.ServifyPrimary
import co.edu.uniquindio.servify.ui.theme.ServifyPrimaryContainer
import co.edu.uniquindio.servify.ui.theme.ServifySurface
import co.edu.uniquindio.servify.ui.theme.ServifySurfaceVariant
import co.edu.uniquindio.servify.ui.theme.ServifyTertiary
import co.edu.uniquindio.servify.ui.theme.ServifyTheme
import co.edu.uniquindio.servify.ui.theme.ServifyTextStyle

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RequestServiceScreen(
    onBackClick: () -> Unit,
    onRequestConfirmed: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: RequestServiceViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    var showDatePickerDialog by remember { mutableStateOf(false) }
    var showSubmitConfirmation by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(state.isSuccess) {
        if (state.isSuccess && state.createdRequestId != null) {
            onRequestConfirmed(state.createdRequestId!!)
        }
    }

    LaunchedEffect(state.userMessage) {
        state.userMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.onMessageShown()
        }
    }

    val availableSlots = listOf("08:00 AM", "10:00 AM", "02:00 PM", "04:00 PM", "06:00 PM")

    if (showDatePickerDialog) {
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = state.scheduledDateMillis
        )
        DatePickerDialog(
            onDismissRequest = { showDatePickerDialog = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let { utcMillis ->
                            val localDate = Instant.ofEpochMilli(utcMillis).atZone(ZoneId.of("UTC")).toLocalDate()
                            val localMillis = localDate.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
                            viewModel.onDateSelected(localMillis)
                        }
                        showDatePickerDialog = false
                    }
                ) {
                    Text(
                        text = "Confirmar",
                        style = ServifyTextStyle.SmallMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePickerDialog = false }) {
                    Text(
                        text = "Cancelar",
                        style = ServifyTextStyle.SmallMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    if (showSubmitConfirmation) {
        ConfirmDialog(
            title = "Confirmar solicitud",
            message = "Se enviará la solicitud para el ${formatDateLabel(state.scheduledDateMillis)} a las ${state.selectedTimeSlot}, por un valor estimado de $${state.totalAmount.toInt()} COP. ¿Deseas continuar?",
            confirmText = "Enviar solicitud",
            dismissText = "Revisar",
            icon = Icons.Default.EventAvailable,
            onConfirm = {
                showSubmitConfirmation = false
                viewModel.onSubmitRequest()
            },
            onDismiss = { showSubmitConfirmation = false }
        )
    }

    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .background(ServifySurface)
            .statusBarsPadding()
            .navigationBarsPadding(),
        snackbarHost = { SnackbarHost(snackbarHostState) },
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
                    text = "Solicitar Servicio",
                    style = ServifyTextStyle.TitleTight.copy(fontSize = 18.sp),
                    color = ServifyOnSurface
                )
            }
        }
    ) { paddingValues ->

        if (state.publication == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = ServifyPrimary)
            }
        } else {
            val publication = state.publication!!
            val serviceItem = publication.toServiceItem()

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // 1. RESUMEN DEL SERVICIO Y PROVEEDOR
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, ServifyOutlineVariant)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .background(serviceItem.providerColor),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = serviceItem.providerInitials,
                                        style = ServifyTextStyle.Caption.copy(
                                            color = Color.White,
                                            fontWeight = FontWeight.Bold
                                        )
                                    )
                                }

                                Spacer(modifier = Modifier.width(10.dp))

                                Column {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = serviceItem.providerName,
                                            style = ServifyTextStyle.SmallMedium.copy(fontWeight = FontWeight.Bold),
                                            color = ServifyOnSurface
                                        )
                                        if (serviceItem.isVerified) {
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Icon(
                                                imageVector = Icons.Default.CheckCircle,
                                                contentDescription = "Verificado",
                                                tint = ServifyTertiary,
                                                modifier = Modifier.size(14.dp)
                                            )
                                        }
                                    }
                                    LevelBadge(level = serviceItem.providerLevel)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = serviceItem.title,
                            style = ServifyTextStyle.TitleTight.copy(fontSize = 18.sp),
                            color = ServifyOnSurface,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = serviceItem.priceRange,
                            style = ServifyTextStyle.SmallMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = ServifyPrimary
                            )
                        )
                    }
                }

                // 2. AGENDA: SELECTOR DE FECHA Y HORARIO
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, ServifyOutlineVariant)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CalendarMonth,
                                    contentDescription = null,
                                    tint = ServifyPrimary,
                                    modifier = Modifier.size(22.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(
                                        text = "Fecha de atención",
                                        style = ServifyTextStyle.SmallMedium.copy(fontWeight = FontWeight.Bold),
                                        color = ServifyOnSurface
                                    )
                                    Text(
                                        text = formatDateLabel(state.scheduledDateMillis),
                                        style = ServifyTextStyle.Caption,
                                        color = ServifyPrimary,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.width(8.dp))

                            Box(
                                modifier = Modifier
                                    .clip(CircleShape)
                                    .background(ServifyPrimaryContainer)
                                    .clickable { showDatePickerDialog = true }
                                    .padding(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.EditCalendar,
                                        contentDescription = "Abrir calendario",
                                        tint = ServifyPrimary,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "Calendario",
                                        style = ServifyTextStyle.Caption.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = ServifyPrimary
                                        )
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Text(
                            text = "Seleccionar día rápido:",
                            style = ServifyTextStyle.Caption,
                            color = ServifyOnSurfaceVariant
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        val quickDates = remember { generateQuickDateChips() }

                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            items(quickDates) { option ->
                                val isSelected = isSameDay(state.scheduledDateMillis, option.millis)
                                Box(
                                    modifier = Modifier
                                        .clip(CircleShape)
                                        .background(if (isSelected) ServifyPrimary else ServifySurfaceVariant)
                                        .border(
                                            1.dp,
                                            if (isSelected) ServifyPrimary else ServifyOutlineVariant,
                                            CircleShape
                                        )
                                        .clickable { viewModel.onDateSelected(option.millis) }
                                        .padding(horizontal = 12.dp, vertical = 6.dp)
                                ) {
                                    Text(
                                        text = option.label,
                                        style = ServifyTextStyle.Caption.copy(
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                        ),
                                        color = if (isSelected) Color.White else ServifyOnSurface
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Schedule,
                                contentDescription = null,
                                tint = ServifyPrimary,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Horarios disponibles:",
                                style = ServifyTextStyle.Caption,
                                color = ServifyOnSurfaceVariant
                            )
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            items(availableSlots) { slot ->
                                val isSelected = state.selectedTimeSlot == slot
                                Box(
                                    modifier = Modifier
                                        .clip(CircleShape)
                                        .background(if (isSelected) ServifyPrimary else ServifySurfaceVariant)
                                        .border(
                                            1.dp,
                                            if (isSelected) ServifyPrimary else ServifyOutlineVariant,
                                            CircleShape
                                        )
                                        .clickable { viewModel.onTimeSlotSelected(slot) }
                                        .padding(horizontal = 14.dp, vertical = 8.dp)
                                ) {
                                    Text(
                                        text = slot,
                                        style = ServifyTextStyle.Caption.copy(
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                        ),
                                        color = if (isSelected) Color.White else ServifyOnSurface
                                    )
                                }
                            }
                        }
                    }
                }

                // 3. INDICACIONES Y DIRECCIÓN
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, ServifyOutlineVariant)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Dirección y requerimientos especiales",
                            style = ServifyTextStyle.SmallMedium.copy(fontWeight = FontWeight.Bold),
                            color = ServifyOnSurface
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        ServifyTextField(
                            value = state.note,
                            onValueChange = viewModel::onNoteChanged,
                            label = "Escribe tu dirección o detalles para el proveedor",
                            singleLine = false,
                            leadingIcon = Icons.Default.LocationOn,
                            supportingText = "Ej: Barrio Granada, Calle 12 # 14-20. Casa de portón blanco."
                        )
                    }
                }

                // 4. DESGLOSE DEL TOTAL
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = ServifyPrimaryContainer)
                ) {
                    Row(
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Total estimado a pagar",
                                style = ServifyTextStyle.Caption,
                                color = ServifyOnSurfaceVariant
                            )
                            Text(
                                text = "$${state.totalAmount.toInt()} COP",
                                style = ServifyTextStyle.TitleTight.copy(
                                    fontSize = 22.sp,
                                    color = ServifyPrimary
                                )
                            )
                        }

                        Icon(
                            imageVector = Icons.Default.CalendarMonth,
                            contentDescription = null,
                            tint = ServifyPrimary,
                            modifier = Modifier.size(32.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                ServifyFilledButton(
                    text = when {
                        state.isOwnService -> "No puedes solicitar tu propio servicio"
                        state.isSubmitting -> "Procesando..."
                        else -> "Confirmar y Enviar Solicitud"
                    },
                    icon = Icons.Default.EventAvailable,
                    onClick = { showSubmitConfirmation = true },
                    enabled = !state.isSubmitting && !state.isOwnService,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Preview(name = "Request Service Light", showBackground = true)
@Composable
fun RequestServicePreviewLight() {
    ServifyTheme(darkTheme = false) {
        RequestServiceScreen(
            onBackClick = {},
            onRequestConfirmed = {}
        )
    }
}

@Preview(name = "Request Service Dark", showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun RequestServicePreviewDark() {
    ServifyTheme(darkTheme = true) {
        RequestServiceScreen(
            onBackClick = {},
            onRequestConfirmed = {}
        )
    }
}

private data class QuickDateOption(val label: String, val millis: Long)

private fun formatDateLabel(millis: Long): String {
    val zone = ZoneId.systemDefault()
    val date = Instant.ofEpochMilli(millis).atZone(zone).toLocalDate()
    val formatter = DateTimeFormatter.ofPattern("EEEE, d 'de' MMMM 'de' yyyy", Locale.forLanguageTag("es-CO"))
    return date.format(formatter).replaceFirstChar { it.uppercase() }
}

private fun generateQuickDateChips(): List<QuickDateOption> {
    val zone = ZoneId.systemDefault()
    val today = LocalDate.now(zone)
    val formatter = DateTimeFormatter.ofPattern("EEE d MMM", Locale.forLanguageTag("es-CO"))

    return (0..4).map { dayOffset ->
        val date = today.plusDays(dayOffset.toLong())
        val millis = date.atStartOfDay(zone).toInstant().toEpochMilli()
        val label = when (dayOffset) {
            0 -> "Hoy, ${date.dayOfMonth} ${date.month.name.take(3).lowercase()}"
            1 -> "Mañana, ${date.dayOfMonth} ${date.month.name.take(3).lowercase()}"
            else -> date.format(formatter).replaceFirstChar { it.uppercase() }
        }
        QuickDateOption(label, millis)
    }
}

private fun isSameDay(millis1: Long, millis2: Long): Boolean {
    val zone = ZoneId.systemDefault()
    val date1 = Instant.ofEpochMilli(millis1).atZone(zone).toLocalDate()
    val date2 = Instant.ofEpochMilli(millis2).atZone(zone).toLocalDate()
    return date1 == date2
}
