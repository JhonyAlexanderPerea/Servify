package co.edu.uniquindio.servify.navigation

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.AddCircleOutline
import androidx.compose.material.icons.outlined.Explore
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Person
import androidx.compose.ui.graphics.vector.ImageVector
import co.edu.uniquindio.servify.R

sealed class BottomNavDestination(
    val route: String,
    @get:StringRes val labelResId: Int,
    val icon: ImageVector,
    val selectedIcon: ImageVector,
    val badgeCount: Int? = null,
    val isCentralAction: Boolean = false
) {
    data object Home : BottomNavDestination(
        route = Screen.Home.route,
        labelResId = R.string.nav_home,
        icon = Icons.Outlined.Home,
        selectedIcon = Icons.Filled.Home
    )

    data object Explore : BottomNavDestination(
        route = Screen.Explore.route,
        labelResId = R.string.nav_explore,
        icon = Icons.Outlined.Explore,
        selectedIcon = Icons.Filled.Explore
    )

    data object CreatePublication : BottomNavDestination(
        route = Screen.CreatePublication.route,
        labelResId = R.string.nav_create,
        icon = Icons.Outlined.AddCircleOutline,
        selectedIcon = Icons.Filled.AddCircle,
        isCentralAction = true
    )

    data object Notifications : BottomNavDestination(
        route = Screen.Notifications.route,
        labelResId = R.string.nav_notifications,
        icon = Icons.Outlined.Notifications,
        selectedIcon = Icons.Filled.Notifications,
        badgeCount = null
    )

    data object Profile : BottomNavDestination(
        route = Screen.Profile.route,
        labelResId = R.string.nav_profile,
        icon = Icons.Outlined.Person,
        selectedIcon = Icons.Filled.Person
    )

    companion object {
        val items = listOf(
            Home,
            Explore,
            CreatePublication,
            Notifications,
            Profile
        )
    }
}
