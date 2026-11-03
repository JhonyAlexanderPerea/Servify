package co.edu.uniquindio.servify.ui.components.dialog

import android.content.res.Configuration
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
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
    val confirmColor = if (isDestructive) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary

    AlertDialog(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(24.dp),
        icon = if (isDestructive) {
            {
                Icon(
                    imageVector = Icons.Default.Warning,
                    contentDescription = null,
                    tint = confirmColor
                )
            }
        } else null,
        title = {
            Text(
                text = title,
                style = ServifyTextStyle.titleLarge.copy(fontWeight = FontWeight.SemiBold),
                color = MaterialTheme.colorScheme.onSurface
            )
        },
        text = {
            Text(
                text = message,
                style = ServifyTextStyle.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text(
                    text = confirmText,
                    style = ServifyTextStyle.labelLarge,
                    color = confirmColor
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(
                    text = dismissText,
                    style = ServifyTextStyle.labelLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    )
}

@Preview(name = "Confirm Dialog - Light", showBackground = true)
@Preview(name = "Confirm Dialog - Dark", showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun ConfirmDialogPreview() {
    ServifyTheme {
        ConfirmDialog(
            title = "Confirmar acción",
            message = "¿Estás seguro de que deseas continuar con esta acción?",
            onConfirm = {},
            onDismiss = {}
        )
    }
}

@Preview(name = "Destructive Dialog - Light", showBackground = true)
@Composable
private fun ConfirmDialogDestructivePreview() {
    ServifyTheme {
        ConfirmDialog(
            title = "Eliminar publicación",
            message = "Esta acción no se puede deshacer. ¿Deseas eliminarla?",
            onConfirm = {},
            onDismiss = {},
            confirmText = "Eliminar",
            isDestructive = true
        )
    }
}
