package co.edu.uniquindio.servify.features.moderator.review

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import co.edu.uniquindio.servify.R
import co.edu.uniquindio.servify.domain.model.RejectionReason
import co.edu.uniquindio.servify.ui.components.button.ServifyFilledButton
import co.edu.uniquindio.servify.ui.components.button.ServifyTextButton
import co.edu.uniquindio.servify.ui.components.dialog.ServifyBottomSheet
import co.edu.uniquindio.servify.ui.components.input.ServifyTextField
import co.edu.uniquindio.servify.ui.theme.ServifyTextStyle

/**
 * Bottom Sheet de rechazo. El motivo escrito es obligatorio: los chips solo
 * sugieren un texto que el moderador puede editar.
 */
@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun RejectReasonSheet(
    reasonText: String,
    errorMessage: String?,
    isConfirmEnabled: Boolean,
    onReasonSelected: (RejectionReason) -> Unit,
    onReasonTextChange: (String) -> Unit,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    ServifyBottomSheet(
        onDismissRequest = onDismiss,
        title = stringResource(R.string.mod_reject_sheet_title)
    ) {
        Text(
            text = stringResource(R.string.mod_reject_reason_label),
            style = ServifyTextStyle.BodyMedium,
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = stringResource(R.string.mod_reject_reason_desc),
            style = ServifyTextStyle.Caption,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(top = 2.dp, bottom = 12.dp)
        )

        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            RejectionReason.entries.forEach { reason ->
                FilterChip(
                    selected = reasonText == reason.suggestedText,
                    onClick = { onReasonSelected(reason) },
                    label = { Text(reason.label) }
                )
            }
        }

        ServifyTextField(
            value = reasonText,
            onValueChange = onReasonTextChange,
            label = stringResource(R.string.mod_reject_describe_label),
            singleLine = false,
            isError = errorMessage != null,
            supportingText = errorMessage,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp)
        )

        ServifyFilledButton(
            text = stringResource(R.string.mod_reject_confirm),
            onClick = onConfirm,
            enabled = isConfirmEnabled,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp)
        )
        ServifyTextButton(
            text = stringResource(R.string.common_cancel),
            onClick = onDismiss,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 4.dp)
        )
    }
}
