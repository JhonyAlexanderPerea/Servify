package co.edu.uniquindio.servify.navigation

import co.edu.uniquindio.servify.domain.model.UserRole
import co.edu.uniquindio.servify.features.forgotpassword.ForgotPasswordViewModel

sealed class Screen(val route: String) {
    data object Splash : Screen("splash")
    data object Onboarding : Screen("onboarding")
    data object Login : Screen("login")
    data object Register : Screen("register")

    data object ForgotPassword : Screen("forgot_password?${ForgotPasswordViewModel.ARG_ROLE}={${ForgotPasswordViewModel.ARG_ROLE}}") {
        fun createRoute(role: UserRole = UserRole.USER) =
            "forgot_password?${ForgotPasswordViewModel.ARG_ROLE}=${role.name}"
    }

    data object Main : Screen("main")
    data object Home : Screen("home")
    data object Explore : Screen("explore")
    data object Notifications : Screen("notifications")
    data object Profile : Screen("profile")

    data object MyPublications : Screen("my_publications")
    data object MyRequests : Screen("my_requests")
    data object Achievements : Screen("achievements")
    data object Statistics : Screen("statistics")
    data object Settings : Screen("settings")

    data object ServiceDetail : Screen("service_detail/{serviceId}") {
        fun createRoute(serviceId: String) = "service_detail/$serviceId"
    }

    data object CreatePublication : Screen("create_publication")

    data object TrustSystem : Screen("trust_system/{providerId}") {
        fun createRoute(providerId: String) = "trust_system/$providerId"
    }

    data object PublicationPublished : Screen("publication_published/{publicationId}") {
        fun createRoute(publicationId: String) = "publication_published/$publicationId"
    }

    data object RequestService : Screen("request_service/{serviceId}") {
        fun createRoute(serviceId: String) = "request_service/$serviceId"
    }

    data object RequestConfirmed : Screen("request_confirmed/{requestId}") {
        fun createRoute(requestId: String) = "request_confirmed/$requestId"
    }

    data object ModeratorPanel : Screen("moderator_panel")
}
