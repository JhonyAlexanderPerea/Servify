package co.edu.uniquindio.servify.features.explore


import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.ui.BiasAlignment
import androidx.compose.ui.geometry.Offset
import co.edu.uniquindio.servify.ui.format.formatCop
import co.edu.uniquindio.servify.ui.motion.ServifyMotion
import co.edu.uniquindio.servify.ui.theme.ServifyExtra
import android.content.res.Configuration
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import co.edu.uniquindio.servify.domain.model.Publication
import co.edu.uniquindio.servify.features.home.component.CategoryChipsRow
import co.edu.uniquindio.servify.features.home.component.HomeSearchBar
import co.edu.uniquindio.servify.features.home.component.LevelBadge
import co.edu.uniquindio.servify.features.home.component.TrustIndexBadge
import co.edu.uniquindio.servify.features.home.model.toServiceItem
import co.edu.uniquindio.servify.ui.components.button.ServifyFilledButton
import co.edu.uniquindio.servify.ui.theme.ServifyOnSurface
import co.edu.uniquindio.servify.ui.theme.ServifyOnSurfaceVariant
import co.edu.uniquindio.servify.ui.theme.ServifyOutlineVariant
import co.edu.uniquindio.servify.ui.theme.ServifyPrimary
import co.edu.uniquindio.servify.ui.theme.ServifySurface
import co.edu.uniquindio.servify.ui.theme.ServifySurfaceVariant
import co.edu.uniquindio.servify.ui.theme.ServifyTertiary
import co.edu.uniquindio.servify.ui.theme.ServifyTheme
import co.edu.uniquindio.servify.ui.theme.ServifyTextStyle

@Composable
fun ExploreScreen(
    onServiceClick: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ExploreViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .background(ServifySurface)
            .statusBarsPadding()
            .navigationBarsPadding(),
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Text(
                    text = "Explorar Servicios en el Mapa",
                    style = ServifyTextStyle.TitleTight.copy(fontSize = 20.sp),
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = "Encuentra proveedores de confianza cerca de ti",
                    style = ServifyTextStyle.Small,
                    color = ServifyOnSurfaceVariant
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
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                // 1. BARRA DE BÚSQUEDA Y CATEGORÍAS
                HomeSearchBar(
                    query = state.searchQuery,
                    onQueryChange = viewModel::onSearchQueryChanged,
                    onFilterClick = {},
                    showFilterButton = false,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                CategoryChipsRow(
                    categories = state.categories,
                    selectedCategoryId = state.selectedCategoryId,
                    onCategorySelect = viewModel::onCategorySelected,
                    modifier = Modifier.padding(bottom = 10.dp)
                )

                // 2. CONTENEDOR PRINCIPAL DEL MAPA INTERACTIVO MOCK
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(horizontal = 16.dp)
                        .clip(RoundedCornerShape(24.dp))
                        .background(
                            brush = Brush.linearGradient(
                                colors = listOf(
                                    MaterialTheme.colorScheme.surfaceContainerHigh,
                                    MaterialTheme.colorScheme.surfaceContainerHighest
                                )
                            )
                        )
                ) {
                    MapBackdrop(modifier = Modifier.matchParentSize())

                    // Fondo estilizado de mapa urbano
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .clickable { viewModel.onDismissSelectedService() },
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = Icons.Default.Map,
                                contentDescription = "Mapa",
                                tint = ServifyPrimary.copy(alpha = 0.35f),
                                modifier = Modifier.size(72.dp)
                            )
                            Text(
                                text = "Mapa de Servicios Cercanos",
                                style = ServifyTextStyle.SmallMedium.copy(fontWeight = FontWeight.Bold),
                                color = ServifyOnSurfaceVariant
                            )
                            Text(
                                text = "${state.nearbyServices.size} servicios disponibles en la zona",
                                style = ServifyTextStyle.Caption,
                                color = ServifyOnSurfaceVariant
                            )
                        }
                    }

                    // Marcadores dinámicos e interactivos en el mapa
                    state.nearbyServices.forEachIndexed { index, publication ->
                        val isSelected = state.selectedService?.id == publication.id

                        ServiceMapMarker(
                            publication = publication,
                            isSelected = isSelected,
                            onClick = { viewModel.onMarkerSelected(publication) },
                            modifier = Modifier.align(MapPinSlots[index % MapPinSlots.size])
                        )
                    }

                    // Botón Mi Ubicación
                    Box(
                        modifier = Modifier
                            .padding(12.dp)
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.surface)
                            .border(1.dp, ServifyOutlineVariant, CircleShape)
                            .align(Alignment.TopEnd),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.MyLocation,
                            contentDescription = "Mi ubicación",
                            tint = ServifyPrimary,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    // 3. TARJETA FLOTANTE DEL SERVICIO SELECCIONADO
                    if (state.selectedService != null) {
                        state.selectedService?.let { selected ->
                            Column(modifier = Modifier.align(Alignment.BottomCenter)) {
                                AnimatedVisibility(
                                    visible = true,
                                    enter = fadeIn(tween(ServifyMotion.MEDIUM)) +
                                        slideInVertically(tween(ServifyMotion.LONG, easing = ServifyMotion.Decelerate)) { it / 4 }
                                ) {
                                    NearbyServiceFloatingCard(
                                        publication = selected,
                                        onClick = { onServiceClick(selected.id) },
                                        onCloseClick = { viewModel.onDismissSelectedService() },
                                        onFavoriteToggle = { viewModel.onToggleInterest(selected.id) },
                                        modifier = Modifier.padding(12.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // 4. CARRUSEL INFERIOR CUANDO NO HAY SERVICIO SELECCIONADO
                if (state.selectedService == null) {
                    Text(
                        text = "Proveedores destacados cerca (${state.nearbyServices.size})",
                        style = ServifyTextStyle.SmallMedium.copy(fontWeight = FontWeight.Bold),
                        color = ServifyOnSurface,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 2.dp)
                    )

                    LazyRow(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 12.dp),
                        contentPadding = PaddingValues(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(state.nearbyServices, key = { it.id }) { service ->
                            ExploreServiceMiniCard(
                                publication = service,
                                onClick = { viewModel.onMarkerSelected(service) },
                                modifier = Modifier.animateItem()
                            )
                        }
                    }
                }
            }
        }
    }
}

// -----------------------------------------------------------------------------
// MARCADOR INTERACTIVO EN EL MAPA (SERVICE MAP MARKER)
// -----------------------------------------------------------------------------

@Composable
private fun ServiceMapMarker(
    publication: Publication,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = MaterialTheme.colorScheme
    val markerBg by animateColorAsState(
        targetValue = if (isSelected) colors.primary else colors.surface,
        animationSpec = tween(ServifyMotion.MEDIUM),
        label = "markerBg"
    )
    val textColor = if (isSelected) colors.onPrimary else colors.onSurface
    val strokeColor = if (isSelected) colors.onPrimary else colors.primary

    Row(
        modifier = modifier
            .clip(CircleShape)
            .background(markerBg)
            .border(1.5.dp, strokeColor, CircleShape)
            .clickable(onClick = onClick)
            .padding(horizontal = 10.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Default.Place,
            contentDescription = null,
            tint = if (isSelected) colors.onPrimary else colors.primary,
            modifier = Modifier.size(16.dp)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = "${publication.subcategoryId ?: publication.title.take(12)} (${publication.provider.trustIndexScore}%)",
            style = ServifyTextStyle.Caption.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 11.sp
            ),
            color = textColor
        )
    }
}

// -----------------------------------------------------------------------------
// TARJETA FLOTANTE DEL SERVICIO SELECCIONADO (FLOATING CARD)
// -----------------------------------------------------------------------------

@Composable
private fun NearbyServiceFloatingCard(
    publication: Publication,
    onClick: () -> Unit,
    onCloseClick: () -> Unit,
    onFavoriteToggle: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        border = BorderStroke(1.dp, ServifyPrimary.copy(alpha = 0.3f))
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(ServifyPrimary),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = publication.provider.name.take(2).uppercase(),
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
                                text = publication.provider.name,
                                style = ServifyTextStyle.SmallMedium.copy(fontWeight = FontWeight.Bold),
                                color = ServifyOnSurface
                            )
                            if (publication.provider.isVerified) {
                                Spacer(modifier = Modifier.width(4.dp))
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = "Verificado",
                                    tint = ServifyTertiary,
                                    modifier = Modifier.size(14.dp)
                                )
                            }
                        }
                        LevelBadge(level = publication.provider.reputationLevel.name)
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(30.dp)
                            .clip(CircleShape)
                            .background(ServifySurfaceVariant)
                            .clickable(onClick = onFavoriteToggle),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (publication.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = "Favorito",
                            tint = if (publication.isFavorite) ServifyExtra.favorite else ServifyOnSurfaceVariant,
                            modifier = Modifier.size(16.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(6.dp))

                    Box(
                        modifier = Modifier
                            .size(30.dp)
                            .clip(CircleShape)
                            .background(ServifySurfaceVariant)
                            .clickable(onClick = onCloseClick),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Cerrar",
                            tint = ServifyOnSurfaceVariant,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = publication.title,
                style = ServifyTextStyle.SmallMedium.copy(fontWeight = FontWeight.Bold),
                color = ServifyOnSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(6.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${formatCop(publication.price)} COP / ${publication.priceUnit}",
                    style = ServifyTextStyle.SmallMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = ServifyPrimary
                    )
                )

                TrustIndexBadge(trustIndexPercent = publication.provider.trustIndexScore)
            }

            Spacer(modifier = Modifier.height(12.dp))

            ServifyFilledButton(
                text = "Ver Detalle del Servicio",
                icon = Icons.Default.ArrowForward,
                onClick = onClick,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

// -----------------------------------------------------------------------------
// MINI CARD DEL CARRUSEL INFERIOR
// -----------------------------------------------------------------------------

@Composable
private fun ExploreServiceMiniCard(
    publication: Publication,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val service = publication.toServiceItem()

    Card(
        modifier = modifier
            .width(260.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, ServifyOutlineVariant)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(service.providerColor),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = service.providerInitials,
                            style = ServifyTextStyle.Caption.copy(
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = service.providerName,
                        style = ServifyTextStyle.SmallMedium,
                        color = ServifyOnSurface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f, fill = false)
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))
                TrustIndexBadge(trustIndexPercent = service.trustIndex)
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = service.title,
                style = ServifyTextStyle.SmallMedium.copy(fontWeight = FontWeight.Bold),
                color = ServifyOnSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Text(
                text = "${service.priceRange} • ${service.city}",
                style = ServifyTextStyle.Caption,
                color = ServifyOnSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

// -----------------------------------------------------------------------------
// FONDO DEL MAPA (MOCK) Y POSICIONES DE MARCADORES
// -----------------------------------------------------------------------------

/** Casillas fijas y bien separadas: los marcadores ya no se pisan entre sí. */
private val MapPinSlots = listOf(
    BiasAlignment(-0.55f, -0.70f),
    BiasAlignment(0.50f, -0.30f),
    BiasAlignment(-0.40f, 0.10f),
    BiasAlignment(0.45f, 0.55f),
    BiasAlignment(-0.55f, 0.75f),
    BiasAlignment(0.10f, -0.90f),
    BiasAlignment(0.05f, 0.35f),
    BiasAlignment(-0.10f, -0.45f)
)

/** Cuadrícula de "calles" que usa el color del texto del tema: se ve bien en claro y en oscuro. */
@Composable
private fun MapBackdrop(modifier: Modifier = Modifier) {
    val line = MaterialTheme.colorScheme.onSurface
    Canvas(modifier = modifier) {
        val step = 56.dp.toPx()
        var x = step
        while (x < size.width) {
            drawLine(line.copy(alpha = 0.06f), Offset(x, 0f), Offset(x, size.height), strokeWidth = 1.dp.toPx())
            x += step
        }
        var y = step
        while (y < size.height) {
            drawLine(line.copy(alpha = 0.06f), Offset(0f, y), Offset(size.width, y), strokeWidth = 1.dp.toPx())
            y += step
        }
        // dos avenidas diagonales
        drawLine(line.copy(alpha = 0.10f), Offset(0f, size.height * 0.85f), Offset(size.width, size.height * 0.2f), strokeWidth = 5.dp.toPx())
        drawLine(line.copy(alpha = 0.08f), Offset(size.width * 0.2f, 0f), Offset(size.width * 0.8f, size.height), strokeWidth = 4.dp.toPx())
    }
}

@Preview(name = "Explore Screen Light", showBackground = true)
@Composable
fun ExploreScreenPreviewLight() {
    ServifyTheme(darkTheme = false) {
        ExploreScreen(onServiceClick = {})
    }
}

@Preview(name = "Explore Screen Dark", showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun ExploreScreenPreviewDark() {
    ServifyTheme(darkTheme = true) {
        ExploreScreen(onServiceClick = {})
    }
}
