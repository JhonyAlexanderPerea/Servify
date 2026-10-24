package co.edu.uniquindio.servify.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import co.edu.uniquindio.servify.features.dashboard.MainScreen
import co.edu.uniquindio.servify.features.forgotpassword.ForgotPasswordScreen
import co.edu.uniquindio.servify.features.login.LoginScreen
import co.edu.uniquindio.servify.features.onboarding.OnboardingScreen
import co.edu.uniquindio.servify.features.publication.create.CreatePublicationScreen
import co.edu.uniquindio.servify.features.publication.published.PublicationPublishedScreen
import co.edu.uniquindio.servify.features.register.RegisterScreen
import co.edu.uniquindio.servify.features.request.confirmed.RequestConfirmedScreen
import co.edu.uniquindio.servify.features.request.create.RequestServiceScreen
import co.edu.uniquindio.servify.features.service.detail.ServiceDetailScreen
import co.edu.uniquindio.servify.features.trust.TrustSystemScreen

@Composable
fun AppNavigation(
    navController: NavHostController = rememberNavController()
) {

    NavHost(
        navController = navController,
        startDestination = Screen.Onboarding.route
    ) {

        // -----------------------------------------
        // ONBOARDING
        // -----------------------------------------

        composable(Screen.Onboarding.route) {

            OnboardingScreen(
                onFinish = {

                    navController.navigate(
                        Screen.Login.route
                    ) {

                        popUpTo(
                            Screen.Onboarding.route
                        ) {
                            inclusive = true
                        }
                    }
                }
            )
        }

        // -----------------------------------------
        // LOGIN
        // -----------------------------------------

        composable(Screen.Login.route) {

            LoginScreen(
                onLoginSuccess = {

                    navController.navigate(
                        Screen.Home.route
                    ) {

                        popUpTo(
                            Screen.Login.route
                        ) {
                            inclusive = true
                        }
                    }
                },

                onRegisterClick = {

                    navController.navigate(
                        Screen.Register.route
                    )
                },

                onForgotPasswordClick = {

                    navController.navigate(
                        Screen.ForgotPassword.route
                    )
                }
            )
        }

        // -----------------------------------------
        // REGISTER
        // -----------------------------------------

        composable(Screen.Register.route) {

            RegisterScreen(
                onBack = {
                    navController.popBackStack()
                },

                onRegisterSuccess = {

                    navController.navigate(
                        Screen.Home.route
                    ) {

                        popUpTo(
                            Screen.Register.route
                        ) {
                            inclusive = true
                        }
                    }
                }
            )
        }

        // -----------------------------------------
        // FORGOT PASSWORD
        // -----------------------------------------

        composable(Screen.ForgotPassword.route) {

            ForgotPasswordScreen(
                onBack = {
                    navController.popBackStack()
                },

                onBackToLogin = {

                    navController.navigate(
                        Screen.Login.route
                    ) {

                        popUpTo(
                            Screen.ForgotPassword.route
                        ) {
                            inclusive = true
                        }
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
                    navController.navigate(
                        Screen.ServiceDetail.createRoute(serviceId)
                    )
                },
                onLogoutClick = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Home.route) {
                            inclusive = true
                        }
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
                onBackClick = {
                    navController.popBackStack()
                },
                onBookClick = {
                    navController.navigate(Screen.RequestService.createRoute(serviceId))
                }
            )
        }

        // -----------------------------------------
        // CREAR/EDITAR PUBLICACIÓN
        // -----------------------------------------

        composable(Screen.CreatePublication.route) {

            CreatePublicationScreen(
                onBackClick = {
                    navController.popBackStack()
                },
                onPublishSuccess = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Home.route) {
                            inclusive = true
                        }
                    }
                }
            )
        }

        // -----------------------------------------
        // PANTALLA ÍNDICE DE CONFIANZA Y REPUTACIÓN
        // -----------------------------------------

        composable(Screen.TrustSystem.route) {

            TrustSystemScreen(
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }

        // -----------------------------------------
        // PANTALLA PUBLICACIÓN PUBLICADA (ÉXITO)
        // -----------------------------------------

        composable(Screen.PublicationPublished.route) { backStackEntry ->
            val pubId = backStackEntry.arguments?.getString("publicationId") ?: "pub_1"

            PublicationPublishedScreen(
                publicationId = pubId,
                onGoToMyPublications = {
                    navController.popBackStack()
                },
                onGoToHome = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Home.route) {
                            inclusive = true
                        }
                    }
                }
            )
        }

        // -----------------------------------------
        // PANTALLA SOLICITAR SERVICIO (FORMULARIO CITA)
        // -----------------------------------------

        composable(Screen.RequestService.route) { backStackEntry ->

            RequestServiceScreen(
                onBackClick = {
                    navController.popBackStack()
                },
                onRequestConfirmed = { requestId ->
                    navController.navigate(Screen.RequestConfirmed.createRoute(requestId)) {
                        popUpTo(Screen.ServiceDetail.route) {
                            inclusive = true
                        }
                    }
                }
            )
        }

        // -----------------------------------------
        // PANTALLA SOLICITUD CONFIRMADA (ÉXITO)
        // -----------------------------------------

        composable(Screen.RequestConfirmed.route) { backStackEntry ->
            val reqId = backStackEntry.arguments?.getString("requestId") ?: "req_1"

            RequestConfirmedScreen(
                requestId = reqId,
                onGoToAgenda = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Home.route) {
                            inclusive = true
                        }
                    }
                },
                onGoToFeed = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Home.route) {
                            inclusive = true
                        }
                    }
                }
            )
        }
    }
}
