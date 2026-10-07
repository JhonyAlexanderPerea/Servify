package co.edu.uniquindio.servify.ui.components.button

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import co.edu.uniquindio.servify.ui.theme.ServifyDisabledContent
import co.edu.uniquindio.servify.ui.theme.ServifyOnSurface
import co.edu.uniquindio.servify.ui.theme.ServifyOutline
import co.edu.uniquindio.servify.ui.theme.ServifyPrimary
import co.edu.uniquindio.servify.ui.theme.ServifySurfaceVariant
import co.edu.uniquindio.servify.ui.theme.ServifyTextStyle

// -----------------------------------------
// FilledButton del mockup:
// px-6 py-3 · text-base font-medium · rounded-full · icono 18 + gap 8
// (alto = 48dp). Para que ocupe todo el ancho, pasar Modifier.fillMaxWidth().
// -----------------------------------------

@Composable
fun ServifyFilledButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    enabled: Boolean = true
) {

    Button(
        onClick = onClick,
        modifier = modifier.height(48.dp),
        enabled = enabled,
        shape = CircleShape,
        contentPadding = PaddingValues(horizontal = 24.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = ServifyPrimary,
            contentColor = Color.White,
            disabledContainerColor = ServifySurfaceVariant,
            disabledContentColor = ServifyDisabledContent
        )
    ) {

        ButtonContent(
            text = text,
            icon = icon
        )
    }
}

// -----------------------------------------
// OutlinedButton del mockup:
// borde 1dp #787680 · texto azul · 48dp de alto
// -----------------------------------------

@Composable
fun ServifyOutlinedButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    enabled: Boolean = true
) {

    OutlinedButton(
        onClick = onClick,
        modifier = modifier.height(48.dp),
        enabled = enabled,
        shape = CircleShape,
        border = BorderStroke(1.dp, ServifyOutline),
        contentPadding = PaddingValues(horizontal = 24.dp),
        colors = ButtonDefaults.outlinedButtonColors(
            contentColor = ServifyPrimary
        )
    ) {

        ButtonContent(
            text = text,
            icon = icon
        )
    }
}

// -----------------------------------------
// TextButton del mockup:
// px-3 py-2 · text-sm font-medium · 36dp de alto
// (se dibuja a mano para que el alto sea exactamente 36dp;
// el TextButton de M3 lo infla a 48dp por accesibilidad)
// -----------------------------------------

@Composable
fun ServifyTextButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    color: Color = ServifyPrimary
) {

    Box(
        modifier = modifier
            .height(36.dp)
            .clip(CircleShape)
            .clickable(
                role = Role.Button,
                onClick = onClick
            )
            .padding(horizontal = 12.dp),
        contentAlignment = Alignment.Center
    ) {

        Text(
            text = text,
            style = ServifyTextStyle.SmallMedium,
            color = color
        )
    }
}

// -----------------------------------------
// Botón circular de 40x40 (flecha atrás de las top bars)
// -----------------------------------------

@Composable
fun ServifyIconButton(
    icon: ImageVector,
    contentDescription: String?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {

    Box(
        modifier = modifier
            .size(40.dp)
            .clip(CircleShape)
            .clickable(
                role = Role.Button,
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {

        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            tint = ServifyOnSurface,
            modifier = Modifier.size(24.dp)
        )
    }
}

// -----------------------------------------
// Contenido común: [icono 18dp] [gap 8dp] [texto 16sp medium]
// -----------------------------------------

@Composable
private fun ButtonContent(
    text: String,
    icon: ImageVector?
) {

    if (icon != null) {

        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(18.dp)
        )

        Spacer(
            modifier = Modifier.width(8.dp)
        )
    }

    Text(
        text = text,
        style = ServifyTextStyle.BodyMedium
    )
}
