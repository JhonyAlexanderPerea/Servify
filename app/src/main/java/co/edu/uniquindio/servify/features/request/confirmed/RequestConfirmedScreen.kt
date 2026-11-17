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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EventAvailable
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import co.edu.uniquindio.servify.R
import co.edu.uniquindio.servify.ui.components.button.ServifyFilledButton
import co.edu.uniquindio.servify.ui.components.button.ServifyOutlinedButton
import co.edu.uniquindio.servify.ui.theme.ServifyOnSurface
import co.edu.uniquindio.servify.ui.theme.ServifyOnSurfaceVariant
import co.edu.uniquindio.servify.ui.theme.ServifyOutlineVariant
import co.edu.uniquindio.servify.ui.theme.ServifyPrimary
import co.edu.uniquindio.servify.ui.theme.ServifyPrimaryContainer
import co.edu.uniquindio.servify.ui.theme.ServifySurface
import co.edu.uniquindio.servify.ui.theme.ServifySurfaceVariant
import co.edu.uniquindio.servify.ui.theme.ServifyTertiary
import co.edu.uniquindio.servify.ui.theme.ServifyTextStyle
import co.edu.uniquindio.servify.ui.theme.ServifyTheme

@Composable
fun RequestConfirmedScreen(
    requestId: String = "req_1",
    onDoneClick: () -> Unit = {},
    onGoToMyRequests: () -> Unit = onDoneClick,
    onGoToFeed: () -> Unit = onDoneClick,
    modifier: Modifier = Modifier,
    viewModel: RequestConfirmedViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .background(ServifySurface)
            .statusBarsPadding(),
        bottomBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                ServifyFilledButton(
                    text = stringResource(R.string.request_my_requests_title),
                    onClick = onGoToMyRequests,
                    modifier = Modifier.fillMaxWidth()
                )
                ServifyOutlinedButton(
                    text = stringResource(R.string.nav_home),
                    onClick = onGoToFeed,
                    modifier = Modifier.fillMaxWidth()
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
                CircularProgressIndicator(color = ServifyPrimary)
            }
        } else {
            val req = state.request
            val currentId = req?.id ?: requestId
            val providerName = req?.providerId ?: "Proveedor Servify"
            val serviceTitle = req?.publicationId ?: "Servicio contratado"

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 24.dp, vertical = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {

                Box(
                    modifier = Modifier
                        .size(96.dp)
                        .clip(CircleShape)
                        .background(ServifyPrimaryContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.EventAvailable,
                        contentDescription = "Confirmación",
                        tint = ServifyPrimary,
                        modifier = Modifier.size(56.dp)
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = stringResource(R.string.request_confirmed_title),
                    style = ServifyTextStyle.TitleTight.copy(fontSize = 24.sp),
                    color = ServifyOnSurface
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = stringResource(R.string.request_confirmed_desc_format, providerName),
                    style = ServifyTextStyle.SmallRelaxed,
                    color = ServifyOnSurfaceVariant
                )

                Spacer(modifier = Modifier.height(24.dp))

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
                                text = stringResource(R.string.request_summary_title),
                                style = ServifyTextStyle.Caption.copy(
                                    color = ServifyPrimary,
                                    fontWeight = FontWeight.Bold
                                )
                            )

                            Text(
                                text = "Ref: #$currentId",
                                style = ServifyTextStyle.Caption,
                                color = ServifyOnSurfaceVariant
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = serviceTitle,
                            style = ServifyTextStyle.SmallMedium.copy(fontWeight = FontWeight.Bold),
                            color = ServifyOnSurface
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = stringResource(R.string.request_provider_format, providerName),
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
                                text = stringResource(R.string.request_status_label),
                                style = ServifyTextStyle.Caption,
                                color = ServifyOnSurfaceVariant
                            )
                            Text(
                                text = stringResource(R.string.request_status_pending_confirmation),
                                style = ServifyTextStyle.Caption.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = ServifyTertiary
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}

@Preview(name = "Request Confirmed Light", showBackground = true)
@Composable
fun RequestConfirmedPreviewLight() {
    ServifyTheme(darkTheme = false) {
        RequestConfirmedScreen()
    }
}
