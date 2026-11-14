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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
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
    publicationId: String,
    onGoToMyPublications: () -> Unit,
    onGoToHome: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: PublicationPublishedViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val pub = state.publication

    val titleText = pub?.title?.ifBlank { null } ?: "Servicio registrado"
    val categorySubcategoryLocation = buildString {
        if (pub != null) {
            append(pub.category)
            if (pub.subcategory.isNotBlank()) append(" • ").append(pub.subcategory)
            val cityStr = pub.city.ifBlank { pub.locationName }
            if (cityStr.isNotBlank()) append(" • ").append(cityStr)
        } else {
            append("Servicio • Armenia, Quindío")
        }
    }
    val priceText = pub?.let { p ->
        p.priceRange.ifBlank {
            if (p.price > 0) "$${"%,d".format(p.price.toInt()).replace(',', '.')} COP / ${p.priceUnit}" else ""
        }
    } ?: ""

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
                    .background(MaterialTheme.colorScheme.secondaryContainer),
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
                text = "¡Tu publicación ha sido enviada!",
                style = ServifyTextStyle.TitleTight.copy(fontSize = 24.sp),
                color = ServifyOnSurface
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Tu servicio se registró correctamente y fue enviado a revisión. En pocos minutos estará visible para todos los clientes en el Marketplace de Servify.",
                style = ServifyTextStyle.SmallRelaxed,
                color = ServifyOnSurfaceVariant
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Resumen de la publicación enviada
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
                                text = "OFERTA REGISTRADA",
                                style = ServifyTextStyle.Caption.copy(
                                    color = ServifyPrimary,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                        }

                        Text(
                            text = "ID: #${pub?.id ?: publicationId}",
                            style = ServifyTextStyle.Caption,
                            color = ServifyOnSurfaceVariant
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = titleText,
                        style = ServifyTextStyle.SmallMedium.copy(fontWeight = FontWeight.Bold),
                        color = ServifyOnSurface
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = categorySubcategoryLocation,
                        style = ServifyTextStyle.Caption,
                        color = ServifyOnSurfaceVariant
                    )

                    if (priceText.isNotBlank()) {
                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = priceText,
                            style = ServifyTextStyle.SmallMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = ServifyPrimary
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            ServifyFilledButton(
                text = "Ir a Mis Publicaciones",
                icon = Icons.Default.List,
                onClick = onGoToMyPublications,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            ServifyOutlinedButton(
                text = "Volver al Inicio",
                icon = Icons.Default.Home,
                onClick = onGoToHome,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
