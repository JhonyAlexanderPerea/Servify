package co.edu.uniquindio.servify.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import co.edu.uniquindio.servify.features.forgotpassword.ForgotPasswordScreen
import co.edu.uniquindio.servify.features.home.HomeScreen
import co.edu.uniquindio.servify.features.login.LoginScreen
import co.edu.uniquindio.servify.features.onboarding.OnboardingScreen
import co.edu.uniquindio.servify.features.register.RegisterScreen
import co.edu.uniquindio.servify.features.service.detail.ServiceDetailScreen
import co.edu.uniquindio.servify.features.splash.SplashScreen

@Composable
fun AppNavigation(
    navController: NavHostController = rememberNavController()
) {

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
        // HOME (FEED DE SERVICIOS)
        // -----------------------------------------

        composable(Screen.Home.route) {

            HomeScreen(
                onServiceClick = { serviceId ->
                    navController.navigate(
                        Screen.ServiceDetail.createRoute(serviceId)
                    )
                },
                onBookClick = { serviceId ->
                    navController.navigate(
                        Screen.ServiceDetail.createRoute(serviceId)
                    )
                }
            )
        }

        // -----------------------------------------
        // SERVICE DETAIL (DETALLE DE LA PUBLICACIÓN)
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
