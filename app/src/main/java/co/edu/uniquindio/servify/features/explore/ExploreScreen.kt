package co.edu.uniquindio.servify.features.explore

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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.HomeRepairService
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import co.edu.uniquindio.servify.features.home.component.TrustIndexBadge
import co.edu.uniquindio.servify.features.home.model.HomeMockData
import co.edu.uniquindio.servify.features.home.model.ServiceItem
import co.edu.uniquindio.servify.ui.theme.ServifyGold
import co.edu.uniquindio.servify.ui.theme.ServifyOnSurface
import co.edu.uniquindio.servify.ui.theme.ServifyOnSurfaceVariant
import co.edu.uniquindio.servify.ui.theme.ServifyOutlineVariant
import co.edu.uniquindio.servify.ui.theme.ServifyPrimary
import co.edu.uniquindio.servify.ui.theme.ServifyPrimaryContainer
import co.edu.uniquindio.servify.ui.theme.ServifySurface
import co.edu.uniquindio.servify.ui.theme.ServifySurfaceVariant
import co.edu.uniquindio.servify.ui.theme.ServifyTextStyle
import co.edu.uniquindio.servify.ui.theme.ServifyTheme

@Composable
fun ExploreScreen(
    onServiceClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("Todos") }
    val categories = listOf("Todos", "Hogar", "Educación", "Mascotas", "Tecnología")

    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .background(ServifySurface),
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                Text(
                    text = "Explorar Servicios en el Mapa",
                    style = ServifyTextStyle.TitleTight.copy(fontSize = 22.sp),
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

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Categorías horizontales
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 10.dp),
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(categories) { cat ->
                    val isSelected = selectedCategory == cat
                    Box(
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(if (isSelected) ServifyPrimary else ServifySurfaceVariant)
                            .clickable { selectedCategory = cat }
                            .padding(horizontal = 14.dp, vertical = 8.dp)
                    ) {
                        Text(
                            text = cat,
                            style = ServifyTextStyle.Caption.copy(
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                            ),
                            color = if (isSelected) Color.White else ServifyOnSurfaceVariant
                        )
                    }
                }
            }

            // CONTENEDOR DEL MAPA INTERACTIVO MOCK
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(horizontal = 16.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .background(
                        brush = Brush.linearGradient(
                            colors = listOf(Color(0xFFE2E8F0), Color(0xFFCBD5E1))
                        )
                    )
            ) {
                // Falso fondo estilizado de mapa urbano
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.Map,
                            contentDescription = "Mapa",
                            tint = ServifyPrimary.copy(alpha = 0.4f),
                            modifier = Modifier.size(80.dp)
                        )
                        Text(
                            text = "Mapa Interactivo de Servicios",
                            style = ServifyTextStyle.SmallMedium.copy(fontWeight = FontWeight.Bold),
                            color = ServifyOnSurfaceVariant
                        )
                        Text(
                            text = "Armenia y Eje Cafetero",
                            style = ServifyTextStyle.Caption,
                            color = ServifyOnSurfaceVariant
                        )
                    }
                }

                // Pines flotantes sobre el mapa
                MapPinOverlay(
                    title = "Plomería (94%)",
                    color = ServifyPrimary,
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(top = 40.dp, start = 40.dp)
                )

                MapPinOverlay(
                    title = "Electricista (91%)",
                    color = Color(0xFF7B2FBE),
                    modifier = Modifier
                        .align(Alignment.Center)
                )

                MapPinOverlay(
                    title = "Clases (98%)",
                    color = Color(0xFFEA4C89),
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(bottom = 60.dp, end = 50.dp)
                )

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
            }

            Spacer(modifier = Modifier.height(12.dp))

            // CARRUSEL INFERIOR DE PROVEEDORES CERCANOS
            Text(
                text = "Proveedores destacados a menos de 5 km",
                style = ServifyTextStyle.SmallMedium.copy(fontWeight = FontWeight.Bold),
                color = ServifyOnSurface,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
            )

            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(HomeMockData.sampleServices) { service ->
                    ExploreServiceCard(
                        service = service,
                        onClick = { onServiceClick(service.id) }
                    )
                }
            }
        }
    }
}

@Composable
private fun MapPinOverlay(
    title: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .clip(CircleShape)
            .background(Color.White)
            .border(1.5.dp, color, CircleShape)
            .padding(horizontal = 10.dp, vertical = 5.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Default.Place,
            contentDescription = null,
            tint = color,
            modifier = Modifier.size(16.dp)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = title,
            style = ServifyTextStyle.Caption.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 11.sp
            ),
            color = ServifyOnSurface
        )
    }
}

@Composable
private fun ExploreServiceCard(
    service: ServiceItem,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
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
                Row(verticalAlignment = Alignment.CenterVertically) {
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
                        overflow = TextOverflow.Ellipsis
                    )
                }

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
