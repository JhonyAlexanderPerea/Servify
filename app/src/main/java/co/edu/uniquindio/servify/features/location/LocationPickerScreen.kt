package co.edu.uniquindio.servify.features.location

import android.Manifest
import android.content.Context
import android.content.res.Configuration
import android.location.LocationManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.rounded.LocationOn
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import co.edu.uniquindio.servify.R
import co.edu.uniquindio.servify.domain.model.PickedLocation
import co.edu.uniquindio.servify.ui.components.button.ServifyFilledButton
import co.edu.uniquindio.servify.ui.theme.ServifyPrimary
import co.edu.uniquindio.servify.ui.theme.ServifyTextStyle
import co.edu.uniquindio.servify.ui.theme.ServifyTheme
import com.mapbox.geojson.Point
import com.mapbox.maps.extension.compose.MapboxMap
import com.mapbox.maps.extension.compose.animation.viewport.rememberMapViewportState
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterNotNull
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LocationPickerScreen(
    onBackClick: () -> Unit,
    onLocationConfirmed: (PickedLocation) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: LocationPickerViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val isInspection = LocalInspectionMode.current

    val mapViewportState = rememberMapViewportState {
        setCameraOptions {
            zoom(15.0)
            center(Point.fromLngLat(state.longitude, state.latitude))
        }
    }

    // Escuchar cambios de cámara del mapa
    LaunchedEffect(mapViewportState) {
        snapshotFlow { mapViewportState.cameraState?.center }
            .filterNotNull()
            .distinctUntilChanged()
            .collect { point ->
                viewModel.onCameraMoved(point.latitude(), point.longitude())
            }
    }

    // Función para centrar en la ubicación actual
    val centerOnMyLocation: () -> Unit = {
        try {
            val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as? LocationManager
            val gpsLoc = locationManager?.getLastKnownLocation(LocationManager.GPS_PROVIDER)
            val netLoc = locationManager?.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
            val currentLoc = gpsLoc ?: netLoc
            if (currentLoc != null) {
                mapViewportState.setCameraOptions {
                    center(Point.fromLngLat(currentLoc.longitude, currentLoc.latitude))
                    zoom(15.0)
                }
                viewModel.onCameraMoved(currentLoc.latitude, currentLoc.longitude)
            }
        } catch (_: SecurityException) {
            // Permiso no concedido
        }
    }

    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            centerOnMyLocation()
        }
    }

    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .statusBarsPadding()
            .navigationBarsPadding(),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.location_picker_title),
                        style = ServifyTextStyle.SmallMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.action_back),
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // 1. MAPA DE MAPBOX (O MOCK PARA PREVIEW)
            if (isInspection) {
                MapPreviewPlaceholder()
            } else {
                MapboxMap(
                    modifier = Modifier.fillMaxSize(),
                    mapViewportState = mapViewportState
                )
            }

            // 2. PIN CENTRAL FIJO CON SOMBRA/BASE
            Box(
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(bottom = 36.dp),
                contentAlignment = Alignment.Center
            ) {
                // Sombra / punto base
                Canvas(
                    modifier = Modifier
                        .size(16.dp)
                        .align(Alignment.BottomCenter)
                ) {
                    drawCircle(
                        color = Color.Black.copy(alpha = 0.3f),
                        radius = size.width / 2,
                        center = Offset(size.width / 2, size.height / 2)
                    )
                }

                // Ícono del pin de ubicación estilizado
                Icon(
                    imageVector = Icons.Rounded.LocationOn,
                    contentDescription = stringResource(R.string.location_picker_pin_cd),
                    tint = ServifyPrimary,
                    modifier = Modifier.size(48.dp)
                )
            }

            // 3. FAB "MI UBICACIÓN"
            FloatingActionButton(
                onClick = {
                    locationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(bottom = 180.dp, end = 16.dp),
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.primary,
                shape = CircleShape
            ) {
                Icon(
                    imageVector = Icons.Default.MyLocation,
                    contentDescription = stringResource(R.string.location_picker_my_location_cd)
                )
            }

            // 4. TARJETA INFERIOR CON INFORMACIÓN Y BOTÓN DE CONFIRMACIÓN
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .padding(16.dp),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = stringResource(R.string.location_picker_hint),
                        style = ServifyTextStyle.Caption,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primaryContainer),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Place,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        Column(modifier = Modifier.weight(1f)) {
                            if (state.isLoadingAddress) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(14.dp),
                                        strokeWidth = 2.dp,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = stringResource(R.string.location_picker_loading_address),
                                        style = ServifyTextStyle.Small,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            } else {
                                Text(
                                    text = state.address.ifBlank { stringResource(R.string.location_picker_loading_address) },
                                    style = ServifyTextStyle.SmallMedium.copy(fontWeight = FontWeight.Bold),
                                    color = MaterialTheme.colorScheme.onSurface,
                                    maxLines = 2
                                )
                            }

                            Text(
                                text = String.format(Locale.US, "%.5f, %.5f", state.latitude, state.longitude),
                                style = ServifyTextStyle.Caption,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    ServifyFilledButton(
                        text = stringResource(R.string.location_picker_confirm_button),
                        onClick = {
                            onLocationConfirmed(viewModel.getPickedLocation())
                        },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}

@Composable
private fun MapPreviewPlaceholder() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFE8ECEF)),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                imageVector = Icons.Default.Place,
                contentDescription = null,
                tint = Color.Gray,
                modifier = Modifier.size(64.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Mapbox Map View Preview",
                style = ServifyTextStyle.SmallMedium.copy(color = Color.Gray)
            )
        }
    }
}

@Preview(name = "Location Picker Light", showBackground = true)
@Composable
fun LocationPickerScreenPreviewLight() {
    ServifyTheme(darkTheme = false) {
        LocationPickerScreen(
            onBackClick = {},
            onLocationConfirmed = {}
        )
    }
}

@Preview(name = "Location Picker Dark", showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun LocationPickerScreenPreviewDark() {
    ServifyTheme(darkTheme = true) {
        LocationPickerScreen(
            onBackClick = {},
            onLocationConfirmed = {}
        )
    }
}
