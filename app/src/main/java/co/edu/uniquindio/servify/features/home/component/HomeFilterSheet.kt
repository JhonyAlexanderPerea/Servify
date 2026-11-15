package co.edu.uniquindio.servify.features.home.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import co.edu.uniquindio.servify.R
import co.edu.uniquindio.servify.ui.components.button.ServifyFilledButton
import co.edu.uniquindio.servify.ui.components.button.ServifyOutlinedButton
import co.edu.uniquindio.servify.ui.components.chips.ServifyChip
import co.edu.uniquindio.servify.ui.components.dialog.ServifyBottomSheet
import co.edu.uniquindio.servify.ui.theme.ServifyTextStyle

/**
 * Hoja de filtros para la pantalla principal.
 */
@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun HomeFilterSheet(
    options: List<String>,
    selected: String,
    onSelect: (String) -> Unit,
    onClear: () -> Unit,
    onDismiss: () -> Unit
) {
    ServifyBottomSheet(
        onDismissRequest = onDismiss,
        title = stringResource(R.string.home_filter_title)
    ) {
        Text(
            text = stringResource(R.string.home_filter_subtitle),
            style = ServifyTextStyle.BodyMedium,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            options.forEach { option ->
                ServifyChip(label = option, selected = option == selected, onClick = { onSelect(option) })
            }
        }
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 20.dp)
        ) {
            ServifyOutlinedButton(
                text = stringResource(R.string.common_reset),
                onClick = onClear,
                modifier = Modifier.weight(1f)
            )
            ServifyFilledButton(
                text = stringResource(R.string.common_done),
                onClick = onDismiss,
                modifier = Modifier.weight(1f)
            )
        }
    }
}
