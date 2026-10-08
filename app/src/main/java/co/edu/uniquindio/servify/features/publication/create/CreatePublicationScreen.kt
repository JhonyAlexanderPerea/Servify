package co.edu.uniquindio.servify.features.publication.create

import android.content.res.Configuration
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import co.edu.uniquindio.servify.features.publication.component.AiSuggestionCard
import co.edu.uniquindio.servify.features.publication.component.CreatePublicationBottomBar
import co.edu.uniquindio.servify.features.publication.component.CreatePublicationTopBar
import co.edu.uniquindio.servify.features.publication.component.CustomDropdownField
import co.edu.uniquindio.servify.features.publication.component.LocationPickerField
import co.edu.uniquindio.servify.features.publication.component.PhotoPickerGrid
import co.edu.uniquindio.servify.features.publication.component.PriceRangeFields
import co.edu.uniquindio.servify.features.publication.component.PublicationPreviewCard
import co.edu.uniquindio.servify.features.publication.component.StepIndicatorRow
import co.edu.uniquindio.servify.features.publication.model.CreatePublicationMockData
import co.edu.uniquindio.servify.features.publication.model.CreatePublicationUiState
import co.edu.uniquindio.servify.ui.components.button.ServifyFilledButton
import co.edu.uniquindio.servify.ui.components.input.ServifyTextField
import co.edu.uniquindio.servify.ui.theme.ServifyOnSurface
import co.edu.uniquindio.servify.ui.theme.ServifyOnSurfaceVariant
import co.edu.uniquindio.servify.ui.theme.ServifyPrimary
import co.edu.uniquindio.servify.ui.theme.ServifySurface
import co.edu.uniquindio.servify.ui.theme.ServifySurfaceVariant
import co.edu.uniquindio.servify.ui.theme.ServifyTertiary
import co.edu.uniquindio.servify.ui.theme.ServifyTheme
import co.edu.uniquindio.servify.ui.theme.ServifyTextStyle
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun CreatePublicationScreen(
    onBackClick: () -> Unit,
    onPublishSuccess: () -> Unit,
    modifier: Modifier = Modifier,
    initialState: CreatePublicationUiState = CreatePublicationUiState()
) {
    var uiState by remember { mutableStateOf(initialState) }
    val coroutineScope = rememberCoroutineScope()

    val categories = CreatePublicationMockData.categoriesList.map { it.name }
    val selectedCategoryObj = CreatePublicationMockData.categoriesList.find { it.name == uiState.selectedCategory }
    val subcategories = selectedCategoryObj?.subcategories ?: emptyList()

    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .background(ServifySurface)
            .statusBarsPadding()
            .navigationBarsPadding(),
        topBar = {
            CreatePublicationTopBar(
                onBackClick = onBackClick,
                currentStep = uiState.currentStep
            )
        },
        bottomBar = {
            if (!uiState.isPublishedSuccess) {
                CreatePublicationBottomBar(
                    currentStep = uiState.currentStep,
                    canGoNext = when (uiState.currentStep) {
                        1 -> uiState.isStep1Valid
                        2 -> uiState.isStep2Valid
                        else -> true
                    },
                    onPreviousClick = {
                        if (uiState.currentStep > 1) {
                            uiState = uiState.copy(currentStep = uiState.currentStep - 1)
                        }
                    },
                    onNextClick = {
                        if (uiState.currentStep < 3) {
                            uiState = uiState.copy(currentStep = uiState.currentStep + 1)
                        } else {
                            uiState = uiState.copy(isPublishedSuccess = true)
                        }
                    }
                )
            }
        }
    ) { paddingValues ->

        if (uiState.isPublishedSuccess) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFDCFCE7)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = "Éxito",
                        tint = ServifyTertiary,
                        modifier = Modifier.size(48.dp)
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                Text(
                    text = "¡Publicación Enviada a Revisión!",
                    style = ServifyTextStyle.TitleTight.copy(fontSize = 22.sp),
                    color = ServifyOnSurface
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Tu servicio fue registrado exitosamente. Nuestro equipo de moderadores o la IA revisarán los detalles y estará visible en el Marketplace en pocos minutos.",
                    style = ServifyTextStyle.SmallRelaxed,
                    color = ServifyOnSurfaceVariant
                )

                Spacer(modifier = Modifier.height(24.dp))

                ServifyFilledButton(
                    text = "Ir al Inicio / Mis Publicaciones",
                    onClick = onPublishSuccess,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                StepIndicatorRow(
                    currentStep = uiState.currentStep,
                    onStepClick = { targetStep ->
                        if (targetStep < uiState.currentStep) {
                            uiState = uiState.copy(currentStep = targetStep)
                        }
                    }
                )

                AnimatedContent(
                    targetState = uiState.currentStep,
                    transitionSpec = { fadeIn() togetherWith fadeOut() },
                    label = "CreatePublicationStepTransition"
                ) { step ->
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        when (step) {
                            1 -> {
                                Text(
                                    text = "Paso 1: Información Básica del Servicio",
                                    style = ServifyTextStyle.TitleTight.copy(fontSize = 20.sp),
                                    color = ServifyOnSurface
                                )

                                Text(
                                    text = "Escribe un título claro y selecciona la categoría que mejor describa tu servicio.",
                                    style = ServifyTextStyle.Small,
                                    color = ServifyOnSurfaceVariant
                                )

                                ServifyTextField(
                                    value = uiState.title,
                                    onValueChange = { uiState = uiState.copy(title = it) },
                                    label = "Título del servicio",
                                    singleLine = true,
                                    supportingText = "Ej: Plomero residencial con atención de emergencias"
                                )

                                CustomDropdownField(
                                    label = "Categoría Principal",
                                    selectedValue = uiState.selectedCategory,
                                    options = categories,
                                    onOptionSelected = { categoryName ->
                                        uiState = uiState.copy(
                                            selectedCategory = categoryName,
                                            selectedSubcategory = ""
                                        )
                                    },
                                    placeholder = "Selecciona una categoría"
                                )

                                if (subcategories.isNotEmpty()) {
                                    CustomDropdownField(
                                        label = "Subcategoría Especializada",
                                        selectedValue = uiState.selectedSubcategory,
                                        options = subcategories,
                                        onOptionSelected = { subName ->
                                            uiState = uiState.copy(selectedSubcategory = subName)
                                        },
                                        placeholder = "Selecciona subcategoría"
                                    )
                                }

                                PhotoPickerGrid(
                                    photoUrls = uiState.photoUrls,
                                    onAddPhotoClick = {
                                        if (uiState.photoUrls.size < 5) {
                                            val nextMockPhoto = CreatePublicationMockData.samplePhotos[uiState.photoUrls.size % CreatePublicationMockData.samplePhotos.size]
                                            uiState = uiState.copy(
                                                photoUrls = uiState.photoUrls + nextMockPhoto
                                            )
                                        }
                                    },
                                    onRemovePhotoClick = { index ->
                                        val updatedList = uiState.photoUrls.toMutableList().apply { removeAt(index) }
                                        uiState = uiState.copy(photoUrls = updatedList)
                                    }
                                )
                            }

                            2 -> {
                                Text(
                                    text = "Paso 2: Detalles, Precios y Sugerencias de IA",
                                    style = ServifyTextStyle.TitleTight.copy(fontSize = 20.sp),
                                    color = ServifyOnSurface
                                )

                                AiSuggestionCard(
                                    isAnalyzing = uiState.isAiAnalyzing,
                                    showSuggestion = uiState.showAiSuggestion,
                                    suggestedTitle = uiState.aiSuggestedTitle,
                                    suggestedDescription = uiState.aiSuggestedDescription,
                                    suggestedCategory = uiState.aiSuggestedCategory,
                                    suggestedPrice = uiState.aiSuggestedPrice,
                                    onGenerateAiClick = {
                                        coroutineScope.launch {
                                            uiState = uiState.copy(isAiAnalyzing = true)
                                            delay(1500)
                                            uiState = uiState.copy(
                                                isAiAnalyzing = false,
                                                showAiSuggestion = true,
                                                aiSuggestedTitle = "Plomería Residencial Certificada y Atención de Emergencias 24/7",
                                                aiSuggestedDescription = "Servicio profesional de plomería con 10 años de experiencia. Reparación de fugas, mantenimiento de tuberías y grifería con garantía de 6 meses por escrito.",
                                                aiSuggestedCategory = "Hogar • Plomería",
                                                aiSuggestedPrice = "$80.000 – $150.000 COP"
                                            )
                                        }
                                    },
                                    onApplyAiClick = {
                                        uiState = uiState.copy(
                                            title = uiState.aiSuggestedTitle,
                                            description = uiState.aiSuggestedDescription,
                                            minPrice = "80000",
                                            maxPrice = "150000"
                                        )
                                    }
                                )

                                ServifyTextField(
                                    value = uiState.description,
                                    onValueChange = { uiState = uiState.copy(description = it) },
                                    label = "Descripción detallada del servicio",
                                    singleLine = false,
                                    supportingText = "Describe tu experiencia, herramientas, horario y garantías."
                                )

                                PriceRangeFields(
                                    minPrice = uiState.minPrice,
                                    onMinPriceChange = { uiState = uiState.copy(minPrice = it) },
                                    maxPrice = uiState.maxPrice,
                                    onMaxPriceChange = { uiState = uiState.copy(maxPrice = it) }
                                )

                                LocationPickerField(
                                    cityZone = uiState.cityZone,
                                    onCityZoneChange = { uiState = uiState.copy(cityZone = it) }
                                )
                            }

                            3 -> {
                                Text(
                                    text = "Paso 3: Vista Previa y Verificación Final",
                                    style = ServifyTextStyle.TitleTight.copy(fontSize = 20.sp),
                                    color = ServifyOnSurface
                                )

                                Text(
                                    text = "Revisa cómo verán los clientes tu anuncio en el Marketplace antes de publicarlo.",
                                    style = ServifyTextStyle.Small,
                                    color = ServifyOnSurfaceVariant
                                )

                                PublicationPreviewCard(state = uiState)

                                Card(
                                    shape = RoundedCornerShape(16.dp),
                                    colors = CardDefaults.cardColors(containerColor = ServifySurfaceVariant)
                                ) {
                                    Row(
                                        modifier = Modifier.padding(14.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Info,
                                            contentDescription = null,
                                            tint = ServifyPrimary,
                                            modifier = Modifier.size(20.dp)
                                        )
                                        Spacer(modifier = Modifier.width(10.dp))
                                        Text(
                                            text = "Tu anuncio cumplirá con las normas de convivencia de Servify y ganará puntos de reputación en tu perfil.",
                                            style = ServifyTextStyle.Caption,
                                            color = ServifyOnSurface
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// -----------------------------------------------------------------------------
// PREVIEWS EN DISTINTOS ESTADOS (PASO 1, PASO 2 CON IA, PASO 3) Y TEMAS
// -----------------------------------------------------------------------------

@Preview(name = "Step 1 Empty Light", showBackground = true)
@Composable
fun CreatePublicationPreviewStep1Light() {
    ServifyTheme(darkTheme = false) {
        CreatePublicationScreen(
            onBackClick = {},
            onPublishSuccess = {}
        )
    }
}

@Preview(name = "Step 2 With AI Light", showBackground = true)
@Composable
fun CreatePublicationPreviewStep2Light() {
    ServifyTheme(darkTheme = false) {
        CreatePublicationScreen(
            onBackClick = {},
            onPublishSuccess = {},
            initialState = CreatePublicationMockData.filledState
        )
    }
}

@Preview(name = "Step 3 Preview Dark", showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun CreatePublicationPreviewStep3Dark() {
    ServifyTheme(darkTheme = true) {
        CreatePublicationScreen(
            onBackClick = {},
            onPublishSuccess = {},
            initialState = CreatePublicationMockData.filledState.copy(currentStep = 3)
        )
    }
}
