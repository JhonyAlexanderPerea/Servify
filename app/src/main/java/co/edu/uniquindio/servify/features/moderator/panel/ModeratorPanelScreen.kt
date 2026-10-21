package co.edu.uniquindio.servify.features.moderator.panel

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import co.edu.uniquindio.servify.di.AppContainer
import co.edu.uniquindio.servify.ui.components.button.ServifyOutlinedButton
import co.edu.uniquindio.servify.ui.theme.ServifyOnSurface
import co.edu.uniquindio.servify.ui.theme.ServifyOnSurfaceVariant
import co.edu.uniquindio.servify.ui.theme.ServifySurface
import co.edu.uniquindio.servify.ui.theme.ServifyTextStyle

/**
 * Destino temporal del flujo de moderador (pantalla 22). Permite probar de punta a punta
 * "login con cuenta de moderador -> panel -> cerrar sesión". La tarea 27 lo reemplaza
 * por el panel real (métricas + lista de pendientes).
 */
@Composable
fun ModeratorPanelScreen(
    onLogoutClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val moderator by AppContainer.authRepository.currentUser.collectAsState(initial = null)

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(ServifySurface)
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Panel de moderación",
            style = ServifyTextStyle.Title,
            color = ServifyOnSurface
        )

        Text(
            text = moderator?.email ?: "",
            style = ServifyTextStyle.Small,
            color = ServifyOnSurfaceVariant
        )

        ServifyOutlinedButton(
            text = "Cerrar sesión",
            onClick = onLogoutClick,
            modifier = Modifier.fillMaxWidth()
        )
    }
}
