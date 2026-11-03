package co.edu.uniquindio.servify.features.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
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
import co.edu.uniquindio.servify.features.profile.ProfileActions
import co.edu.uniquindio.servify.features.profile.ProfileScreen
import co.edu.uniquindio.servify.features.statistics.PersonalStatisticsScreen
import co.edu.uniquindio.servify.features.publication.create.CreatePublicationScreen
import co.edu.uniquindio.servify.features.publication.mine.MyPublicationsActions
import co.edu.uniquindio.servify.features.publication.mine.MyPublicationsScreen
import co.edu.uniquindio.servify.navigation.BottomNavDestination
import co.edu.uniquindio.servify.navigation.Screen
import co.edu.uniquindio.servify.ui.components.state.ComingSoonScreen

@Composable
fun MainScreen(
    onNavigateToServiceDetail: (String) -> Unit,
    onNavigateToEditProfile: () -> Unit = {},
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
            .background(MaterialTheme.colorScheme.background),
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
                    actions = ProfileActions(
                        onMyPublicationsClick = { dashboardNavController.navigate(Screen.MyPublications.route) },
                        onMyRequestsClick = { dashboardNavController.navigate(Screen.MyRequests.route) },
                        onAchievementsClick = { dashboardNavController.navigate(Screen.Achievements.route) },
                        onStatisticsClick = { dashboardNavController.navigate(Screen.Statistics.route) },
                        onSettingsClick = onNavigateToEditProfile,
                        onLogoutClick = onLogoutClick,
                        onNavigateToEditProfile = onNavigateToEditProfile,
                        onNavigateToStatistics = { dashboardNavController.navigate(Screen.Statistics.route) }
                    )
                )
            }

            // 6. MIS PUBLICACIONES (pantalla 15)
            composable(Screen.MyPublications.route) {
                MyPublicationsScreen(
                    actions = MyPublicationsActions(
                        onBackClick = { dashboardNavController.popBackStack() },
                        onViewClick = onNavigateToServiceDetail,
                        // TODO(Jean, tarea 15): abrir el formulario en modo edición con el id de la publicación
                        onEditClick = { dashboardNavController.navigate(Screen.CreatePublication.route) },
                        onCreateClick = { dashboardNavController.navigate(Screen.CreatePublication.route) }
                    )
                )
            }

            // 7. SECCIONES DEL PERFIL PENDIENTES (tareas 19-20, 24-26 del kanban)
            composable(Screen.MyRequests.route) {
                ComingSoonScreen(
                    title = "Mis solicitudes",
                    description = "Aquí verás las solicitudes de servicio que enviaste y su estado.",
                    onBack = { dashboardNavController.popBackStack() }
                )
            }
            composable(Screen.Achievements.route) {
                ComingSoonScreen(
                    title = "Mis logros",
                    description = "Aquí verás tus insignias desbloqueadas y tu progreso hacia las siguientes.",
                    onBack = { dashboardNavController.popBackStack() }
                )
            }
            composable(Screen.Statistics.route) {
                PersonalStatisticsScreen(
                    onBackClick = {
                        dashboardNavController.popBackStack()
                    }
                )
            }
            composable(Screen.Settings.route) {
                ComingSoonScreen(
                    title = "Configuración",
                    description = "Aquí podrás editar tus datos personales y eliminar tu cuenta.",
                    onBack = { dashboardNavController.popBackStack() }
                )
            }
        }
    }
}
