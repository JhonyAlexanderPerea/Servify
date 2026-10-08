package co.edu.uniquindio.servify.features.profile

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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.EventNote
import androidx.compose.material.icons.filled.ListAlt
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Star
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
import co.edu.uniquindio.servify.features.home.component.LevelBadge
import co.edu.uniquindio.servify.features.home.model.HomeMockData
import co.edu.uniquindio.servify.ui.theme.ServifyGold
import co.edu.uniquindio.servify.ui.theme.ServifyOnSurface
import co.edu.uniquindio.servify.ui.theme.ServifyOnSurfaceVariant
import co.edu.uniquindio.servify.ui.theme.ServifyOutlineVariant
import co.edu.uniquindio.servify.ui.theme.ServifyPrimary
import co.edu.uniquindio.servify.ui.theme.ServifySurface
import co.edu.uniquindio.servify.ui.theme.ServifySurfaceVariant
import co.edu.uniquindio.servify.ui.theme.ServifyTertiary
import co.edu.uniquindio.servify.ui.theme.ServifyTextStyle
import co.edu.uniquindio.servify.ui.theme.ServifyTheme

@Composable
fun ProfileScreen(
    onNavigateToCreatePublication: () -> Unit = {},
    onLogoutClick: () -> Unit = {},
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
                Text(
                    text = "Mi Perfil & Reputación",
                    style = ServifyTextStyle.TitleTight.copy(fontSize = 22.sp),
                    color = MaterialTheme.colorScheme.onBackground
                )

                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(ServifySurfaceVariant)
                        .clickable { /* Editar perfil */ },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Editar",
                        tint = ServifyOnSurface,
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
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 1. TARJETA CABECERA DE PERFIL CON AVATAR Y PUNTOS
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, ServifyOutlineVariant)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(60.dp)
                                    .clip(CircleShape)
                                    .background(ServifyPrimary),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = HomeMockData.currentUserInitials,
                                    style = ServifyTextStyle.Title.copy(
                                        color = Color.White,
                                        fontSize = 22.sp
                                    )
                                )
                            }

                            Spacer(modifier = Modifier.width(14.dp))

                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = "${HomeMockData.currentUserName} Ramírez",
                                        style = ServifyTextStyle.TitleTight.copy(fontSize = 18.sp),
                                        color = ServifyOnSurface
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    LevelBadge(level = "Experto")
                                }

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Place,
                                        contentDescription = null,
                                        tint = ServifyOnSurfaceVariant,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(2.dp))
                                    Text(
                                        text = HomeMockData.currentUserCity,
                                        style = ServifyTextStyle.Small,
                                        color = ServifyOnSurfaceVariant
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Estadísticas de Puntos y Trabajos
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(14.dp))
                                .background(ServifySurfaceVariant)
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceAround
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "1.240",
                                    style = ServifyTextStyle.SmallMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = ServifyPrimary
                                    )
                                )
                                Text(
                                    text = "Puntos Servify",
                                    style = ServifyTextStyle.Caption,
                                    color = ServifyOnSurfaceVariant
                                )
                            }

                            Box(
                                modifier = Modifier
                                    .width(1.dp)
                                    .height(28.dp)
                                    .background(ServifyOutlineVariant)
                            )

                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "23",
                                    style = ServifyTextStyle.SmallMedium.copy(fontWeight = FontWeight.Bold),
                                    color = ServifyOnSurface
                                )
                                Text(
                                    text = "Trabajos",
                                    style = ServifyTextStyle.Caption,
                                    color = ServifyOnSurfaceVariant
                                )
                            }

                            Box(
                                modifier = Modifier
                                    .width(1.dp)
                                    .height(28.dp)
                                    .background(ServifyOutlineVariant)
                            )

                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Star,
                                        contentDescription = null,
                                        tint = ServifyGold,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(2.dp))
                                    Text(
                                        text = "4.8",
                                        style = ServifyTextStyle.SmallMedium.copy(fontWeight = FontWeight.Bold),
                                        color = ServifyOnSurface
                                    )
                                }
                                Text(
                                    text = "Rating",
                                    style = ServifyTextStyle.Caption,
                                    color = ServifyOnSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }

            // 2. ÍNDICE DE CONFIANZA CARD
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFE8FFF8)),
                    border = androidx.compose.foundation.BorderStroke(1.dp, ServifyTertiary.copy(alpha = 0.3f))
                ) {
                    Row(
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(ServifyTertiary),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Shield,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(22.dp)
                                )
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Column {
                                Text(
                                    text = "Índice de Confianza",
                                    style = ServifyTextStyle.SmallMedium.copy(fontWeight = FontWeight.Bold),
                                    color = ServifyTertiary
                                )
                                Text(
                                    text = "91% • Perfil Verificado y Seguro",
                                    style = ServifyTextStyle.Caption,
                                    color = ServifyOnSurfaceVariant
                                )
                            }
                        }

                        Icon(
                            imageVector = Icons.Default.ChevronRight,
                            contentDescription = null,
                            tint = ServifyTertiary
                        )
                    }
                }
            }

            // 3. SECCIÓN DE LOGROS E INSIGNIAS
            item {
                Column {
                    Text(
                        text = "Insignias y Logros Alcanzados",
                        style = ServifyTextStyle.TitleTight.copy(fontSize = 18.sp),
                        color = ServifyOnSurface,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        AchievementBadgeChip(
                            title = "Usuario Confiable",
                            icon = Icons.Default.Shield,
                            color = ServifyTertiary,
                            modifier = Modifier.weight(1f)
                        )

                        AchievementBadgeChip(
                            title = "10 Verificados",
                            icon = Icons.Default.WorkspacePremium,
                            color = Color(0xFFB45309),
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            // 4. MENÚ DE OPCIONES DE LA CUENTA
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = androidx.compose.foundation.BorderStroke(1.dp, ServifyOutlineVariant)
                ) {
                    Column(modifier = Modifier.padding(vertical = 8.dp)) {
                        ProfileMenuItem(
                            icon = Icons.Default.ListAlt,
                            title = "Mis Publicaciones de Servicios",
                            onClick = onNavigateToCreatePublication
                        )

                        ProfileMenuItem(
                            icon = Icons.Default.EventNote,
                            title = "Mi Agenda y Citas Programadas",
                            onClick = { }
                        )

                        ProfileMenuItem(
                            icon = Icons.Default.Logout,
                            title = "Cerrar Sesión",
                            textColor = Color(0xFFBA1A1A),
                            onClick = onLogoutClick
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun AchievementBadgeChip(
    title: String,
    icon: ImageVector,
    color: Color,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(color.copy(alpha = 0.12f))
            .padding(horizontal = 10.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = color,
            modifier = Modifier.size(18.dp)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = title,
            style = ServifyTextStyle.Caption.copy(fontWeight = FontWeight.Bold),
            color = color
        )
    }
}

@Composable
private fun ProfileMenuItem(
    icon: ImageVector,
    title: String,
    textColor: Color = ServifyOnSurface,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = textColor,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = title,
                style = ServifyTextStyle.SmallMedium,
                color = textColor
            )
        }

        Icon(
            imageVector = Icons.Default.ChevronRight,
            contentDescription = null,
            tint = ServifyOnSurfaceVariant,
            modifier = Modifier.size(20.dp)
        )
    }
}

@Preview(name = "Profile Screen Light", showBackground = true)
@Composable
fun ProfileScreenPreviewLight() {
    ServifyTheme(darkTheme = false) {
        ProfileScreen()
    }
}

@Preview(name = "Profile Screen Dark", showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun ProfileScreenPreviewDark() {
    ServifyTheme(darkTheme = true) {
        ProfileScreen()
    }
}
