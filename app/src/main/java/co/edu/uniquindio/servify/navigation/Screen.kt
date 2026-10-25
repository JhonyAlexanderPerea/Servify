package co.edu.uniquindio.servify.navigation

import co.edu.uniquindio.servify.domain.model.UserRole
import co.edu.uniquindio.servify.features.forgotpassword.ForgotPasswordViewModel

sealed class Screen(val route: String) {

    data object Splash : Screen("splash")

    data object Onboarding : Screen("onboarding")

    data object Login : Screen("login")

    data object Register : Screen("register")

    /** Recuperación de contraseña compartida por usuario y moderador (el rol viaja como argumento). */
    data object ForgotPassword : Screen("forgot_password?${ForgotPasswordViewModel.ARG_ROLE}={${ForgotPasswordViewModel.ARG_ROLE}}") {
        fun createRoute(role: UserRole = UserRole.USER) =
            "forgot_password?${ForgotPasswordViewModel.ARG_ROLE}=${role.name}"
    }

    data object Main : Screen("main")

    data object Home : Screen("home")

    data object Explore : Screen("explore")

    data object Notifications : Screen("notifications")

    data object Profile : Screen("profile")

    // Secciones del Perfil (pantalla 17). Las que aún no tienen pantalla muestran "Próximamente".
    data object MyPublications : Screen("my_publications")

    data object MyRequests : Screen("my_requests")

    data object Achievements : Screen("achievements")

    data object Statistics : Screen("statistics")

    data object Settings : Screen("settings")

    data object ServiceDetail : Screen("service_detail/{serviceId}") {
        fun createRoute(serviceId: String) = "service_detail/$serviceId"
    }

    data object CreatePublication : Screen("create_publication")

    // Flujo del moderador (tareas 26-28 lo completan)
    data object ModeratorPanel : Screen("moderator_panel")
}
