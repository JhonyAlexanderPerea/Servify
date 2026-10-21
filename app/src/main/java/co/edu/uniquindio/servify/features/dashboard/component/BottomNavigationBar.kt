package co.edu.uniquindio.servify.features.dashboard.component

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import co.edu.uniquindio.servify.navigation.BottomNavDestination
import co.edu.uniquindio.servify.ui.theme.ServifyOnSurfaceVariant
import co.edu.uniquindio.servify.ui.theme.ServifyPrimary
import co.edu.uniquindio.servify.ui.theme.ServifyPrimaryContainer
import co.edu.uniquindio.servify.ui.theme.ServifyTheme
import co.edu.uniquindio.servify.ui.theme.ServifyTextStyle

@Composable
fun BottomNavigationBar(
    currentRoute: String?,
    onDestinationSelect: (BottomNavDestination) -> Unit,
    modifier: Modifier = Modifier
) {
    NavigationBar(
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.surface,
        contentColor = MaterialTheme.colorScheme.onSurface,
        tonalElevation = 8.dp
    ) {
        BottomNavDestination.items.forEach { destination ->
            val isSelected = currentRoute == destination.route

            if (destination.isCentralAction) {
                // PESTAÑA CENTRAL DESTACADA (BOTÓN "+ PUBLICAR")
                NavigationBarItem(
                    selected = isSelected,
                    onClick = { onDestinationSelect(destination) },
                    icon = {
                        Box(
                            modifier = Modifier
                                .size(42.dp)
                                .clip(CircleShape)
                                .background(ServifyPrimary),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = if (isSelected) destination.selectedIcon else destination.icon,
                                contentDescription = destination.label,
                                tint = Color.White,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    },
                    label = {
                        Text(
                            text = destination.label,
                            style = ServifyTextStyle.Caption.copy(
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            ),
                            color = ServifyPrimary
                        )
                    },
                    colors = NavigationBarItemDefaults.colors(
                        indicatorColor = Color.Transparent
                    )
                )
            } else {
                // PESTAÑAS ESTÁNDAR
                NavigationBarItem(
                    selected = isSelected,
                    onClick = { onDestinationSelect(destination) },
                    icon = {
                        if (destination.badgeCount != null && destination.badgeCount > 0) {
                            BadgedBox(
                                badge = {
                                    Badge(
                                        containerColor = Color(0xFFBA1A1A),
                                        contentColor = Color.White
                                    ) {
                                        Text(text = "${destination.badgeCount}")
                                    }
                                }
                            ) {
                                Icon(
                                    imageVector = if (isSelected) destination.selectedIcon else destination.icon,
                                    contentDescription = destination.label,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                        } else {
                            Icon(
                                imageVector = if (isSelected) destination.selectedIcon else destination.icon,
                                contentDescription = destination.label,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                    },
                    label = {
                        Text(
                            text = destination.label,
                            style = ServifyTextStyle.Caption.copy(
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                            )
                        )
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = ServifyPrimary,
                        selectedTextColor = ServifyPrimary,
                        indicatorColor = ServifyPrimaryContainer,
                        unselectedIconColor = ServifyOnSurfaceVariant,
                        unselectedTextColor = ServifyOnSurfaceVariant
                    )
                )
            }
        }
    }
}

// -----------------------------------------------------------------------------
// PREVIEWS DE LA BARRA INFERIOR DE NAVEGACIÓN
// -----------------------------------------------------------------------------

@Preview(name = "Bottom Nav Bar Light", showBackground = true)
@Composable
fun BottomNavigationBarPreviewLight() {
    ServifyTheme(darkTheme = false) {
        BottomNavigationBar(
            currentRoute = BottomNavDestination.Home.route,
            onDestinationSelect = {}
        )
    }
}

@Preview(name = "Bottom Nav Bar Dark", showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun BottomNavigationBarPreviewDark() {
    ServifyTheme(darkTheme = true) {
        BottomNavigationBar(
            currentRoute = BottomNavDestination.Home.route,
            onDestinationSelect = {}
        )
    }
}
