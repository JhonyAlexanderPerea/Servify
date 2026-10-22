package co.edu.uniquindio.servify

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import co.edu.uniquindio.servify.navigation.AppNavigation
import co.edu.uniquindio.servify.ui.theme.ServifyTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        // Omitir la animación del sistema para que la primera animación sea el Splash Composable directamente
        splashScreen.setOnExitAnimationListener { splashScreenViewProvider ->
            splashScreenViewProvider.remove()
        }

        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        setContent {
            ServifyTheme {
                AppNavigation()
            }
        }
    }
}
