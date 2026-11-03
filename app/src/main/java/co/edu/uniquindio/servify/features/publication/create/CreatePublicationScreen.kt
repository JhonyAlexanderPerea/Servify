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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
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
import co.edu.uniquindio.servify.ui.theme.ServifyPrimary
import co.edu.uniquindio.servify.ui.theme.ServifyTheme
import co.edu.uniquindio.servify.ui.theme.ServifyTextStyle

@Composable
fun CreatePublicationScreen(
    onBackClick: () -> Unit,
    onPublishSuccess: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: PublicationFlowViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    val categories = CreatePublicationMockData.categoriesList.map { it.name }
    val selectedCategoryObj = CreatePublicationMockData.categoriesList.find { it.name == state.categoryId }
    val subcategories = selectedCategoryObj?.subcategories ?: emptyList()

    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding()
            .navigationBarsPadding(),
        topBar = {
            CreatePublicationTopBar(
                onBackClick = onBackClick,
                currentStep = state.currentStep
            )
        },
        bottomBar = {
            if (!state.isSuccess) {
                CreatePublicationBottomBar(
                    currentStep = state.currentStep,
                    canGoNext = when (state.currentStep) {
                        1 -> state.title.length >= 5 && state.categoryId.isNotBlank()
                        2 -> state.description.length >= 20 && state.price.isNotBlank()
                        else -> true
                    },
                    onPreviousClick = viewModel::onPreviousStep,
                    onNextClick = {
                        if (state.currentStep < 3) {
                            viewModel.onNextStep()
                        } else {
                            viewModel.onSavePublication()
                        }
                    }
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
        } else if (state.isSuccess) {
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
                        .background(MaterialTheme.colorScheme.secondaryContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = "Éxito",
                        tint = MaterialTheme.colorScheme.secondary,
                        modifier = Modifier.size(48.dp)
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                Text(
                    text = if (state.publicationId != null) "¡Publicación Actualizada Exitosamente!" else "¡Publicación Creada y Publicada!",
                    style = ServifyTextStyle.TitleTight.copy(fontSize = 22.sp),
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Tu servicio fue registrado reactivamente en la capa de datos en memoria. Ya está disponible en el feed del Marketplace y en tu perfil de proveedor.",
                    style = ServifyTextStyle.SmallRelaxed,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
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
                    currentStep = state.currentStep,
                    onStepClick = { targetStep ->
                        if (targetStep < state.currentStep) {
                            viewModel.onPreviousStep()
                        }
                    }
                )

                AnimatedContent(
                    targetState = state.currentStep,
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
                                    color = MaterialTheme.colorScheme.onSurface
                                )

                                Text(
                                    text = "Escribe un título claro y selecciona la categoría que mejor describa tu servicio.",
                                    style = ServifyTextStyle.Small,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )

                                ServifyTextField(
                                    value = state.title,
                                    onValueChange = viewModel::onTitleChanged,
                                    label = "Título del servicio",
                                    singleLine = true,
                                    isError = state.titleError != null,
                                    supportingText = state.titleError ?: "Ej: Plomero residencial con atención de emergencias"
                                )

                                CustomDropdownField(
                                    label = "Categoría Principal",
                                    selectedValue = state.categoryId,
                                    options = categories,
                                    onOptionSelected = viewModel::onCategoryChanged,
                                    placeholder = "Selecciona una categoría"
                                )

                                if (subcategories.isNotEmpty()) {
                                    CustomDropdownField(
                                        label = "Subcategoría Especializada",
                                        selectedValue = state.subcategoryId,
                                        options = subcategories,
                                        onOptionSelected = viewModel::onSubcategoryChanged,
                                        placeholder = "Selecciona subcategoría"
                                    )
                                }

                                PhotoPickerGrid(
                                    photoUrls = state.images,
                                    onAddPhotoClick = {
                                        if (state.images.size < 5) {
                                            val nextMockPhoto = CreatePublicationMockData.samplePhotos[state.images.size % CreatePublicationMockData.samplePhotos.size]
                                            viewModel.onAddImage(nextMockPhoto)
                                        }
                                    },
                                    onRemovePhotoClick = viewModel::onRemoveImage
                                )
                            }

                            2 -> {
                                Text(
                                    text = "Paso 2: Detalles, Precios y Sugerencias de IA",
                                    style = ServifyTextStyle.TitleTight.copy(fontSize = 20.sp),
                                    color = MaterialTheme.colorScheme.onSurface
                                )

                                AiSuggestionCard(
                                    isAnalyzing = state.isAiAnalyzing,
                                    showSuggestion = state.showAiSuggestion,
                                    suggestedTitle = state.aiSuggestedTitle,
                                    suggestedDescription = state.aiSuggestedDescription,
                                    suggestedCategory = state.aiSuggestedCategory,
                                    suggestedPrice = state.aiSuggestedPrice,
                                    onGenerateAiClick = {
                                        viewModel.onApplyAiSuggestion(
                                            title = "Plomería Residencial Certificada y Atención de Emergencias 24/7",
                                            desc = "Servicio profesional de plomería con 10 años de experiencia. Reparación de fugas, mantenimiento de tuberías y grifería con garantía de 6 meses por escrito.",
                                            category = "Hogar",
                                            price = "80000"
                                        )
                                    },
                                    onApplyAiClick = {
                                        viewModel.onApplyAiSuggestion(
                                            title = "Plomería Residencial Certificada y Atención de Emergencias 24/7",
                                            desc = "Servicio profesional de plomería con 10 años de experiencia. Reparación de fugas, mantenimiento de tuberías y grifería con garantía de 6 meses por escrito.",
                                            category = "Hogar",
                                            price = "80000"
                                        )
                                    }
                                )

                                ServifyTextField(
                                    value = state.description,
                                    onValueChange = viewModel::onDescriptionChanged,
                                    label = "Descripción detallada del servicio",
                                    singleLine = false,
                                    isError = state.descriptionError != null,
                                    supportingText = state.descriptionError ?: "Describe tu experiencia, herramientas, horario y garantías."
                                )

                                PriceRangeFields(
                                    minPrice = state.price,
                                    onMinPriceChange = viewModel::onPriceChanged,
                                    maxPrice = state.price,
                                    onMaxPriceChange = viewModel::onPriceChanged
                                )

                                LocationPickerField(
                                    cityZone = state.locationName,
                                    onCityZoneChange = viewModel::onLocationChanged
                                )
                            }

                            3 -> {
                                Text(
                                    text = "Paso 3: Vista Previa y Verificación Final",
                                    style = ServifyTextStyle.TitleTight.copy(fontSize = 20.sp),
                                    color = MaterialTheme.colorScheme.onSurface
                                )

                                Text(
                                    text = "Revisa cómo verán los clientes tu anuncio en el Marketplace antes de publicarlo.",
                                    style = ServifyTextStyle.Small,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )

                                val previewState = CreatePublicationUiState(
                                    title = state.title,
                                    selectedCategory = state.categoryId,
                                    selectedSubcategory = state.subcategoryId,
                                    description = state.description,
                                    minPrice = state.price,
                                    maxPrice = state.price,
                                    cityZone = state.locationName
                                )

                                PublicationPreviewCard(state = previewState)

                                Card(
                                    shape = RoundedCornerShape(16.dp),
                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                                ) {
                                    Row(
                                        modifier = Modifier.padding(14.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Info,
                                            contentDescription = null,
                                            tint = MaterialTheme.colorScheme.primary,
                                            modifier = Modifier.size(20.dp)
                                        )
                                        Spacer(modifier = Modifier.width(10.dp))
                                        Text(
                                            text = "Tu anuncio cumplirá con las normas de convivencia de Servify y ganará puntos de reputación en tu perfil.",
                                            style = ServifyTextStyle.Caption,
                                            color = MaterialTheme.colorScheme.onSurface
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

@Preview(name = "Step 3 Preview Dark", showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun CreatePublicationPreviewStep3Dark() {
    ServifyTheme(darkTheme = true) {
        CreatePublicationScreen(
            onBackClick = {},
            onPublishSuccess = {}
        )
    }
}
