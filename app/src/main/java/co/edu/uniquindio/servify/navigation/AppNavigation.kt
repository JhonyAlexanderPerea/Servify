package co.edu.uniquindio.servify.navigation

import androidx.compose.runtime.Composable
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
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
import co.edu.uniquindio.servify.features.onboarding.OnboardingScreen
import co.edu.uniquindio.servify.features.publication.create.CreatePublicationScreen
import co.edu.uniquindio.servify.features.publication.published.PublicationPublishedScreen
import co.edu.uniquindio.servify.features.register.RegisterScreen
import co.edu.uniquindio.servify.features.request.confirmed.RequestConfirmedScreen
import co.edu.uniquindio.servify.features.request.create.RequestServiceScreen
import co.edu.uniquindio.servify.features.service.detail.ServiceDetailScreen
import co.edu.uniquindio.servify.features.session.SessionViewModel
import co.edu.uniquindio.servify.features.splash.SplashScreen
import co.edu.uniquindio.servify.features.trust.TrustSystemScreen
import co.edu.uniquindio.servify.features.profile.edit.EditProfileScreen

@Composable
fun AppNavigation(
    navController: NavHostController = rememberNavController()
) {
    val sessionViewModel: SessionViewModel = hiltViewModel()

    NavHost(
        navController = navController,
        startDestination = Screen.Splash.route
    ) {
        // -----------------------------------------
        // SPLASH
        // -----------------------------------------
        composable(Screen.Splash.route) {
            SplashScreen(
                onFinished = {
                    navController.navigate(Screen.Onboarding.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                }
            )
        }

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
                onLoginSuccess = { role ->
                    val destination = when (role) {
                        UserRole.MODERATOR -> Screen.ModeratorPanel.route
                        UserRole.USER, UserRole.CLIENT, UserRole.PROVIDER -> Screen.Home.route
                    }
                    navController.navigate(destination) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                },
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
                onBack = { navController.popBackStack() },
                onRegisterSuccess = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Register.route) { inclusive = true }
                    }
                }
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

        // -----------------------------------------
        // DASHBOARD PRINCIPAL CON BOTTOM NAVIGATION BAR
        // -----------------------------------------
        composable(Screen.Home.route) {
            MainScreen(
                onNavigateToServiceDetail = { serviceId ->
                    navController.navigate(Screen.ServiceDetail.createRoute(serviceId))
                },
                onNavigateToEditProfile = {
                    navController.navigate(Screen.EditProfile.route)
                },
                onLogoutClick = {
                    sessionViewModel.logout()
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Home.route) { inclusive = true }
                    }
                }
            )
        }

        // -----------------------------------------
        // PANEL DE MODERACIÓN
        // -----------------------------------------
        composable(Screen.ModeratorPanel.route) {
            ModeratorPanelScreen(
                onLogoutClick = {
                    sessionViewModel.logout()
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.ModeratorPanel.route) { inclusive = true }
                    }
                }
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
                }
            )
        }

        // -----------------------------------------
        // CREAR PUBLICACIÓN
        // -----------------------------------------
        composable(Screen.CreatePublication.route) {
            CreatePublicationScreen(
                onBackClick = { navController.popBackStack() },
                onPublishSuccess = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Home.route) { inclusive = true }
                    }
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
                onGoToMyPublications = { navController.popBackStack() },
                onGoToHome = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Home.route) { inclusive = true }
                    }
                }
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
                onGoToAgenda = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Home.route) { inclusive = true }
                    }
                },
                onGoToFeed = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Home.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.EditProfile.route) {
            EditProfileScreen(
                onBackClick = { navController.popBackStack() },
                onNavigateToLogin = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.EditProfile.route) { inclusive = true }
                    }
                }
            )
        }

    }
}
