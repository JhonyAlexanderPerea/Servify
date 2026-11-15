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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import co.edu.uniquindio.servify.R
import co.edu.uniquindio.servify.ui.theme.ServifyTextStyle
import co.edu.uniquindio.servify.ui.theme.ServifyTheme

@Composable
fun ConfirmDialog(
    title: String,
    message: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    confirmText: String = stringResource(R.string.common_confirm),
    dismissText: String = stringResource(R.string.common_cancel),
    isDestructive: Boolean = false,
    icon: ImageVector? = null
) {
    val confirmColor = if (isDestructive) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
    val dialogIcon = icon ?: if (isDestructive) Icons.Default.Warning else null

    AlertDialog(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(24.dp),
        icon = dialogIcon?.let {
            {
                Icon(
                    imageVector = it,
                    contentDescription = null,
                    tint = confirmColor
                )
            }
        },
        title = {
            Text(
                text = title,
                style = ServifyTextStyle.TitleTight.copy(fontWeight = FontWeight.Bold, fontSize = 20.sp),
                color = MaterialTheme.colorScheme.onSurface
            )
        },
        text = {
            Text(
                text = message,
                style = ServifyTextStyle.SmallMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
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
            title = stringResource(R.string.confirm_dialog_title),
            message = stringResource(R.string.confirm_dialog_message),
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
            title = stringResource(R.string.confirm_dialog_delete_publication_title),
            message = stringResource(R.string.confirm_dialog_delete_publication_message),
            onConfirm = {},
            onDismiss = {},
            confirmText = stringResource(R.string.common_delete),
            isDestructive = true
        )
    }
}
