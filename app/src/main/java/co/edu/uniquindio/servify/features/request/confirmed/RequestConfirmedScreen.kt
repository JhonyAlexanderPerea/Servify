package co.edu.uniquindio.servify.features.request.confirmed

import android.content.res.Configuration
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Home
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import co.edu.uniquindio.servify.ui.components.button.ServifyFilledButton
import co.edu.uniquindio.servify.ui.components.button.ServifyOutlinedButton
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
fun RequestConfirmedScreen(
    requestId: String,
    onGoToAgenda: () -> Unit,
    onGoToFeed: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .background(ServifySurface)
            .statusBarsPadding()
            .navigationBarsPadding()
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(90.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFDCFCE7)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = "Éxito",
                    tint = ServifyTertiary,
                    modifier = Modifier.size(56.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "¡Solicitud enviada al proveedor!",
                style = ServifyTextStyle.TitleTight.copy(fontSize = 24.sp),
                color = ServifyOnSurface
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "El proveedor Juan Carlos Mejía ha recibido tu solicitud. Te notificaremos tan pronto acepte la cita o si propone un cambio de horario.",
                style = ServifyTextStyle.SmallRelaxed,
                color = ServifyOnSurfaceVariant
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Ficha de resumen de la cita
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, ServifyOutlineVariant)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "RESUMEN DE RESERVA",
                            style = ServifyTextStyle.Caption.copy(
                                color = ServifyPrimary,
                                fontWeight = FontWeight.Bold
                            )
                        )

                        Text(
                            text = "Ref: #$requestId",
                            style = ServifyTextStyle.Caption,
                            color = ServifyOnSurfaceVariant
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "Plomería residencial y comercial",
                        style = ServifyTextStyle.SmallMedium.copy(fontWeight = FontWeight.Bold),
                        color = ServifyOnSurface
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "Proveedor: Juan Carlos Mejía",
                        style = ServifyTextStyle.Small,
                        color = ServifyOnSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(ServifySurfaceVariant)
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Horario programado:",
                            style = ServifyTextStyle.Caption,
                            color = ServifyOnSurfaceVariant
                        )
                        Text(
                            text = "Mañana a las 2:00 PM",
                            style = ServifyTextStyle.Caption.copy(
                                fontWeight = FontWeight.Bold,
                                color = ServifyTertiary
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            ServifyFilledButton(
                text = "Ver en Mi Agenda",
                icon = Icons.Default.CalendarMonth,
                onClick = onGoToAgenda,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            ServifyOutlinedButton(
                text = "Volver al Feed",
                icon = Icons.Default.Home,
                onClick = onGoToFeed,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Preview(name = "Request Confirmed Light", showBackground = true)
@Composable
fun RequestConfirmedPreviewLight() {
    ServifyTheme(darkTheme = false) {
        RequestConfirmedScreen(
            requestId = "req_101",
            onGoToAgenda = {},
            onGoToFeed = {}
        )
    }
}

@Preview(name = "Request Confirmed Dark", showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun RequestConfirmedPreviewDark() {
    ServifyTheme(darkTheme = true) {
        RequestConfirmedScreen(
            requestId = "req_101",
            onGoToAgenda = {},
            onGoToFeed = {}
        )
    }
}
