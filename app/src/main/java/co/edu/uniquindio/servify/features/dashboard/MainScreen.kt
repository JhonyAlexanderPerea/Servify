package co.edu.uniquindio.servify.features.dashboard

import androidx.compose.runtime.LaunchedEffect
import co.edu.uniquindio.servify.features.request.mine.MyRequestsScreen
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
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
import co.edu.uniquindio.servify.features.profile.achievements.AchievementsScreen
import co.edu.uniquindio.servify.features.publication.create.CreatePublicationScreen
import co.edu.uniquindio.servify.features.publication.mine.MyPublicationsActions
import co.edu.uniquindio.servify.features.publication.mine.MyPublicationsScreen
import co.edu.uniquindio.servify.features.statistics.PersonalStatisticsScreen
import co.edu.uniquindio.servify.navigation.BottomNavDestination
import co.edu.uniquindio.servify.navigation.Screen
import co.edu.uniquindio.servify.ui.motion.isTabSwitch
import co.edu.uniquindio.servify.ui.motion.servifyEnter
import co.edu.uniquindio.servify.ui.motion.servifyExit
import co.edu.uniquindio.servify.ui.motion.servifyPopEnter
import co.edu.uniquindio.servify.ui.motion.servifyPopExit
import co.edu.uniquindio.servify.ui.motion.tabEnter
import co.edu.uniquindio.servify.ui.motion.tabExit

@Composable
fun MainScreen(
    onNavigateToServiceDetail: (String) -> Unit,
    onNavigateToEditProfile: () -> Unit = {},
    onNavigateToPublicationPublished: (publicationId: String) -> Unit = {},
    /** Ruta del dashboard que debe abrirse al volver (p. ej. Mis publicaciones tras "Publicación enviada"). */
    pendingRoute: String? = null,
    onPendingRouteConsumed: () -> Unit = {},
    modifier: Modifier = Modifier,
    dashboardNavController: NavHostController = rememberNavController(),
    dashboardViewModel: DashboardViewModel = hiltViewModel()
) {
    val navBackStackEntry by dashboardNavController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val unreadCount by dashboardViewModel.unreadCount.collectAsStateWithLifecycle()

    val tabRoutes = BottomNavDestination.items.map { it.route }.toSet()
    val showBottomBar = currentRoute in tabRoutes

    // Cambia a una pestaña conservando su estado (misma política que la barra inferior).
    fun navigateToTab(route: String) {
        dashboardNavController.navigate(route) {
            popUpTo(dashboardNavController.graph.findStartDestination().id) { saveState = true }
            launchSingleTop = true
            restoreState = true
        }
    }

    // Las pantallas de nivel superior (Publicación enviada, Solicitud confirmada) pueden pedir
    // que el dashboard abra una sección concreta al volver.
    LaunchedEffect(pendingRoute) {
        pendingRoute?.let { route ->
            dashboardNavController.navigate(route) { launchSingleTop = true }
            onPendingRouteConsumed()
        }
    }

    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        // Cada pantalla ya aplica su propio statusBarsPadding(): si el Scaffold también reservara
        // el inset superior, se veía una franja vacía duplicada bajo la barra de estado.
        contentWindowInsets = WindowInsets(0.dp, 0.dp, 0.dp, 0.dp),
        bottomBar = {
            if (showBottomBar) {
                BottomNavigationBar(
                    currentRoute = currentRoute,
                    notificationBadgeCount = unreadCount,
                    onDestinationSelect = { destination -> navigateToTab(destination.route) }
                )
            }
        }
    ) { paddingValues ->

        NavHost(
            navController = dashboardNavController,
            startDestination = Screen.Home.route,
            // Marca como consumido el espacio de la barra inferior para que los
            // navigationBarsPadding() internos no vuelvan a sumarlo.
            modifier = Modifier
                .padding(paddingValues)
                .consumeWindowInsets(paddingValues),
            enterTransition = {
                if (isTabSwitch(initialState.destination.route, targetState.destination.route, tabRoutes)) {
                    tabEnter()
                } else servifyEnter()
            },
            exitTransition = {
                if (isTabSwitch(initialState.destination.route, targetState.destination.route, tabRoutes)) {
                    tabExit()
                } else servifyExit()
            },
            popEnterTransition = {
                if (isTabSwitch(initialState.destination.route, targetState.destination.route, tabRoutes)) {
                    tabEnter()
                } else servifyPopEnter()
            },
            popExitTransition = {
                if (isTabSwitch(initialState.destination.route, targetState.destination.route, tabRoutes)) {
                    tabExit()
                } else servifyPopExit()
            }
        ) {
            // 1. INICIO / FEED DE SERVICIOS
            composable(Screen.Home.route) {
                HomeScreen(
                    onServiceClick = onNavigateToServiceDetail,
                    onBookClick = onNavigateToServiceDetail,
                    onNotificationClick = { navigateToTab(Screen.Notifications.route) },
                    onProfileClick = { navigateToTab(Screen.Profile.route) },
                    onMapClick = { navigateToTab(Screen.Explore.route) }
                )
            }

            // 2. EXPLORAR / MAPA
            composable(Screen.Explore.route) {
                ExploreScreen(onServiceClick = onNavigateToServiceDetail)
            }

            // 3. CREAR PUBLICACIÓN DE SERVICIO (+ IA) → "Publicación enviada"
            composable(Screen.CreatePublication.route) {
                CreatePublicationScreen(
                    onBackClick = { dashboardNavController.popBackStack() },
                    onPublishSuccess = { publicationId ->
                        // Se saca el formulario de la pila: al volver de "Publicación enviada" no debe
                        // reaparecer en estado de éxito.
                        dashboardNavController.navigate(Screen.Home.route) {
                            popUpTo(dashboardNavController.graph.findStartDestination().id) { inclusive = false }
                            launchSingleTop = true
                        }
                        onNavigateToPublicationPublished(publicationId)
                    }
                )
            }

            // 4. ALERTAS / NOTIFICACIONES (tocar una notificación abre su pantalla relacionada)
            composable(Screen.Notifications.route) {
                NotificationsScreen(
                    onNavigateToRoute = { route ->
                        when {
                            route.startsWith("service_detail/") ->
                                onNavigateToServiceDetail(route.removePrefix("service_detail/"))

                            route == Screen.MyRequests.route ||
                                route == Screen.MyPublications.route ||
                                route == Screen.Achievements.route -> dashboardNavController.navigate(route)

                            else -> Unit
                        }
                    }
                )
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
                        onNavigateToEditProfile = onNavigateToEditProfile,
                        onNavigateToStatistics = { dashboardNavController.navigate(Screen.Statistics.route) }
                    )
                )
            }

            // 5.1 EDITAR / REENVIAR PUBLICACIÓN (reutiliza el formulario de crear)
            composable(Screen.EditPublication.route) {
                CreatePublicationScreen(
                    onBackClick = { dashboardNavController.popBackStack() },
                    onPublishSuccess = {
                        dashboardNavController.popBackStack(Screen.MyPublications.route, inclusive = false)
                    }
                )
            }

            // 6. MIS PUBLICACIONES (pantalla 15)
            composable(Screen.MyPublications.route) {
                MyPublicationsScreen(
                    actions = MyPublicationsActions(
                        onBackClick = { dashboardNavController.popBackStack() },
                        onViewClick = onNavigateToServiceDetail,
                        onEditClick = { publicationId ->
                            dashboardNavController.navigate(Screen.EditPublication.createRoute(publicationId))
                        },
                        onCreateClick = { dashboardNavController.navigate(Screen.CreatePublication.route) }
                    )
                )
            }

            // 7. SECCIONES DEL PERFIL
            composable(Screen.MyRequests.route) {
                MyRequestsScreen(
                    onBackClick = { dashboardNavController.popBackStack() },
                    onRequestClick = onNavigateToServiceDetail,
                    onExploreClick = { dashboardNavController.popBackStack(Screen.Home.route, inclusive = false) }
                )
            }
            composable(Screen.Achievements.route) {
                AchievementsScreen(onBackClick = { dashboardNavController.popBackStack() })
            }
            composable(Screen.Statistics.route) {
                PersonalStatisticsScreen(onBackClick = { dashboardNavController.popBackStack() })
            }
        }
    }
}
