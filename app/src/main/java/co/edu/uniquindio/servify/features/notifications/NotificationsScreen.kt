package co.edu.uniquindio.servify.features.notifications

import android.content.res.Configuration
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.ChatBubble
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.EventAvailable
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import co.edu.uniquindio.servify.ui.theme.ServifyOnSurface
import co.edu.uniquindio.servify.ui.theme.ServifyOnSurfaceVariant
import co.edu.uniquindio.servify.ui.theme.ServifyOutlineVariant
import co.edu.uniquindio.servify.ui.theme.ServifyPrimary
import co.edu.uniquindio.servify.ui.theme.ServifySurface
import co.edu.uniquindio.servify.ui.theme.ServifySurfaceVariant
import co.edu.uniquindio.servify.ui.theme.ServifyTertiary
import co.edu.uniquindio.servify.ui.theme.ServifyTextStyle
import co.edu.uniquindio.servify.ui.theme.ServifyTheme

data class NotificationItemData(
    val id: String,
    val title: String,
    val message: String,
    val timeAgo: String,
    val isRead: Boolean,
    val icon: ImageVector,
    val iconColor: Color
)

object NotificationsMockData {
    val sampleNotifications = listOf(
        NotificationItemData(
            id = "1",
            title = "Recordatorio de Cita Próxima",
            message = "Tu servicio de Plomería Residencial con Juan Carlos está programado para Hoy a las 2:00 PM.",
            timeAgo = "Hace 10 min",
            isRead = false,
            icon = Icons.Default.Alarm,
            iconColor = Color(0xFFB45309)
        ),
        NotificationItemData(
            id = "2",
            title = "Publicación Verificada",
            message = "Tu anuncio 'Plomería y reparación de fuga' fue revisado y aprobado exitosamente por la IA.",
            timeAgo = "Hace 30 min",
            isRead = false,
            icon = Icons.Default.CheckCircle,
            iconColor = ServifyTertiary
        ),
        NotificationItemData(
            id = "3",
            title = "Nuevo Cliente Interesado",
            message = "Sebastián García guardó tu servicio 'Soporte Técnico' en sus favoritos.",
            timeAgo = "Hace 1 hora",
            isRead = false,
            icon = Icons.Default.Favorite,
            iconColor = Color(0xFFEA4C89)
        ),
        NotificationItemData(
            id = "4",
            title = "Cita Confirmada",
            message = "Luis Fernando Torres confirmó tu reserva para el Viernes 9:00 AM.",
            timeAgo = "Hace 3 horas",
            isRead = true,
            icon = Icons.Default.EventAvailable,
            iconColor = ServifyPrimary
        ),
        NotificationItemData(
            id = "5",
            title = "¡Insignia Desbloqueada!",
            message = "Obtuviste la insignia 'Usuario Confiable' tras superar el 90% de Índice de Confianza.",
            timeAgo = "Ayer",
            isRead = true,
            icon = Icons.Default.WorkspacePremium,
            iconColor = Color(0xFFFFD700)
        ),
        NotificationItemData(
            id = "6",
            title = "Nuevo Comentario en tu Anuncio",
            message = "Laura Patricia Gómez dejó una reseña de 5 estrellas en tu perfil de servicios.",
            timeAgo = "Hace 2 días",
            isRead = true,
            icon = Icons.Default.ChatBubble,
            iconColor = ServifyPrimary
        )
    )
}

@Composable
fun NotificationsScreen(
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .background(ServifySurface),
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Centro de Alertas",
                        style = ServifyTextStyle.TitleTight.copy(fontSize = 22.sp),
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        text = "Actualizaciones de tus citas y publicaciones",
                        style = ServifyTextStyle.Small,
                        color = ServifyOnSurfaceVariant
                    )
                }

                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(ServifySurfaceVariant),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Notifications,
                        contentDescription = null,
                        tint = ServifyPrimary,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    ) { paddingValues ->

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(NotificationsMockData.sampleNotifications, key = { it.id }) { item ->
                NotificationCardItem(notification = item)
            }
        }
    }
}

@Composable
private fun NotificationCardItem(
    notification: NotificationItemData
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (notification.isRead) MaterialTheme.colorScheme.surface else Color(0xFFE8EEFF)
        ),
        border = BorderStroke(
            1.dp,
            if (notification.isRead) ServifyOutlineVariant else ServifyPrimary.copy(alpha = 0.3f)
        )
    ) {
        Row(
            modifier = Modifier
                .padding(14.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.Top
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(notification.iconColor.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = notification.icon,
                    contentDescription = null,
                    tint = notification.iconColor,
                    modifier = Modifier.size(22.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = notification.title,
                        style = ServifyTextStyle.SmallMedium.copy(fontWeight = FontWeight.Bold),
                        color = ServifyOnSurface,
                        modifier = Modifier.weight(1f)
                    )

                    Text(
                        text = notification.timeAgo,
                        style = ServifyTextStyle.Caption,
                        color = ServifyOnSurfaceVariant
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = notification.message,
                    style = ServifyTextStyle.SmallRelaxed,
                    color = ServifyOnSurfaceVariant
                )
            }
        }
    }
}

@Preview(name = "Notifications Screen Light", showBackground = true)
@Composable
fun NotificationsScreenPreviewLight() {
    ServifyTheme(darkTheme = false) {
        NotificationsScreen()
    }
}

@Preview(name = "Notifications Screen Dark", showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun NotificationsScreenPreviewDark() {
    ServifyTheme(darkTheme = true) {
        NotificationsScreen()
    }
}
