package co.edu.uniquindio.servify.features.home

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Tune
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import co.edu.uniquindio.servify.features.home.component.CategoryChipsRow
import co.edu.uniquindio.servify.features.home.component.HomeSearchBar
import co.edu.uniquindio.servify.features.home.component.HomeTopAppBar
import co.edu.uniquindio.servify.features.home.component.NextAppointmentBanner
import co.edu.uniquindio.servify.features.home.component.ServiceFeedCard
import co.edu.uniquindio.servify.features.home.model.HomeMockData
import co.edu.uniquindio.servify.ui.theme.ServifyOnSurface
import co.edu.uniquindio.servify.ui.theme.ServifyOnSurfaceVariant
import co.edu.uniquindio.servify.ui.theme.ServifyPrimary
import co.edu.uniquindio.servify.ui.theme.ServifySurface
import co.edu.uniquindio.servify.ui.theme.ServifySurfaceVariant
import co.edu.uniquindio.servify.ui.theme.ServifyTheme
import co.edu.uniquindio.servify.ui.theme.ServifyTextStyle
import androidx.compose.material.icons.Icons

@Composable
fun HomeScreen(
    onServiceClick: (String) -> Unit,
    onBookClick: (String) -> Unit,
    modifier: Modifier = Modifier,
    onNotificationClick: () -> Unit = {},
    onProfileClick: () -> Unit = {},
    onLocationClick: () -> Unit = {}
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategoryId by remember { mutableStateOf<String?>(null) }
    var selectedFilterChip by remember { mutableStateOf("Cerca de mí") }
    var isMapView by remember { mutableStateOf(false) }

    var servicesList by remember { mutableStateOf(HomeMockData.sampleServices) }

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
                    query = searchQuery,
                    onQueryChange = { searchQuery = it },
                    onFilterClick = { /* Abrir bottom sheet de filtros */ }
                )
            }

            // 2. CHIPS DE CATEGORÍAS
            item {
                CategoryChipsRow(
                    categories = HomeMockData.categories,
                    selectedCategoryId = selectedCategoryId,
                    onCategorySelect = { catId ->
                        selectedCategoryId = if (selectedCategoryId == catId) null else catId
                    }
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
                            val isSelected = selectedFilterChip == filterLabel
                            Box(
                                modifier = Modifier
                                    .clip(CircleShape)
                                    .background(
                                        if (isSelected) ServifyPrimary else ServifySurfaceVariant
                                    )
                                    .clickable { selectedFilterChip = filterLabel }
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
                                .background(if (!isMapView) MaterialTheme.colorScheme.surface else Color.Transparent)
                                .clickable { isMapView = false }
                                .padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.List,
                                    contentDescription = "Lista",
                                    tint = if (!isMapView) ServifyOnSurface else ServifyOnSurfaceVariant,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(3.dp))
                                Text(
                                    text = "Lista",
                                    style = ServifyTextStyle.Caption.copy(fontSize = 11.sp),
                                    color = if (!isMapView) ServifyOnSurface else ServifyOnSurfaceVariant
                                )
                            }
                        }

                        Box(
                            modifier = Modifier
                                .clip(CircleShape)
                                .background(if (isMapView) MaterialTheme.colorScheme.surface else Color.Transparent)
                                .clickable { isMapView = true }
                                .padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Map,
                                    contentDescription = "Mapa",
                                    tint = if (isMapView) ServifyOnSurface else ServifyOnSurfaceVariant,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(3.dp))
                                Text(
                                    text = "Mapa",
                                    style = ServifyTextStyle.Caption.copy(fontSize = 11.sp),
                                    color = if (isMapView) ServifyOnSurface else ServifyOnSurfaceVariant
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
                        text = "Servicios cerca de ti",
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

            // 6. FEED DE SERVICIOS
            items(servicesList, key = { it.id }) { serviceItem ->
                ServiceFeedCard(
                    service = serviceItem,
                    onClick = { onServiceClick(serviceItem.id) },
                    onFavoriteToggle = {
                        servicesList = servicesList.map { item ->
                            if (item.id == serviceItem.id) {
                                val newFav = !item.isFavorite
                                val countDiff = if (newFav) 1 else -1
                                item.copy(
                                    isFavorite = newFav,
                                    interestedCount = item.interestedCount + countDiff
                                )
                            } else item
                        }
                    },
                    onBookClick = { onBookClick(serviceItem.id) },
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
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
