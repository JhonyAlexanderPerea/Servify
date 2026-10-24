package co.edu.uniquindio.servify.ui.components.feedback

import android.content.res.Configuration
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import co.edu.uniquindio.servify.ui.theme.ServifyPrimary
import co.edu.uniquindio.servify.ui.theme.ServifyTextStyle
import co.edu.uniquindio.servify.ui.theme.ServifyTheme

@Composable
fun ServifySnackbarHost(
    hostState: SnackbarHostState,
    modifier: Modifier = Modifier,
    containerColor: Color = MaterialTheme.colorScheme.onSurface,
    contentColor: Color = MaterialTheme.colorScheme.surface
) {
    SnackbarHost(
        hostState = hostState,
        modifier = modifier.padding(16.dp)
    ) { snackbarData ->
        Snackbar(
            shape = RoundedCornerShape(16.dp),
            containerColor = containerColor,
            contentColor = contentColor,
            action = snackbarData.visuals.actionLabel?.let { actionLabel ->
                {
                    TextButton(onClick = { snackbarData.performAction() }) {
                        Text(
                            text = actionLabel,
                            style = ServifyTextStyle.Caption.copy(fontWeight = FontWeight.Bold),
                            color = ServifyPrimary
                        )
                    }
                }
            }
        ) {
            Text(
                text = snackbarData.visuals.message,
                style = ServifyTextStyle.Small,
                color = contentColor
            )
        }
    }
}

@Preview(name = "Snackbar Light", showBackground = true)
@Composable
fun ServifySnackbarHostPreviewLight() {
    ServifyTheme(darkTheme = false) {
        val hostState = SnackbarHostState()
        ServifySnackbarHost(hostState = hostState)
    }
}
