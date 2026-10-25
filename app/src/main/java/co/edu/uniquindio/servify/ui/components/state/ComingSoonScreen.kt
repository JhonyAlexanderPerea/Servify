package co.edu.uniquindio.servify.ui.components.state

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Construction
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import co.edu.uniquindio.servify.ui.components.button.ServifyIconButton
import co.edu.uniquindio.servify.ui.theme.ServifyTextStyle

/**
 * Destino provisional para secciones del Perfil cuya pantalla se implementa en otra tarjeta del
 * kanban (Mis solicitudes, Logros, Estadísticas, Configuración). La ruta ya existe en el grafo:
 * cuando llegue la pantalla real solo se cambia el `composable` de esa ruta.
 */
@Composable
fun ComingSoonScreen(
    title: String,
    description: String,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            ServifyIconButton(
                icon = Icons.AutoMirrored.Outlined.ArrowBack,
                contentDescription = "Atrás",
                onClick = onBack
            )
            Text(
                text = title,
                style = ServifyTextStyle.Title,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(start = 4.dp)
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterVertically),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Outlined.Construction,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = "Próximamente",
                style = ServifyTextStyle.Title,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = description,
                style = ServifyTextStyle.SmallRelaxed,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }
    }
}
