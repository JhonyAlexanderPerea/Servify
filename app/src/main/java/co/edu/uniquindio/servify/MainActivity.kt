package co.edu.uniquindio.servify

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import co.edu.uniquindio.servify.navigation.AppNavigation
import co.edu.uniquindio.servify.ui.theme.ServifyTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        setContent {
            ServifyTheme {
                AppNavigation()
            }
        }
    }
}