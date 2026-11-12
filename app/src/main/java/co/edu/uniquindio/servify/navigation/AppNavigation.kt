package co.edu.uniquindio.servify.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import co.edu.uniquindio.servify.domain.model.UserRole
import co.edu.uniquindio.servify.features.dashboard.MainScreen
import co.edu.uniquindio.servify.features.forgotpassword.ForgotPasswordScreen
import co.edu.uniquindio.servify.features.forgotpassword.ForgotPasswordViewModel
import co.edu.uniquindio.servify.features.login.LoginScreen
import co.edu.uniquindio.servify.features.moderator.panel.ModeratorPanelScreen
import co.edu.uniquindio.servify.features.moderator.review.ModeratorReviewScreen
import co.edu.uniquindio.servify.features.onboarding.OnboardingScreen
import co.edu.uniquindio.servify.features.profile.edit.EditProfileScreen
import co.edu.uniquindio.servify.features.publication.published.PublicationPublishedScreen
import co.edu.uniquindio.servify.features.register.RegisterScreen
import co.edu.uniquindio.servify.features.request.confirmed.RequestConfirmedScreen
import co.edu.uniquindio.servify.features.resetpassword.ResetPasswordScreen
import co.edu.uniquindio.servify.features.request.create.RequestServiceScreen
import co.edu.uniquindio.servify.features.service.detail.ServiceDetailScreen
import co.edu.uniquindio.servify.features.splash.SplashScreen
import co.edu.uniquindio.servify.features.trust.TrustSystemScreen
import co.edu.uniquindio.servify.ui.motion.servifyEnter
import co.edu.uniquindio.servify.ui.motion.servifyExit
import co.edu.uniquindio.servify.ui.motion.servifyPopEnter
import co.edu.uniquindio.servify.ui.motion.servifyPopExit

@Composable
fun AppNavigation(
    resetPasswordCode: String? = null,
    onResetPasswordHandled: () -> Unit = {},
    sessionViewModel: SessionViewModel = hiltViewModel()
) {
    val sessionState by sessionViewModel.sessionState.collectAsStateWithLifecycle()
    var splashFinished by rememberSaveable { mutableStateOf(false) }

    Surface(modifier = Modifier.fillMaxSize()) {
        when {
            // El enlace de recuperación tiene prioridad sobre el splash y la sesión
            resetPasswordCode != null -> ResetPasswordScreen(
                oobCode = resetPasswordCode,
                onFinished = onResetPasswordHandled
            )

            !splashFinished -> {
                // Se conserva el splash animado existente
                SplashScreen(onFinished = { splashFinished = true })
            }

            else -> when (val state = sessionState) {
                // Indicador mientras se determina el estado de la sesión
                is SessionState.Loading -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
                // Sin sesión: navegación de autenticación
                is SessionState.NotAuthenticated -> AuthNavigation()
                // Con sesión: grafo autenticado, con inicio según el rol
                is SessionState.Authenticated -> MainNavigation(role = state.session.role)
            }
        }
    }
}

@Composable
private fun AuthNavigation() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Screen.Onboarding.route,
        enterTransition = { servifyEnter() },
        exitTransition = { servifyExit() },
        popEnterTransition = { servifyPopEnter() },
        popExitTransition = { servifyPopExit() }
    ) {
        // -----------------------------------------
        // ONBOARDING
        // -----------------------------------------
        composable(Screen.Onboarding.route) {
            OnboardingScreen(
                onFinish = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Onboarding.route) { inclusive = true }
                    }
                }
            )
        }

        // -----------------------------------------
        // LOGIN
        // -----------------------------------------
        composable(Screen.Login.route) {
            LoginScreen(
                onRegisterClick = {
                    navController.navigate(Screen.Register.route)
                },
                onForgotPasswordClick = {
                    navController.navigate(Screen.ForgotPassword.createRoute(UserRole.USER))
                }
            )
        }

        // -----------------------------------------
        // REGISTER
        // -----------------------------------------
        composable(Screen.Register.route) {
            RegisterScreen(
                onBack = { navController.popBackStack() }
            )
        }

        // -----------------------------------------
        // FORGOT PASSWORD
        // -----------------------------------------
        composable(
            route = Screen.ForgotPassword.route,
            arguments = listOf(
                navArgument(ForgotPasswordViewModel.ARG_ROLE) {
                    type = NavType.StringType
                    defaultValue = UserRole.USER.name
                }
            )
        ) {
            ForgotPasswordScreen(
                onBack = { navController.popBackStack() },
                onBackToLogin = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.ForgotPassword.route) { inclusive = true }
                    }
                }
            )
        }
    }
}

@Composable
private fun MainNavigation(role: UserRole) {
    val navController = rememberNavController()

    // Vuelve al dashboard y le pide abrir una sección (Mis solicitudes, Mis publicaciones...).
    fun goToDashboard(dashboardRoute: String?) {
        runCatching {
            navController.getBackStackEntry(Screen.Home.route)
                .savedStateHandle["dashboard_route"] = dashboardRoute
        }
        if (!navController.popBackStack(Screen.Home.route, inclusive = false)) {
            navController.navigate(Screen.Home.route)
        }
    }

    NavHost(
        navController = navController,
        startDestination = if (role == UserRole.MODERATOR) Screen.ModeratorPanel.route else Screen.Home.route,
        enterTransition = { servifyEnter() },
        exitTransition = { servifyExit() },
        popEnterTransition = { servifyPopEnter() },
        popExitTransition = { servifyPopExit() }
    ) {
        // -----------------------------------------
        // DASHBOARD PRINCIPAL CON BOTTOM NAVIGATION BAR
        // -----------------------------------------
        composable(Screen.Home.route) { entry ->
            val pendingRoute by entry.savedStateHandle
                .getStateFlow<String?>("dashboard_route", null)
                .collectAsStateWithLifecycle()
            MainScreen(
                pendingRoute = pendingRoute,
                onPendingRouteConsumed = { entry.savedStateHandle["dashboard_route"] = null },
                onNavigateToPublicationPublished = { publicationId ->
                    navController.navigate(Screen.PublicationPublished.createRoute(publicationId))
                },
                onNavigateToServiceDetail = { serviceId ->
                    navController.navigate(Screen.ServiceDetail.createRoute(serviceId))
                },
                onNavigateToEditProfile = {
                    navController.navigate(Screen.EditProfile.route)
                }
            )
        }

        // -----------------------------------------
        // PANEL DE MODERACIÓN
        // -----------------------------------------
        composable(Screen.ModeratorPanel.route) {
            ModeratorPanelScreen(
                onReviewClick = { publicationId ->
                    navController.navigate(Screen.ModeratorReview.createRoute(publicationId))
                }
            )
        }

        // -----------------------------------------
        // REVISAR PUBLICACIÓN (MODERADOR)
        // -----------------------------------------
        composable(Screen.ModeratorReview.route) {
            ModeratorReviewScreen(
                onBackClick = { navController.popBackStack() }
            )
        }

        // -----------------------------------------
        // DETALLE DE PUBLICACIÓN
        // -----------------------------------------
        composable(Screen.ServiceDetail.route) { backStackEntry ->
            val serviceId = backStackEntry.arguments?.getString("serviceId") ?: "pub_1"
            ServiceDetailScreen(
                serviceId = serviceId,
                onBackClick = { navController.popBackStack() },
                onBookClick = {
                    navController.navigate(Screen.RequestService.createRoute(serviceId))
                },
                onTrustClick = { providerId ->
                    navController.navigate(Screen.TrustSystem.createRoute(providerId))
                }
            )
        }

        // -----------------------------------------
        // SISTEMA DE CONFIANZA
        // -----------------------------------------
        composable(Screen.TrustSystem.route) {
            TrustSystemScreen(
                onBackClick = { navController.popBackStack() }
            )
        }

        // -----------------------------------------
        // PUBLICACIÓN PUBLICADA
        // -----------------------------------------
        composable(Screen.PublicationPublished.route) { backStackEntry ->
            val pubId = backStackEntry.arguments?.getString("publicationId") ?: "pub_1"
            PublicationPublishedScreen(
                publicationId = pubId,
                onGoToMyPublications = { goToDashboard(Screen.MyPublications.route) },
                onGoToHome = { goToDashboard(null) }
            )
        }

        // -----------------------------------------
        // SOLICITAR SERVICIO
        // -----------------------------------------
        composable(Screen.RequestService.route) {
            RequestServiceScreen(
                onBackClick = { navController.popBackStack() },
                onRequestConfirmed = { requestId ->
                    navController.navigate(Screen.RequestConfirmed.createRoute(requestId)) {
                        popUpTo(Screen.ServiceDetail.route) { inclusive = true }
                    }
                }
            )
        }

        // -----------------------------------------
        // SOLICITUD CONFIRMADA
        // -----------------------------------------
        composable(Screen.RequestConfirmed.route) { backStackEntry ->
            val reqId = backStackEntry.arguments?.getString("requestId") ?: "req_1"
            RequestConfirmedScreen(
                requestId = reqId,
                onGoToMyRequests = { goToDashboard(Screen.MyRequests.route) },
                onGoToFeed = { goToDashboard(null) }
            )
        }

        // -----------------------------------------
        // EDITAR PERFIL
        // -----------------------------------------
        composable(Screen.EditProfile.route) {
            EditProfileScreen(
                onBackClick = { navController.popBackStack() }
            )
        }
    }
}
