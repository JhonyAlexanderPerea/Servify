package co.edu.uniquindio.servify.ui.components.display

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import co.edu.uniquindio.servify.R
import co.edu.uniquindio.servify.ui.theme.ServifyExtra


@Composable
fun ServifyLogo(
    modifier: Modifier = Modifier,
    size: Dp = 64.dp,
    contentDescription: String = "Servify Logo"
) {

    val painter = painterResource(R.drawable.logo)

    Image(
        painter = painter,
        contentDescription = contentDescription,
        modifier = modifier.size(size)
    )
}
