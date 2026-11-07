package co.edu.uniquindio.servify.ui.components.chips

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import co.edu.uniquindio.servify.ui.motion.pressScale
import co.edu.uniquindio.servify.ui.theme.ServifyTextStyle

/**
 * Chip de filtro único de la app. Reemplaza los chips dibujados a mano con `Box + clickable`:
 *  - colores del esquema (el texto seleccionado usa onPrimary, legible en claro y oscuro),
 *  - el color de fondo cambia con animación (lo hace FilterChip),
 *  - texto de una sola línea (nunca se parte "Mejor valorados" en dos),
 *  - área táctil de 48dp y rol accesible de Material.
 */
@Composable
fun ServifyChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    FilterChip(
        selected = selected,
        onClick = onClick,
        interactionSource = interactionSource,
        modifier = modifier.pressScale(interactionSource, pressedScale = 0.96f),
        shape = CircleShape,
        label = {
            Text(
                text = label,
                style = ServifyTextStyle.Caption,
                maxLines = 1,
                softWrap = false,
                overflow = TextOverflow.Ellipsis
            )
        },
        colors = FilterChipDefaults.filterChipColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
            labelColor = MaterialTheme.colorScheme.onSurfaceVariant,
            selectedContainerColor = MaterialTheme.colorScheme.primary,
            selectedLabelColor = MaterialTheme.colorScheme.onPrimary
        ),
        border = FilterChipDefaults.filterChipBorder(
            enabled = true,
            selected = selected,
            borderColor = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f),
            selectedBorderColor = MaterialTheme.colorScheme.primary
        )
    )
}
