package co.edu.uniquindio.servify.ui.components.state

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import co.edu.uniquindio.servify.ui.theme.ServifyOnSurfaceVariant
import co.edu.uniquindio.servify.ui.theme.ServifyPrimary
import co.edu.uniquindio.servify.ui.theme.ServifySurface
import co.edu.uniquindio.servify.ui.theme.ServifyTextStyle
import co.edu.uniquindio.servify.ui.theme.ServifyTheme

@Composable
fun LoadingState(
    modifier: Modifier = Modifier,
    message: String? = "Cargando..."
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(ServifySurface)
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            CircularProgressIndicator(
                modifier = Modifier.size(48.dp),
                color = ServifyPrimary,
                strokeWidth = 4.dp
            )

            if (!message.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = message,
                    style = ServifyTextStyle.SmallMedium,
                    color = ServifyOnSurfaceVariant
                )
            }
        }
    }
}

@Preview(name = "Loading State Light", showBackground = true)
@Composable
fun LoadingStatePreviewLight() {
    ServifyTheme(darkTheme = false) {
        LoadingState()
    }
}

@Preview(name = "Loading State Dark", showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun LoadingStatePreviewDark() {
    ServifyTheme(darkTheme = true) {
        LoadingState(message = "Cargando servicios...")
    }
}
