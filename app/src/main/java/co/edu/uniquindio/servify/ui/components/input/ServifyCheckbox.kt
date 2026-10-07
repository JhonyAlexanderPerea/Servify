package co.edu.uniquindio.servify.ui.components.input

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import co.edu.uniquindio.servify.ui.theme.ServifyOutline
import co.edu.uniquindio.servify.ui.theme.ServifyPrimary

/**
 * Checkbox del mockup: 20dp, esquinas de 4dp, borde de 2dp.
 * Es solo visual; el toggle se hace con Modifier.toggleable en la fila
 * que lo contiene (así también se puede tocar el texto).
 */
@Composable
fun ServifyCheckbox(
    checked: Boolean,
    modifier: Modifier = Modifier
) {

    val shape = RoundedCornerShape(4.dp)

    Box(
        modifier = modifier
            .size(20.dp)
            .clip(shape)
            .background(
                if (checked) ServifyPrimary else Color.Transparent
            )
            .border(
                width = 2.dp,
                color = if (checked) ServifyPrimary else ServifyOutline,
                shape = shape
            ),
        contentAlignment = Alignment.Center
    ) {

        if (checked) {

            Icon(
                imageVector = Icons.Outlined.Check,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(14.dp)
            )
        }
    }
}
