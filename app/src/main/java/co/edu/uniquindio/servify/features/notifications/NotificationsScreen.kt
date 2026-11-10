package co.edu.uniquindio.servify.features.notifications

import android.content.res.Configuration
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.ChatBubble
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material.icons.filled.EventAvailable
import androidx.compose.material.icons.filled.NotificationsNone
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import co.edu.uniquindio.servify.domain.model.Notification
import co.edu.uniquindio.servify.domain.model.NotificationType
import co.edu.uniquindio.servify.ui.motion.ServifyMotion
import co.edu.uniquindio.servify.ui.theme.ServifyExtra
import co.edu.uniquindio.servify.ui.theme.ServifyTextStyle
import co.edu.uniquindio.servify.ui.theme.ServifyTheme

@Composable
fun NotificationsScreen(
    modifier: Modifier = Modifier,
    onNavigateToRoute: (String) -> Unit = {},
    viewModel: NotificationsViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val colors = MaterialTheme.colorScheme

    // El Scaffold del dashboard ya reserva la barra inferior (y consume su inset),
    // así que aquí solo se protege la barra de estado.
    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .background(colors.background)
            .statusBarsPadding(),
        containerColor = colors.background,
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Notificaciones",
                        style = ServifyTextStyle.TitleTight.copy(fontSize = 22.sp),
                        color = colors.onBackground
                    )
                    if (state.unreadCount > 0) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Box(
                            modifier = Modifier
                                .clip(CircleShape)
                                .background(colors.primary)
                                .padding(horizontal = 8.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "${state.unreadCount} nuevas",
                                style = ServifyTextStyle.Caption.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp
                                ),
                                color = colors.onPrimary,
                                maxLines = 1,
                                softWrap = false
                            )
                        }
                    }
                }
                Text(
                    text = "Novedades de tus publicaciones y solicitudes",
                    style = ServifyTextStyle.Small,
                    color = colors.onSurfaceVariant
                )
            }
        }
    ) { paddingValues ->

        if (state.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = colors.primary)
            }
        } else if (state.todayNotifications.isEmpty() && state.earlierNotifications.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = colors.surfaceContainerHigh)
                ) {
                    Column(
                        modifier = Modifier
                            .padding(24.dp)
                            .fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.NotificationsNone,
                            contentDescription = null,
                            tint = colors.onSurfaceVariant,
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "No tienes notificaciones",
                            style = ServifyTextStyle.SmallMedium.copy(fontWeight = FontWeight.Bold),
                            color = colors.onSurface
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Te avisaremos cuando haya novedades en tus publicaciones, solicitudes o insignias.",
                            style = ServifyTextStyle.Caption,
                            color = colors.onSurfaceVariant,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                if (state.todayNotifications.isNotEmpty()) {
                    item(key = "header_today") {
                        // "Marcar todas como leídas" vive en la fila de la sección, con texto completo
                        // (antes estaba en la cabecera y se partía en dos líneas: "Leí / das").
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Hoy (${state.todayNotifications.size})",
                                style = ServifyTextStyle.SmallMedium.copy(fontWeight = FontWeight.Bold),
                                color = colors.primary,
                                modifier = Modifier.weight(1f)
                            )
                            if (state.unreadCount > 0) {
                                MarkAllAsReadAction(onClick = viewModel::onMarkAllAsRead)
                            }
                        }
                    }

                    items(state.todayNotifications, key = { it.id }) { notification ->
                        NotificationCardItem(
                            notification = notification,
                            onClick = { viewModel.onNotificationClicked(notification, onNavigateToRoute) },
                            modifier = Modifier.animateItem()
                        )
                    }
                }

                if (state.earlierNotifications.isNotEmpty()) {
                    item(key = "header_earlier") {
                        Text(
                            text = "Anteriores (${state.earlierNotifications.size})",
                            style = ServifyTextStyle.SmallMedium.copy(fontWeight = FontWeight.Bold),
                            color = colors.onSurfaceVariant,
                            modifier = Modifier.padding(top = 10.dp, bottom = 2.dp)
                        )
                    }

                    items(state.earlierNotifications, key = { it.id }) { notification ->
                        NotificationCardItem(
                            notification = notification,
                            onClick = { viewModel.onNotificationClicked(notification, onNavigateToRoute) },
                            modifier = Modifier.animateItem()
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun MarkAllAsReadAction(onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .clip(CircleShape)
            .clickable(onClick = onClick)
            .padding(horizontal = 10.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Default.DoneAll,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(16.dp)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = "Marcar todas como leídas",
            style = ServifyTextStyle.Caption.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.primary,
            maxLines = 1,
            softWrap = false
        )
    }
}

@Composable
private fun NotificationCardItem(
    notification: Notification,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = MaterialTheme.colorScheme
    val (icon, iconColor) = getNotificationIconAndColor(notification.type)
    val timeAgo = formatTimeAgo(notification.createdAt)
    val unread = !notification.isRead

    // No leída: velo del color primario sobre la superficie (en claro es un azul pálido, en oscuro
    // un azul profundo) y el texto sigue siendo onSurface, legible en ambos temas. Al leerla, el
    // fondo y el borde se transicionan suavemente en vez de saltar.
    val container by animateColorAsState(
        targetValue = if (unread) colors.primaryContainer.copy(alpha = 0.45f).compositeOver(colors.surface)
        else colors.surface,
        animationSpec = tween(ServifyMotion.MEDIUM),
        label = "notificationContainer"
    )
    val borderColor by animateColorAsState(
        targetValue = if (unread) colors.primary.copy(alpha = 0.45f) else colors.outlineVariant.copy(alpha = 0.5f),
        animationSpec = tween(ServifyMotion.MEDIUM),
        label = "notificationBorder"
    )

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = container),
        border = BorderStroke(1.dp, borderColor)
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
                    .background(iconColor.copy(alpha = 0.18f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconColor,
                    modifier = Modifier.size(22.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // weight(fill = false): el título cede espacio, y el punto de "no leída"
                    // y la hora nunca se aplastan.
                    Text(
                        text = notification.title,
                        style = ServifyTextStyle.SmallMedium.copy(
                            fontWeight = if (unread) FontWeight.Bold else FontWeight.Medium
                        ),
                        color = colors.onSurface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f, fill = false)
                    )
                    if (unread) {
                        Spacer(modifier = Modifier.width(6.dp))
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(colors.primary)
                        )
                    }
                    Spacer(modifier = Modifier.weight(1f))
                    Text(
                        text = timeAgo,
                        style = ServifyTextStyle.Caption,
                        color = colors.onSurfaceVariant,
                        maxLines = 1,
                        softWrap = false
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = notification.message,
                    style = ServifyTextStyle.SmallRelaxed,
                    color = colors.onSurfaceVariant
                )
            }
        }
    }
}

/** Colores semánticos del tema (claro y oscuro); nada de hexadecimales fijos. */
@Composable
private fun getNotificationIconAndColor(type: NotificationType): Pair<ImageVector, Color> {
    val colors = MaterialTheme.colorScheme
    return when (type) {
        NotificationType.REQUEST_RECEIVED -> Icons.Default.EventAvailable to colors.primary
        NotificationType.REQUEST_ACCEPTED -> Icons.Default.CheckCircle to colors.secondary
        NotificationType.APPOINTMENT_REMINDER -> Icons.Default.Alarm to ServifyExtra.warning
        NotificationType.MODERATION_UPDATE -> Icons.Default.CheckCircle to colors.secondary
        NotificationType.ACHIEVEMENT_UNLOCKED -> Icons.Default.WorkspacePremium to ServifyExtra.gold
        NotificationType.REVIEW_PUBLISHED -> Icons.Default.ChatBubble to colors.primary
    }
}

private fun formatTimeAgo(createdAt: Long): String {
    val diffMinutes = ((System.currentTimeMillis() - createdAt) / 60000L).coerceAtLeast(0)
    return when {
        diffMinutes < 1 -> "Ahora mismo"
        diffMinutes < 60 -> "Hace $diffMinutes min"
        diffMinutes < 1440 -> "Hace ${diffMinutes / 60} h"
        else -> "Hace ${diffMinutes / 1440} d"
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
