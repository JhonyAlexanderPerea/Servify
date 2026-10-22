package co.edu.uniquindio.servify.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import co.edu.uniquindio.servify.di.AppContainer
import co.edu.uniquindio.servify.domain.model.UserRole
import co.edu.uniquindio.servify.features.dashboard.MainScreen
import co.edu.uniquindio.servify.features.forgotpassword.ForgotPasswordScreen
import co.edu.uniquindio.servify.features.login.LoginScreen
import co.edu.uniquindio.servify.features.moderator.panel.ModeratorPanelScreen
import co.edu.uniquindio.servify.features.onboarding.OnboardingScreen
import co.edu.uniquindio.servify.features.register.RegisterScreen
import co.edu.uniquindio.servify.features.service.detail.ServiceDetailScreen
import co.edu.uniquindio.servify.features.splash.SplashScreen
import kotlinx.coroutines.launch

@Composable
fun AppNavigation(
    navController: NavHostController = rememberNavController()
) {

    val scope = rememberCoroutineScope()
    val logoutUseCase = AppContainer.logoutUseCase

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

                    navController.navigate(
                        Screen.Onboarding.route
                    ) {

                        popUpTo(
                            Screen.Splash.route
                        ) {
                            inclusive = true
                        }
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
                onLoginSuccess = { role ->

                    // El rol decide el flujo: moderador -> panel de moderación, usuario -> Home.
                    val destination = when (role) {
                        UserRole.MODERATOR -> Screen.ModeratorPanel.route
                        UserRole.USER -> Screen.Home.route
                    }

                    navController.navigate(destination) {

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
                    scope.launch { logoutUseCase() }
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Home.route) {
                            inclusive = true
                        }
                    }
                }
            )
        }

        // -----------------------------------------
        // PANEL DE MODERACIÓN (placeholder hasta la tarea 27)
        // -----------------------------------------

        composable(Screen.ModeratorPanel.route) {

            ModeratorPanelScreen(
                onLogoutClick = {
                    scope.launch { logoutUseCase() }
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.ModeratorPanel.route) {
                            inclusive = true
                        }
                    }
                }
            )
        }

        // -----------------------------------------
        // DETALLE DE PUBLICACIÓN (PANTALLA SECUNDARIA)
        // -----------------------------------------

        composable(Screen.ServiceDetail.route) {

            ServiceDetailScreen(
                serviceId = "1",
                onBackClick = {
                    navController.popBackStack()
                },
                onBookClick = {
                    // Acción para agendar cita
                }
            )
        }
    }
}
