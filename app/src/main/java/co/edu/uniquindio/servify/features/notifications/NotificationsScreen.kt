package co.edu.uniquindio.servify.features.notifications

import android.content.res.Configuration
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
import androidx.compose.foundation.layout.navigationBarsPadding
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import co.edu.uniquindio.servify.domain.model.Notification
import co.edu.uniquindio.servify.domain.model.NotificationType
import co.edu.uniquindio.servify.ui.theme.ServifyOnSurface
import co.edu.uniquindio.servify.ui.theme.ServifyOnSurfaceVariant
import co.edu.uniquindio.servify.ui.theme.ServifyOutlineVariant
import co.edu.uniquindio.servify.ui.theme.ServifyPrimary
import co.edu.uniquindio.servify.ui.theme.ServifySurface
import co.edu.uniquindio.servify.ui.theme.ServifySurfaceVariant
import co.edu.uniquindio.servify.ui.theme.ServifyTertiary
import co.edu.uniquindio.servify.ui.theme.ServifyTheme
import co.edu.uniquindio.servify.ui.theme.ServifyTextStyle

@Composable
fun NotificationsScreen(
    modifier: Modifier = Modifier,
    onNavigateToRoute: (String) -> Unit = {},
    viewModel: NotificationsViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .background(ServifySurface)
            .statusBarsPadding()
            .navigationBarsPadding(),
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Notificaciones",
                            style = ServifyTextStyle.TitleTight.copy(fontSize = 22.sp),
                            color = MaterialTheme.colorScheme.onBackground
                        )

                        if (state.unreadCount > 0) {
                            Spacer(modifier = Modifier.width(8.dp))
                            Box(
                                modifier = Modifier
                                    .clip(CircleShape)
                                    .background(ServifyPrimary)
                                    .padding(horizontal = 8.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = "${state.unreadCount} nuevas",
                                    style = ServifyTextStyle.Caption.copy(
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 11.sp
                                    )
                                )
                            }
                        }
                    }

                    Text(
                        text = "Actualizaciones de tus citas y publicaciones",
                        style = ServifyTextStyle.Small,
                        color = ServifyOnSurfaceVariant
                    )
                }

                if (state.unreadCount > 0) {
                    Row(
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(ServifySurfaceVariant)
                            .clickable { viewModel.onMarkAllAsRead() }
                            .padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.DoneAll,
                            contentDescription = "Marcar todas como leídas",
                            tint = ServifyPrimary,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Leídas",
                            style = ServifyTextStyle.Caption.copy(
                                fontWeight = FontWeight.Bold,
                                color = ServifyPrimary
                            )
                        )
                    }
                }
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
                CircularProgressIndicator(color = ServifyPrimary)
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
                    colors = CardDefaults.cardColors(containerColor = ServifySurfaceVariant)
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
                            tint = ServifyOnSurfaceVariant,
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "No tienes notificaciones",
                            style = ServifyTextStyle.SmallMedium.copy(fontWeight = FontWeight.Bold),
                            color = ServifyOnSurface
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Te avisaremos cuando haya actualizaciones sobre tus citas, reservas o insignias.",
                            style = ServifyTextStyle.Caption,
                            color = ServifyOnSurfaceVariant,
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
                // SECCIÓN "HOY"
                if (state.todayNotifications.isNotEmpty()) {
                    item {
                        Text(
                            text = "Hoy (${state.todayNotifications.size})",
                            style = ServifyTextStyle.SmallMedium.copy(fontWeight = FontWeight.Bold),
                            color = ServifyPrimary,
                            modifier = Modifier.padding(top = 4.dp, bottom = 2.dp)
                        )
                    }

                    items(state.todayNotifications, key = { it.id }) { notification ->
                        NotificationCardItem(
                            notification = notification,
                            onClick = {
                                viewModel.onNotificationClicked(notification, onNavigateToRoute)
                            }
                        )
                    }
                }

                // SECCIÓN "ANTERIORES"
                if (state.earlierNotifications.isNotEmpty()) {
                    item {
                        Text(
                            text = "Anteriores (${state.earlierNotifications.size})",
                            style = ServifyTextStyle.SmallMedium.copy(fontWeight = FontWeight.Bold),
                            color = ServifyOnSurfaceVariant,
                            modifier = Modifier.padding(top = 10.dp, bottom = 2.dp)
                        )
                    }

                    items(state.earlierNotifications, key = { it.id }) { notification ->
                        NotificationCardItem(
                            notification = notification,
                            onClick = {
                                viewModel.onNotificationClicked(notification, onNavigateToRoute)
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun NotificationCardItem(
    notification: Notification,
    onClick: () -> Unit
) {
    val (icon, iconColor) = getNotificationIconAndColor(notification.type)
    val timeAgo = formatTimeAgo(notification.createdAt)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (!notification.isRead) Color(0xFFE8EEFF) else MaterialTheme.colorScheme.surface
        ),
        border = BorderStroke(
            1.dp,
            if (!notification.isRead) ServifyPrimary.copy(alpha = 0.35f) else ServifyOutlineVariant
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
                    .background(iconColor.copy(alpha = 0.15f)),
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
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = notification.title,
                            style = ServifyTextStyle.SmallMedium.copy(
                                fontWeight = if (!notification.isRead) FontWeight.Bold else FontWeight.Medium
                            ),
                            color = ServifyOnSurface
                        )

                        if (!notification.isRead) {
                            Spacer(modifier = Modifier.width(6.dp))
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(ServifyPrimary)
                            )
                        }
                    }

                    Text(
                        text = timeAgo,
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

private fun getNotificationIconAndColor(type: NotificationType): Pair<ImageVector, Color> {
    return when (type) {
        NotificationType.REQUEST_RECEIVED -> Icons.Default.EventAvailable to ServifyPrimary
        NotificationType.REQUEST_ACCEPTED -> Icons.Default.CheckCircle to ServifyTertiary
        NotificationType.APPOINTMENT_REMINDER -> Icons.Default.Alarm to Color(0xFFB45309)
        NotificationType.MODERATION_UPDATE -> Icons.Default.CheckCircle to ServifyTertiary
        NotificationType.ACHIEVEMENT_UNLOCKED -> Icons.Default.WorkspacePremium to Color(0xFFFFD700)
        NotificationType.REVIEW_PUBLISHED -> Icons.Default.ChatBubble to ServifyPrimary
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
