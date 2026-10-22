package co.edu.uniquindio.servify.features.publication.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.HomeRepairService
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.RocketLaunch
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import co.edu.uniquindio.servify.features.publication.model.CreatePublicationUiState
import co.edu.uniquindio.servify.ui.components.button.ServifyFilledButton
import co.edu.uniquindio.servify.ui.components.button.ServifyOutlinedButton
import co.edu.uniquindio.servify.ui.components.input.ServifyTextField
import co.edu.uniquindio.servify.ui.theme.ServifyExtra
import co.edu.uniquindio.servify.ui.theme.ServifyTextStyle

// -----------------------------------------------------------------------------
// TOP APP BAR DE CREAR PUBLICACIÓN
// -----------------------------------------------------------------------------

@Composable
fun CreatePublicationTopBar(
    onBackClick: () -> Unit,
    currentStep: Int,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surfaceVariant)
                .clickable(onClick = onBackClick),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Cancelar",
                tint = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.size(20.dp)
            )
        }

        Text(
            text = "Nueva Publicación",
            style = ServifyTextStyle.SmallMedium.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onSurface
        )

        Box(
            modifier = Modifier
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primaryContainer)
                .padding(horizontal = 10.dp, vertical = 4.dp)
        ) {
            Text(
                text = "Paso $currentStep de 3",
                style = ServifyTextStyle.Caption.copy(
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
            )
        }
    }
}

// -----------------------------------------------------------------------------
// INDICADOR VISUAL DE PASOS (STEP INDICATOR)
// -----------------------------------------------------------------------------

@Composable
fun StepIndicatorRow(
    currentStep: Int,
    onStepClick: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val steps = listOf("1. Básica", "2. Detalles", "3. Previa")

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        steps.forEachIndexed { index, stepName ->
            val stepNumber = index + 1
            val isActive = currentStep == stepNumber
            val isCompleted = currentStep > stepNumber

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clip(CircleShape)
                    .clickable { onStepClick(stepNumber) }
                    .padding(vertical = 4.dp, horizontal = 6.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(26.dp)
                        .clip(CircleShape)
                        .background(
                            when {
                                isCompleted -> MaterialTheme.colorScheme.secondary
                                isActive -> MaterialTheme.colorScheme.primary
                                else -> MaterialTheme.colorScheme.surfaceVariant
                            }
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    if (isCompleted) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSecondary,
                            modifier = Modifier.size(16.dp)
                        )
                    } else {
                        Text(
                            text = "$stepNumber",
                            style = ServifyTextStyle.Caption.copy(
                                fontWeight = FontWeight.Bold,
                                color = if (isActive) {
                                    MaterialTheme.colorScheme.onPrimary
                                } else {
                                    MaterialTheme.colorScheme.onSurfaceVariant
                                }
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.width(6.dp))

                Text(
                    text = stepName,
                    style = ServifyTextStyle.Caption.copy(
                        fontWeight = if (isActive || isCompleted) FontWeight.Bold else FontWeight.Normal
                    ),
                    color = when {
                        isActive -> MaterialTheme.colorScheme.primary
                        isCompleted -> MaterialTheme.colorScheme.secondary
                        else -> MaterialTheme.colorScheme.onSurfaceVariant
                    }
                )
            }

            if (index < steps.size - 1) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(2.dp)
                        .padding(horizontal = 4.dp)
                        .background(
                            if (currentStep > stepNumber) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.outlineVariant
                        )
                )
            }
        }
    }
}

// -----------------------------------------------------------------------------
// SELECTOR DESPLEGABLE (DROPDOWN FIELD)
// -----------------------------------------------------------------------------

@Composable
fun CustomDropdownField(
    label: String,
    selectedValue: String,
    options: List<String>,
    onOptionSelected: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "Seleccionar opción"
) {
    var expanded by remember { mutableStateOf(false) }

    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = label,
            style = ServifyTextStyle.Caption.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(bottom = 4.dp, start = 4.dp)
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant)
                .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(12.dp))
                .clickable { expanded = !expanded }
                .padding(horizontal = 14.dp, vertical = 14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Category,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = if (selectedValue.isNotBlank()) selectedValue else placeholder,
                        style = ServifyTextStyle.Small,
                        color = if (selectedValue.isNotBlank()) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Icon(
                    imageVector = Icons.Default.KeyboardArrowDown,
                    contentDescription = "Desplegar",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(20.dp)
                )
            }

            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier
                    .fillMaxWidth(0.85f)
                    .background(MaterialTheme.colorScheme.surface)
            ) {
                options.forEach { option ->
                    DropdownMenuItem(
                        text = {
                            Text(
                                text = option,
                                style = ServifyTextStyle.Small,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        },
                        onClick = {
                            onOptionSelected(option)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}

// -----------------------------------------------------------------------------
// GRID / CONTENEDOR DE FOTOS (PHOTO PICKER GRID)
// -----------------------------------------------------------------------------

@Composable
fun PhotoPickerGrid(
    photoUrls: List<String>,
    onAddPhotoClick: () -> Unit,
    onRemovePhotoClick: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Fotos del servicio (${photoUrls.size}/5)",
                style = ServifyTextStyle.Caption.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(start = 4.dp)
            )

            Text(
                text = "Formatos recomendados: JPG, PNG",
                style = ServifyTextStyle.Caption,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            // Botón para simular agregar foto
            item {
                Box(
                    modifier = Modifier
                        .size(90.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(MaterialTheme.colorScheme.primaryContainer)
                        .border(1.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(16.dp))
                        .clickable(onClick = onAddPhotoClick),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Agregar foto",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(28.dp)
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Agregar",
                            style = ServifyTextStyle.Caption.copy(
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        )
                    }
                }
            }

            // Fotos añadidas (simuladas con placeholders con estilo)
            itemsIndexed(photoUrls) { index, _ ->
                Box(
                    modifier = Modifier
                        .size(90.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(
                            Brush.linearGradient(
                                colors = listOf(ServifyExtra.heroGradientStart, ServifyExtra.heroGradientEnd)
                            )
                        )
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.HomeRepairService,
                            contentDescription = null,
                            tint = ServifyExtra.onHero,
                            modifier = Modifier.size(32.dp)
                        )
                    }

                    // Botón para eliminar foto
                    Box(
                        modifier = Modifier
                            .padding(4.dp)
                            .size(22.dp)
                            .clip(CircleShape)
                            .background(Color.Black.copy(alpha = 0.6f))
                            .clickable { onRemovePhotoClick(index) }
                            .align(Alignment.TopEnd),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Eliminar",
                            tint = Color.White,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }
            }
        }
    }
}

// -----------------------------------------------------------------------------
// ASISTENTE DE IA (AI SUGGESTION CARD & ANALYZING INDICATOR)
// -----------------------------------------------------------------------------

@Composable
fun AiSuggestionCard(
    isAnalyzing: Boolean,
    showSuggestion: Boolean,
    suggestedTitle: String,
    suggestedDescription: String,
    suggestedCategory: String,
    suggestedPrice: String,
    onGenerateAiClick: () -> Unit,
    onApplyAiClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.tertiaryContainer
        ),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.tertiary.copy(alpha = 0.4f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.tertiary),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = "IA Servify",
                            tint = MaterialTheme.colorScheme.onTertiary,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Column {
                        Text(
                            text = "Asistente IA de Publicación",
                            style = ServifyTextStyle.SmallMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.tertiary
                            )
                        )
                        Text(
                            text = "Optimiza título, descripción y precio",
                            style = ServifyTextStyle.Caption,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                if (!isAnalyzing && !showSuggestion) {
                    Box(
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.tertiary)
                            .clickable(onClick = onGenerateAiClick)
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = "Optimizar con IA",
                            style = ServifyTextStyle.Caption.copy(
                                color = MaterialTheme.colorScheme.onTertiary,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                }
            }

            AnimatedVisibility(visible = isAnalyzing) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = MaterialTheme.colorScheme.tertiary,
                        strokeWidth = 2.dp
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "Analizando tu oferta con IA Servify...",
                        style = ServifyTextStyle.Small,
                        color = MaterialTheme.colorScheme.tertiary
                    )
                }
            }

            AnimatedVisibility(visible = showSuggestion && !isAnalyzing) {
                Column(modifier = Modifier.padding(top = 12.dp)) {
                    Text(
                        text = "💡 Sugerencia generada:",
                        style = ServifyTextStyle.Caption.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.tertiary
                        )
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Card(
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        border = BorderStroke(0.5.dp, MaterialTheme.colorScheme.tertiary.copy(alpha = 0.2f))
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(
                                text = suggestedTitle,
                                style = ServifyTextStyle.SmallMedium.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = suggestedDescription,
                                style = ServifyTextStyle.Caption,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                maxLines = 3,
                                overflow = TextOverflow.Ellipsis
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "Categoría: $suggestedCategory",
                                    style = ServifyTextStyle.Caption.copy(fontWeight = FontWeight.Medium),
                                    color = MaterialTheme.colorScheme.tertiary
                                )
                                Text(
                                    text = "Precio: $suggestedPrice",
                                    style = ServifyTextStyle.Caption.copy(fontWeight = FontWeight.Bold),
                                    color = MaterialTheme.colorScheme.secondary
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        Box(
                            modifier = Modifier
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.tertiary)
                                .clickable(onClick = onApplyAiClick)
                                .padding(horizontal = 14.dp, vertical = 8.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onTertiary,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Aplicar Sugerencia de IA",
                                    style = ServifyTextStyle.Caption.copy(
                                        color = MaterialTheme.colorScheme.onTertiary,
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

// -----------------------------------------------------------------------------
// RANGO DE PRECIOS Y UBICACIÓN
// -----------------------------------------------------------------------------

@Composable
fun PriceRangeFields(
    minPrice: String,
    onMinPriceChange: (String) -> Unit,
    maxPrice: String,
    onMaxPriceChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = "Rango de precio estimado ($ COP)",
            style = ServifyTextStyle.Caption.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(bottom = 6.dp, start = 4.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            ServifyTextField(
                value = minPrice,
                onValueChange = onMinPriceChange,
                label = "Precio Mínimo",
                modifier = Modifier.weight(1f)
            )

            ServifyTextField(
                value = maxPrice,
                onValueChange = onMaxPriceChange,
                label = "Precio Máximo",
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
fun LocationPickerField(
    cityZone: String,
    onCityZoneChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    ServifyTextField(
        value = cityZone,
        onValueChange = onCityZoneChange,
        label = "Ciudad y Zona de Cobertura",
        leadingIcon = Icons.Default.Place,
        modifier = modifier.fillMaxWidth()
    )
}

// -----------------------------------------------------------------------------
// RESUMEN DE VISTA PREVIA (PASO 3: PUBLICATION PREVIEW CARD)
// -----------------------------------------------------------------------------

@Composable
fun PublicationPreviewCard(
    state: CreatePublicationUiState,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primaryContainer)
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "VISTA PREVIA DEL ANUNCIO",
                        style = ServifyTextStyle.Caption.copy(
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }

                Text(
                    text = "${state.selectedCategory} • ${state.selectedSubcategory}",
                    style = ServifyTextStyle.Caption.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = if (state.title.isNotBlank()) state.title else "Título de tu publicación",
                style = ServifyTextStyle.TitleTight.copy(fontSize = 20.sp),
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(6.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Place,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = if (state.cityZone.isNotBlank()) state.cityZone else "Armenia, Quindío",
                    style = ServifyTextStyle.Small,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = if (state.description.isNotBlank()) state.description else "Descripción de la oferta de servicios...",
                style = ServifyTextStyle.SmallRelaxed,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .padding(12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Tarifa Estimada:",
                    style = ServifyTextStyle.SmallMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = if (state.minPrice.isNotBlank()) "$${state.minPrice} – $${state.maxPrice} COP" else "$80.000 – $150.000 COP",
                    style = ServifyTextStyle.SmallMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                )
            }
        }
    }
}

// -----------------------------------------------------------------------------
// BOTTOM BAR DE NAVEGACIÓN ENTRE PASOS
// -----------------------------------------------------------------------------

@Composable
fun CreatePublicationBottomBar(
    currentStep: Int,
    canGoNext: Boolean,
    onPreviousClick: () -> Unit,
    onNextClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 8.dp,
        shadowElevation = 8.dp
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 12.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (currentStep > 1) {
                ServifyOutlinedButton(
                    text = "Anterior",
                    icon = Icons.Default.ArrowBack,
                    onClick = onPreviousClick,
                    modifier = Modifier.weight(1f)
                )
            }

            ServifyFilledButton(
                text = if (currentStep == 3) "Publicar Anuncio" else "Siguiente",
                icon = if (currentStep == 3) Icons.Default.RocketLaunch else Icons.Default.ArrowForward,
                onClick = onNextClick,
                enabled = canGoNext,
                modifier = Modifier.weight(1f)
            )
        }
    }
}
