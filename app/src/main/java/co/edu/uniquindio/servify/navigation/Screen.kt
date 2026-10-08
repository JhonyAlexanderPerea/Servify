package co.edu.uniquindio.servify.navigation

sealed class Screen(val route: String) {

    data object Splash : Screen("splash")

    data object Onboarding : Screen("onboarding")

    data object Login : Screen("login")

    data object Register : Screen("register")

    data object ForgotPassword : Screen("forgot_password")

    data object Home : Screen("home")

    data object ServiceDetail : Screen("service_detail/{serviceId}") {
        fun createRoute(serviceId: String) = "service_detail/$serviceId"
    }

    data object CreatePublication : Screen("create_publication")
}
