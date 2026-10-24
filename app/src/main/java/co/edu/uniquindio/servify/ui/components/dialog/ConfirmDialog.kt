package co.edu.uniquindio.servify.ui.components.dialog

import android.content.res.Configuration
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import co.edu.uniquindio.servify.ui.theme.ServifyOnSurface
import co.edu.uniquindio.servify.ui.theme.ServifyOnSurfaceVariant
import co.edu.uniquindio.servify.ui.theme.ServifyPrimary
import co.edu.uniquindio.servify.ui.theme.ServifyTextStyle
import co.edu.uniquindio.servify.ui.theme.ServifyTheme

@Composable
fun ConfirmDialog(
    title: String,
    message: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    confirmText: String = "Confirmar",
    dismissText: String = "Cancelar",
    isDestructive: Boolean = false
) {
    val confirmColor = if (isDestructive) Color(0xFFBA1A1A) else ServifyPrimary

    AlertDialog(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(24.dp),
        icon = if (isDestructive) {
            {
                Icon(
                    imageVector = Icons.Default.Warning,
                    contentDescription = null,
                    tint = Color(0xFFBA1A1A)
                )
            }
        } else null,
        title = {
            Text(
                text = title,
                style = ServifyTextStyle.SmallMedium.copy(fontWeight = FontWeight.Bold),
                color = ServifyOnSurface
            )
        },
        text = {
            Text(
                text = message,
                style = ServifyTextStyle.SmallRelaxed,
                color = ServifyOnSurfaceVariant
            )
        },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text(
                    text = confirmText,
                    style = ServifyTextStyle.SmallMedium.copy(fontWeight = FontWeight.Bold),
                    color = confirmColor
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(
                    text = dismissText,
                    style = ServifyTextStyle.SmallMedium,
                    color = ServifyOnSurfaceVariant
                )
            }
        }
    )
}

@Preview(name = "Confirm Dialog Normal Light", showBackground = true)
@Composable
fun ConfirmDialogNormalPreviewLight() {
    ServifyTheme(darkTheme = false) {
        ConfirmDialog(
            title = "¿Confirmar reserva?",
            message = "Se enviará la solicitud de cita al proveedor Juan Carlos Mejía.",
            onConfirm = {},
            onDismiss = {}
        )
    }
}

@Preview(name = "Confirm Dialog Destructive Dark", showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun ConfirmDialogDestructivePreviewDark() {
    ServifyTheme(darkTheme = true) {
        ConfirmDialog(
            title = "¿Cancelar cita programada?",
            message = "Esta acción es irreversible y notificará al cliente de la cancelación.",
            confirmText = "Sí, Cancelar Cita",
            isDestructive = true,
            onConfirm = {},
            onDismiss = {}
        )
    }
}
