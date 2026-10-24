package co.edu.uniquindio.servify.navigation

sealed class Screen(val route: String) {

    data object Splash : Screen("splash")

    data object Onboarding : Screen("onboarding")

    data object Login : Screen("login")

    data object Register : Screen("register")

    data object ForgotPassword : Screen("forgot_password")

    data object Main : Screen("main")

    data object Home : Screen("home")

    data object Explore : Screen("explore")

    data object Notifications : Screen("notifications")

    data object Profile : Screen("profile")

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
}
