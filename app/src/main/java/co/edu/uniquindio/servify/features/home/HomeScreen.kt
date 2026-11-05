package co.edu.uniquindio.servify.features.home

import android.content.res.Configuration
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.SearchOff
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import co.edu.uniquindio.servify.features.home.component.CategoryChipsRow
import co.edu.uniquindio.servify.features.home.component.HomeFilterSheet
import co.edu.uniquindio.servify.features.home.component.HomeSearchBar
import co.edu.uniquindio.servify.features.home.component.HomeTopAppBar
import co.edu.uniquindio.servify.features.home.component.ServiceFeedCard
import co.edu.uniquindio.servify.features.home.model.HomeMockData
import co.edu.uniquindio.servify.features.home.model.toServiceItem
import co.edu.uniquindio.servify.ui.components.chips.ServifyChip
import co.edu.uniquindio.servify.ui.motion.ServifyMotion
import co.edu.uniquindio.servify.ui.theme.ServifyTextStyle
import co.edu.uniquindio.servify.ui.theme.ServifyTheme

@Composable
fun HomeScreen(
    onServiceClick: (String) -> Unit,
    onBookClick: (String) -> Unit,
    modifier: Modifier = Modifier,
    onNotificationClick: () -> Unit = {},
    onProfileClick: () -> Unit = {},
    onLocationClick: () -> Unit = {},
    onMapClick: () -> Unit = {},
    viewModel: HomeViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    var showFilters by remember { mutableStateOf(false) }
    val colors = MaterialTheme.colorScheme

    // Solo la barra de estado: el Scaffold del dashboard ya reserva la barra inferior.
    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .background(colors.background)
            .statusBarsPadding(),
        containerColor = colors.background,
        topBar = {
            HomeTopAppBar(
                userName = state.userName.ifBlank { HomeMockData.currentUserName },
                userCity = HomeMockData.currentUserCity,
                userInitials = state.userInitials.ifBlank { HomeMockData.currentUserInitials },
                hasUnreadNotifications = state.unreadCount > 0,
                onLocationClick = onLocationClick,
                onNotificationClick = onNotificationClick,
                onProfileClick = onProfileClick
            )
        }
    ) { paddingValues ->

        if (state.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = colors.primary)
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentPadding = PaddingValues(bottom = 24.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // 1. BÚSQUEDA (el botón de filtros abre la hoja de filtros)
                item(key = "search") {
                    HomeSearchBar(
                        query = state.searchQuery,
                        onQueryChange = viewModel::onSearchQueryChanged,
                        onFilterClick = { showFilters = true }
                    )
                }

                // 2. CATEGORÍAS
                item(key = "categories") {
                    CategoryChipsRow(
                        categories = state.categories,
                        selectedCategoryId = state.selectedCategoryId,
                        onCategorySelect = viewModel::onCategorySelected
                    )
                }

                // 3. FILTROS RÁPIDOS (una sola fila de chips; el selector Lista/Mapa pasó al encabezado
                //    del feed: ya no compite por el ancho ni recorta "Mejor valorados")
                item(key = "quick_filters") {
                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(HomeQuickFilters) { label ->
                            ServifyChip(
                                label = label,
                                selected = state.selectedFilterChip == label,
                                onClick = { viewModel.onFilterChipSelected(label) }
                            )
                        }
                    }
                }

                // 4. ENCABEZADO DEL FEED + SELECTOR LISTA / MAPA
                item(key = "feed_header") {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Servicios cerca de ti (${state.filteredServices.size})",
                            style = ServifyTextStyle.TitleTight.copy(fontSize = 20.sp),
                            color = colors.onBackground,
                            modifier = Modifier.weight(1f)
                        )
                        ViewModeToggle(isMapView = false, onMapClick = onMapClick)
                    }
                }

                // 5. ESTADO VACÍO O LISTA
                if (state.filteredServices.isEmpty()) {
                    item(key = "empty") {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 12.dp),
                            shape = RoundedCornerShape(20.dp),
                            colors = CardDefaults.cardColors(containerColor = colors.surfaceContainerHigh)
                        ) {
                            Column(
                                modifier = Modifier
                                    .padding(24.dp)
                                    .fillMaxWidth(),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(
                                    imageVector = Icons.Default.SearchOff,
                                    contentDescription = null,
                                    tint = colors.onSurfaceVariant,
                                    modifier = Modifier.size(48.dp)
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                                Text(
                                    text = "No se encontraron servicios",
                                    style = ServifyTextStyle.SmallMedium.copy(fontWeight = FontWeight.Bold),
                                    color = colors.onSurface
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Intenta con otros términos o restablece los filtros.",
                                    style = ServifyTextStyle.Caption,
                                    color = colors.onSurfaceVariant,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                } else {
                    items(state.filteredServices, key = { it.id }) { publication ->
                        ServiceFeedCard(
                            service = publication.toServiceItem(),
                            onClick = { onServiceClick(publication.id) },
                            onFavoriteToggle = { viewModel.onToggleInterest(publication.id) },
                            onBookClick = { onBookClick(publication.id) },
                            modifier = Modifier
                                .animateItem()
                                .padding(horizontal = 16.dp)
                        )
                    }
                }
            }
        }
    }

    if (showFilters) {
        HomeFilterSheet(
            options = HomeQuickFilters,
            selected = state.selectedFilterChip,
            onSelect = viewModel::onFilterChipSelected,
            onClear = viewModel::onClearFilters,
            onDismiss = { showFilters = false }
        )
    }
}

/**
 * Selector Lista / Mapa. Home siempre es la lista; "Mapa" lleva a la pestaña Explorar
 * (antes cambiaba un estado que ninguna vista usaba, así que no hacía nada visible).
 */
@Composable
private fun ViewModeToggle(isMapView: Boolean, onMapClick: () -> Unit) {
    val colors = MaterialTheme.colorScheme
    Row(
        modifier = Modifier
            .clip(CircleShape)
            .background(colors.surfaceContainerHigh)
            .padding(3.dp),
        horizontalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        ViewModeOption(selected = !isMapView, label = "Vista de lista", onClick = {}) {
            Icon(Icons.AutoMirrored.Filled.List, contentDescription = null, modifier = Modifier.size(18.dp), tint = it)
        }
        ViewModeOption(selected = isMapView, label = "Ver en el mapa", onClick = onMapClick) {
            Icon(Icons.Default.Map, contentDescription = null, modifier = Modifier.size(18.dp), tint = it)
        }
    }
}

@Composable
private fun ViewModeOption(
    selected: Boolean,
    label: String,
    onClick: () -> Unit,
    icon: @Composable (tint: androidx.compose.ui.graphics.Color) -> Unit
) {
    val colors = MaterialTheme.colorScheme
    val container by animateColorAsState(
        targetValue = if (selected) colors.primary else androidx.compose.ui.graphics.Color.Transparent,
        animationSpec = tween(ServifyMotion.MEDIUM),
        label = "viewModeContainer"
    )
    val content by animateColorAsState(
        targetValue = if (selected) colors.onPrimary else colors.onSurfaceVariant,
        animationSpec = tween(ServifyMotion.MEDIUM),
        label = "viewModeContent"
    )
    Box(
        modifier = Modifier
            .size(width = 44.dp, height = 36.dp)
            .clip(CircleShape)
            .background(container)
            .clickable(role = Role.Button, onClickLabel = label, onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        icon(content)
    }
}

// -----------------------------------------------------------------------------
// PREVIEWS EN MODO CLARO Y OSCURO
// -----------------------------------------------------------------------------

@Preview(name = "Home Screen Light", showBackground = true)
@Composable
fun HomeScreenPreviewLight() {
    ServifyTheme(darkTheme = false) {
        HomeScreen(onServiceClick = {}, onBookClick = {})
    }
}

@Preview(name = "Home Screen Dark", showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun HomeScreenPreviewDark() {
    ServifyTheme(darkTheme = true) {
        HomeScreen(onServiceClick = {}, onBookClick = {})
    }
}
