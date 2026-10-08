package co.edu.uniquindio.servify.features.dashboard

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import co.edu.uniquindio.servify.features.dashboard.component.BottomNavigationBar
import co.edu.uniquindio.servify.features.explore.ExploreScreen
import co.edu.uniquindio.servify.features.home.HomeScreen
import co.edu.uniquindio.servify.features.notifications.NotificationsScreen
import co.edu.uniquindio.servify.features.profile.ProfileScreen
import co.edu.uniquindio.servify.features.publication.create.CreatePublicationScreen
import co.edu.uniquindio.servify.navigation.BottomNavDestination
import co.edu.uniquindio.servify.navigation.Screen
import co.edu.uniquindio.servify.ui.theme.ServifySurface
import co.edu.uniquindio.servify.ui.theme.ServifyTheme

@Composable
fun MainScreen(
    onNavigateToServiceDetail: (String) -> Unit,
    onLogoutClick: () -> Unit = {},
    modifier: Modifier = Modifier,
    dashboardNavController: NavHostController = rememberNavController()
) {
    val navBackStackEntry by dashboardNavController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    // Visibilidad condicional de la barra inferior (mostrar solo en pestañas principales)
    val showBottomBar = currentRoute in BottomNavDestination.items.map { it.route }

    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .background(ServifySurface),
        bottomBar = {
            if (showBottomBar) {
                BottomNavigationBar(
                    currentRoute = currentRoute,
                    onDestinationSelect = { destination ->
                        dashboardNavController.navigate(destination.route) {
                            popUpTo(dashboardNavController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }
        }
    ) { paddingValues ->

        NavHost(
            navController = dashboardNavController,
            startDestination = Screen.Home.route,
            modifier = Modifier.padding(paddingValues)
        ) {
            // 1. INICIO / FEED DE SERVICIOS
            composable(Screen.Home.route) {
                HomeScreen(
                    onServiceClick = { serviceId ->
                        onNavigateToServiceDetail(serviceId)
                    },
                    onBookClick = { serviceId ->
                        onNavigateToServiceDetail(serviceId)
                    },
                    onNotificationClick = {
                        dashboardNavController.navigate(Screen.Notifications.route) {
                            popUpTo(dashboardNavController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    onProfileClick = {
                        dashboardNavController.navigate(Screen.Profile.route) {
                            popUpTo(dashboardNavController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }

            // 2. EXPLORAR / MAPA
            composable(Screen.Explore.route) {
                ExploreScreen(
                    onServiceClick = { serviceId ->
                        onNavigateToServiceDetail(serviceId)
                    }
                )
            }

            // 3. CREAR PUBLICACIÓN DE SERVICIO (+ IA)
            composable(Screen.CreatePublication.route) {
                CreatePublicationScreen(
                    onBackClick = {
                        dashboardNavController.popBackStack()
                    },
                    onPublishSuccess = {
                        dashboardNavController.navigate(Screen.Home.route) {
                            popUpTo(dashboardNavController.graph.findStartDestination().id) {
                                inclusive = true
                            }
                        }
                    }
                )
            }

            // 4. ALERTAS / NOTIFICACIONES
            composable(Screen.Notifications.route) {
                NotificationsScreen()
            }

            // 5. PERFIL & REPUTACIÓN
            composable(Screen.Profile.route) {
                ProfileScreen(
                    onNavigateToCreatePublication = {
                        dashboardNavController.navigate(Screen.CreatePublication.route)
                    },
                    onLogoutClick = onLogoutClick
                )
            }
        }
    }
}

// -----------------------------------------------------------------------------
// PREVIEWS DE MAIN SCREEN
// -----------------------------------------------------------------------------

@Preview(name = "Main Screen Light", showBackground = true)
@Composable
fun MainScreenPreviewLight() {
    ServifyTheme(darkTheme = false) {
        MainScreen(
            onNavigateToServiceDetail = {}
        )
    }
}

@Preview(name = "Main Screen Dark", showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun MainScreenPreviewDark() {
    ServifyTheme(darkTheme = true) {
        MainScreen(
            onNavigateToServiceDetail = {}
        )
    }
}
