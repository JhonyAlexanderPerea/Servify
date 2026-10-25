package co.edu.uniquindio.servify.features.publication.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import co.edu.uniquindio.servify.domain.model.PublicationStatus
import co.edu.uniquindio.servify.ui.theme.ServifyExtra
import co.edu.uniquindio.servify.ui.theme.ServifyTextStyle

/** Texto del estado. El color nunca va solo: siempre acompaña a una etiqueta (accesibilidad). */
fun PublicationStatus.label(): String = when (this) {
    PublicationStatus.PENDIENTE -> "Pendiente de verificación"
    PublicationStatus.VERIFICADA -> "Verificada"
    PublicationStatus.RECHAZADA -> "Rechazada"
    PublicationStatus.FINALIZADA -> "Finalizada"
}

/** 🟡 pendiente · 🟢 verificada · 🔴 rechazada · 🔵 finalizada (misma codificación del mockup). */
@Composable
fun PublicationStatus.color(): Color = when (this) {
    PublicationStatus.PENDIENTE -> ServifyExtra.warning
    PublicationStatus.VERIFICADA -> MaterialTheme.colorScheme.secondary
    PublicationStatus.RECHAZADA -> MaterialTheme.colorScheme.error
    PublicationStatus.FINALIZADA -> MaterialTheme.colorScheme.primary
}

@Composable
fun PublicationStatusBadge(
    status: PublicationStatus,
    modifier: Modifier = Modifier
) {
    val color = status.color()
    Row(
        modifier = modifier
            .clip(CircleShape)
            .background(color.copy(alpha = 0.12f))
            .padding(horizontal = 10.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .clip(CircleShape)
                .background(color)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = status.label(),
            style = ServifyTextStyle.Caption.copy(fontWeight = FontWeight.Bold),
            color = color
        )
    }
}
