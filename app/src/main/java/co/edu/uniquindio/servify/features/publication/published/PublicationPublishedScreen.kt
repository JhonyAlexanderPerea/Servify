package co.edu.uniquindio.servify.features.publication.published

import android.content.res.Configuration
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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
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
import co.edu.uniquindio.servify.features.publication.component.PublicationStatusBadge
import co.edu.uniquindio.servify.ui.components.button.ServifyFilledButton
import co.edu.uniquindio.servify.ui.components.button.ServifyOutlinedButton
import co.edu.uniquindio.servify.ui.theme.ServifyOnSurface
import co.edu.uniquindio.servify.ui.theme.ServifyOnSurfaceVariant
import co.edu.uniquindio.servify.ui.theme.ServifyPrimary
import co.edu.uniquindio.servify.ui.theme.ServifyPrimaryContainer
import co.edu.uniquindio.servify.ui.theme.ServifySurface
import co.edu.uniquindio.servify.ui.theme.ServifySurfaceVariant
import co.edu.uniquindio.servify.ui.theme.ServifyTertiary
import co.edu.uniquindio.servify.ui.theme.ServifyTextStyle
import co.edu.uniquindio.servify.ui.theme.ServifyTheme

@Composable
fun PublicationPublishedScreen(
    publicationId: String = "pub_1",
    onDoneClick: () -> Unit = {},
    onGoToMyPublications: () -> Unit = onDoneClick,
    onGoToHome: () -> Unit = onDoneClick,
    modifier: Modifier = Modifier,
    viewModel: PublicationPublishedViewModel = hiltViewModel()
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
                    text = stringResource(R.string.publication_my_publications_title),
                    onClick = onGoToMyPublications,
                    modifier = Modifier.fillMaxWidth()
                )
                ServifyOutlinedButton(
                    text = stringResource(R.string.nav_home),
                    onClick = onGoToHome,
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
            val pub = state.publication
            val currentId = pub?.id ?: publicationId

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
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = "Éxito",
                        tint = ServifyTertiary,
                        modifier = Modifier.size(56.dp)
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = stringResource(R.string.publication_published_success_title),
                    style = ServifyTextStyle.TitleTight.copy(fontSize = 24.sp),
                    color = ServifyOnSurface
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = stringResource(R.string.publication_published_success_desc),
                    style = ServifyTextStyle.SmallRelaxed,
                    color = ServifyOnSurfaceVariant
                )

                Spacer(modifier = Modifier.height(24.dp))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = ServifySurfaceVariant)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .clip(CircleShape)
                                    .background(ServifyPrimaryContainer)
                                    .padding(horizontal = 10.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = stringResource(R.string.publication_offer_registered),
                                    style = ServifyTextStyle.Caption.copy(
                                        color = ServifyPrimary,
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                            }

                            Text(
                                text = "ID: #$currentId",
                                style = ServifyTextStyle.Caption,
                                color = ServifyOnSurfaceVariant
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = pub?.title ?: "Servicio registrado",
                            style = ServifyTextStyle.SmallMedium.copy(fontWeight = FontWeight.Bold),
                            color = ServifyOnSurface
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        if (pub != null) {
                            PublicationStatusBadge(status = pub.status)
                        }
                    }
                }
            }
        }
    }
}

@Preview(name = "Publication Published Light", showBackground = true)
@Composable
fun PublicationPublishedPreviewLight() {
    ServifyTheme(darkTheme = false) {
        PublicationPublishedScreen()
    }
}
