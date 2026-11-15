package co.edu.uniquindio.servify

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import co.edu.uniquindio.servify.navigation.AppNavigation
import co.edu.uniquindio.servify.ui.theme.ServifyTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private var resetPasswordCode by mutableStateOf<String?>(null)

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        // Omitir la animación del sistema para que la primera animación sea el Splash Composable directamente
        splashScreen.setOnExitAnimationListener { splashScreenViewProvider ->
            splashScreenViewProvider.remove()
        }

        super.onCreate(savedInstanceState)

        readResetPasswordCode(intent)

        enableEdgeToEdge()

        setContent {
            ServifyTheme {
                AppNavigation(
                    resetPasswordCode = resetPasswordCode,
                    onResetPasswordHandled = { resetPasswordCode = null }
                )
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        readResetPasswordCode(intent)
    }

    // Enlace de Firebase: https://<host>/__/auth/action?mode=resetPassword&oobCode=<codigo>
    private fun readResetPasswordCode(intent: Intent?) {
        val data = intent?.data ?: return
        if (data.getQueryParameter("mode") == "resetPassword") {
            resetPasswordCode = data.getQueryParameter("oobCode")
        }
    }
}
