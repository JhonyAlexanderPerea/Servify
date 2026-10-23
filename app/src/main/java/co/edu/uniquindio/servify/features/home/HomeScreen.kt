package co.edu.uniquindio.servify.features.home

import android.content.res.Configuration
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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.SearchOff
import androidx.compose.material.icons.filled.Tune
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import co.edu.uniquindio.servify.features.home.component.CategoryChipsRow
import co.edu.uniquindio.servify.features.home.component.HomeSearchBar
import co.edu.uniquindio.servify.features.home.component.HomeTopAppBar
import co.edu.uniquindio.servify.features.home.component.NextAppointmentBanner
import co.edu.uniquindio.servify.features.home.component.ServiceFeedCard
import co.edu.uniquindio.servify.features.home.model.HomeMockData
import co.edu.uniquindio.servify.features.home.model.toServiceItem
import co.edu.uniquindio.servify.ui.theme.ServifyOnSurface
import co.edu.uniquindio.servify.ui.theme.ServifyOnSurfaceVariant
import co.edu.uniquindio.servify.ui.theme.ServifyPrimary
import co.edu.uniquindio.servify.ui.theme.ServifySurface
import co.edu.uniquindio.servify.ui.theme.ServifySurfaceVariant
import co.edu.uniquindio.servify.ui.theme.ServifyTheme
import co.edu.uniquindio.servify.ui.theme.ServifyTextStyle

@Composable
fun HomeScreen(
    onServiceClick: (String) -> Unit,
    onBookClick: (String) -> Unit,
    modifier: Modifier = Modifier,
    onNotificationClick: () -> Unit = {},
    onProfileClick: () -> Unit = {},
    onLocationClick: () -> Unit = {},
    viewModel: HomeViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .background(ServifySurface)
            .statusBarsPadding()
            .navigationBarsPadding(),
        topBar = {
            HomeTopAppBar(
                userName = HomeMockData.currentUserName,
                userCity = HomeMockData.currentUserCity,
                userInitials = HomeMockData.currentUserInitials,
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
                CircularProgressIndicator(color = ServifyPrimary)
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentPadding = PaddingValues(bottom = 24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // 1. BARRA DE BÚSQUEDA
                item {
                    HomeSearchBar(
                        query = state.searchQuery,
                        onQueryChange = viewModel::onSearchQueryChanged,
                        onFilterClick = { /* Abrir bottom sheet de filtros */ }
                    )
                }

                // 2. CHIPS DE CATEGORÍAS
                item {
                    CategoryChipsRow(
                        categories = state.categories,
                        selectedCategoryId = state.selectedCategoryId,
                        onCategorySelect = viewModel::onCategorySelected
                    )
                }

                // 3. CHIPS DE FILTRADO RÁPIDO Y TOGGLE MAPA/LISTA
                item {
                    val filters = listOf("Cerca de mí", "Precio", "Mejor valorados", "Verificados")

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        LazyRow(
                            modifier = Modifier.weight(1f),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(filters) { filterLabel ->
                                val isSelected = state.selectedFilterChip == filterLabel
                                Box(
                                    modifier = Modifier
                                        .clip(CircleShape)
                                        .background(
                                            if (isSelected) ServifyPrimary else ServifySurfaceVariant
                                        )
                                        .clickable { viewModel.onFilterChipSelected(filterLabel) }
                                        .padding(horizontal = 12.dp, vertical = 6.dp)
                                ) {
                                    Text(
                                        text = filterLabel,
                                        style = ServifyTextStyle.Caption.copy(
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                        ),
                                        color = if (isSelected) Color.White else ServifyOnSurfaceVariant
                                    )
                                }
                            }
                        }

                        // Botón conmutador Lista / Mapa
                        Row(
                            modifier = Modifier
                                .clip(CircleShape)
                                .background(ServifySurfaceVariant)
                                .padding(2.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .clip(CircleShape)
                                    .background(if (!state.isMapView) MaterialTheme.colorScheme.surface else Color.Transparent)
                                    .clickable { viewModel.onToggleMapView(false) }
                                    .padding(horizontal = 10.dp, vertical = 4.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.List,
                                        contentDescription = "Lista",
                                        tint = if (!state.isMapView) ServifyOnSurface else ServifyOnSurfaceVariant,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(3.dp))
                                    Text(
                                        text = "Lista",
                                        style = ServifyTextStyle.Caption.copy(fontSize = 11.sp),
                                        color = if (!state.isMapView) ServifyOnSurface else ServifyOnSurfaceVariant
                                    )
                                }
                            }

                            Box(
                                modifier = Modifier
                                    .clip(CircleShape)
                                    .background(if (state.isMapView) MaterialTheme.colorScheme.surface else Color.Transparent)
                                    .clickable { viewModel.onToggleMapView(true) }
                                    .padding(horizontal = 10.dp, vertical = 4.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Map,
                                        contentDescription = "Mapa",
                                        tint = if (state.isMapView) ServifyOnSurface else ServifyOnSurfaceVariant,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(3.dp))
                                    Text(
                                        text = "Mapa",
                                        style = ServifyTextStyle.Caption.copy(fontSize = 11.sp),
                                        color = if (state.isMapView) ServifyOnSurface else ServifyOnSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                }

                // 4. BANNER DE PRÓXIMA CITA
                item {
                    NextAppointmentBanner(
                        appointment = HomeMockData.nextAppointment,
                        onClick = { onBookClick(HomeMockData.nextAppointment.id) }
                    )
                }

                // 5. ENCABEZADO DEL FEED
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Servicios cerca de ti (${state.filteredServices.size})",
                            style = ServifyTextStyle.TitleTight.copy(fontSize = 20.sp),
                            color = ServifyOnSurface
                        )

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .clip(CircleShape)
                                .clickable { /* Abrir modal filtros */ }
                                .padding(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Tune,
                                contentDescription = "Filtros",
                                tint = ServifyPrimary,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Filtros",
                                style = ServifyTextStyle.SmallMedium,
                                color = ServifyPrimary
                            )
                        }
                    }
                }

                // 6. ESTADO VACÍO O LISTA DE SERVICIOS
                if (state.filteredServices.isEmpty()) {
                    item {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 12.dp),
                            shape = RoundedCornerShape(20.dp),
                            colors = CardDefaults.cardColors(containerColor = ServifySurfaceVariant)
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
                                    tint = ServifyOnSurfaceVariant,
                                    modifier = Modifier.size(48.dp)
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                                Text(
                                    text = "No se encontraron servicios",
                                    style = ServifyTextStyle.SmallMedium.copy(fontWeight = FontWeight.Bold),
                                    color = ServifyOnSurface
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Intenta buscar con otros términos o borra los filtros de categoría.",
                                    style = ServifyTextStyle.Caption,
                                    color = ServifyOnSurfaceVariant,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                } else {
                    items(state.filteredServices, key = { it.id }) { publication ->
                        val serviceItem = publication.toServiceItem()
                        ServiceFeedCard(
                            service = serviceItem,
                            onClick = { onServiceClick(publication.id) },
                            onFavoriteToggle = {
                                viewModel.onToggleInterest(publication.id)
                            },
                            onBookClick = { onBookClick(publication.id) },
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )
                    }
                }
            }
        }
    }
}

// -----------------------------------------------------------------------------
// PREVIEWS EN MODO CLARO Y OSCURO
// -----------------------------------------------------------------------------

@Preview(name = "Home Screen Light", showBackground = true)
@Composable
fun HomeScreenPreviewLight() {
    ServifyTheme(darkTheme = false) {
        HomeScreen(
            onServiceClick = {},
            onBookClick = {}
        )
    }
}

@Preview(name = "Home Screen Dark", showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun HomeScreenPreviewDark() {
    ServifyTheme(darkTheme = true) {
        HomeScreen(
            onServiceClick = {},
            onBookClick = {}
        )
    }
}
