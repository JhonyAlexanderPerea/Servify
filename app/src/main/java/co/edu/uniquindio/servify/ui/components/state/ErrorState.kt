package co.edu.uniquindio.servify.ui.components.state

import android.content.res.Configuration
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.WifiOff
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import co.edu.uniquindio.servify.ui.components.button.ServifyFilledButton
import co.edu.uniquindio.servify.ui.theme.ServifyOnSurface
import co.edu.uniquindio.servify.ui.theme.ServifyOnSurfaceVariant
import co.edu.uniquindio.servify.ui.theme.ServifySurfaceVariant
import co.edu.uniquindio.servify.ui.theme.ServifyTextStyle
import co.edu.uniquindio.servify.ui.theme.ServifyTheme

@Composable
fun ErrorState(
    modifier: Modifier = Modifier,
    icon: ImageVector = Icons.Default.ErrorOutline,
    title: String = "Algo salió mal",
    description: String = "Ha ocurrido un error inesperado al procesar la información. Por favor, intenta de nuevo.",
    retryText: String = "Reintentar",
    onRetry: () -> Unit
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = ServifySurfaceVariant)
    ) {
        Column(
            modifier = Modifier
                .padding(24.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = Color(0xFFBA1A1A),
                modifier = Modifier.size(56.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = title,
                style = ServifyTextStyle.SmallMedium.copy(fontWeight = FontWeight.Bold),
                color = ServifyOnSurface,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = description,
                style = ServifyTextStyle.Caption,
                color = ServifyOnSurfaceVariant,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(16.dp))

            ServifyFilledButton(
                text = retryText,
                icon = Icons.Default.Refresh,
                onClick = onRetry,
                modifier = Modifier.fillMaxWidth(0.8f)
            )
        }
    }
}

@Composable
fun NoConnectionState(
    modifier: Modifier = Modifier,
    onRetry: () -> Unit
) {
    ErrorState(
        modifier = modifier,
        icon = Icons.Default.WifiOff,
        title = "Sin conexión a internet",
        description = "Verifica tu conexión a la red Wi-Fi o datos móviles e intenta cargar nuevamente.",
        retryText = "Volver a conectar",
        onRetry = onRetry
    )
}

@Preview(name = "Error State Light", showBackground = true)
@Composable
fun ErrorStatePreviewLight() {
    ServifyTheme(darkTheme = false) {
        ErrorState(onRetry = {})
    }
}

@Preview(name = "No Connection Dark", showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun NoConnectionPreviewDark() {
    ServifyTheme(darkTheme = true) {
        NoConnectionState(onRetry = {})
    }
}
