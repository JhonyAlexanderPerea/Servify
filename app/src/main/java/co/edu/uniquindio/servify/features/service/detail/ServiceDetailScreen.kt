package co.edu.uniquindio.servify.features.service.detail

import android.content.res.Configuration
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
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.SearchOff
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import co.edu.uniquindio.servify.features.home.component.StarRatingRow
import co.edu.uniquindio.servify.features.service.component.CommentInput
import co.edu.uniquindio.servify.features.service.component.CommentItem
import co.edu.uniquindio.servify.features.service.component.ExpandableDescriptionCard
import co.edu.uniquindio.servify.features.service.component.ImageGalleryBanner
import co.edu.uniquindio.servify.features.service.component.ProviderCard
import co.edu.uniquindio.servify.features.service.component.ServiceDetailBottomBar
import co.edu.uniquindio.servify.features.service.component.ServiceDetailTopBar
import co.edu.uniquindio.servify.features.service.component.TrustSystemCard
import co.edu.uniquindio.servify.features.service.model.ServiceDetailMockData
import co.edu.uniquindio.servify.features.service.model.toCommentItemData
import co.edu.uniquindio.servify.features.service.model.toServiceDetailData
import co.edu.uniquindio.servify.ui.theme.ServifyOnSurface
import co.edu.uniquindio.servify.ui.theme.ServifyOnSurfaceVariant
import co.edu.uniquindio.servify.ui.theme.ServifyPrimary
import co.edu.uniquindio.servify.ui.theme.ServifySurface
import co.edu.uniquindio.servify.ui.theme.ServifySurfaceVariant
import co.edu.uniquindio.servify.ui.theme.ServifyTheme
import co.edu.uniquindio.servify.ui.theme.ServifyTextStyle

@Composable
fun ServiceDetailScreen(
    serviceId: String,
    onBackClick: () -> Unit,
    onBookClick: () -> Unit,
    modifier: Modifier = Modifier,
    onShareClick: () -> Unit = {},
    viewModel: ServiceDetailViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .background(ServifySurface)
            .statusBarsPadding()
            .navigationBarsPadding(),
        topBar = {
            ServiceDetailTopBar(
                onBackClick = onBackClick,
                isFavorite = state.service?.isFavorite ?: false,
                onFavoriteToggle = viewModel::onToggleInterest,
                onShareClick = onShareClick
            )
        },
        bottomBar = {
            state.service?.let { pub ->
                val detailData = pub.toServiceDetailData()
                ServiceDetailBottomBar(
                    priceRange = detailData.priceRange,
                    hourlyPrice = detailData.hourlyPrice,
                    onBookClick = onBookClick
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
        } else if (state.service == null) {
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
                            imageVector = Icons.Default.SearchOff,
                            contentDescription = null,
                            tint = ServifyOnSurfaceVariant,
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "Servicio no encontrado",
                            style = ServifyTextStyle.SmallMedium.copy(fontWeight = FontWeight.Bold),
                            color = ServifyOnSurface
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "El servicio solicitado no existe o ha sido eliminado del Marketplace.",
                            style = ServifyTextStyle.Caption,
                            color = ServifyOnSurfaceVariant,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        } else {
            val detailData = state.service!!.toServiceDetailData()
            val commentItems = state.comments.map { it.toCommentItemData() }

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // 1. GALERÍA DE IMÁGENES
                item {
                    ImageGalleryBanner(detail = detailData)
                }

                // 2. ENCABEZADO: TÍTULO, RATING, UBICACIÓN Y PRECIO
                item {
                    Column {
                        Text(
                            text = detailData.title,
                            style = ServifyTextStyle.TitleTight.copy(fontSize = 22.sp),
                            color = ServifyOnSurface
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            StarRatingRow(
                                rating = detailData.rating,
                                reviewCount = detailData.reviewCount
                            )

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Place,
                                    contentDescription = null,
                                    tint = ServifyOnSurfaceVariant,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = detailData.city,
                                    style = ServifyTextStyle.Small,
                                    color = ServifyOnSurfaceVariant
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .clip(CircleShape)
                                .background(ServifyPrimary.copy(alpha = 0.1f))
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = "Rango de precio: ",
                                style = ServifyTextStyle.Caption,
                                color = ServifyOnSurfaceVariant
                            )
                            Text(
                                text = detailData.priceRange,
                                style = ServifyTextStyle.SmallMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = ServifyPrimary
                                )
                            )
                        }
                    }
                }

                // 3. TARJETA DEL PROVEEDOR
                item {
                    ProviderCard(
                        detail = detailData,
                        onContactProviderClick = { /* Chat o llamada */ }
                    )
                }

                // 4. SISTEMA DE CONFIANZA (TRUST FACTORS)
                item {
                    TrustSystemCard(
                        trustIndex = detailData.trustIndex,
                        trustFactors = detailData.trustFactors
                    )
                }

                // 5. DESCRIPCIÓN DEL SERVICIO
                item {
                    ExpandableDescriptionCard(
                        description = detailData.description,
                        zone = detailData.zone,
                        city = detailData.city
                    )
                }

                // 6. ENCABEZADO DE COMENTARIOS
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Reseñas y opiniones (${commentItems.size})",
                            style = ServifyTextStyle.TitleTight.copy(fontSize = 18.sp),
                            color = ServifyOnSurface
                        )
                    }
                }

                // 7. LISTA DE COMENTARIOS REAJEADOS
                if (commentItems.isEmpty()) {
                    item {
                        Text(
                            text = "Sé el primero en dejar una opinión sobre este servicio.",
                            style = ServifyTextStyle.Caption,
                            color = ServifyOnSurfaceVariant,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    }
                } else {
                    items(commentItems, key = { it.id }) { comment ->
                        CommentItem(comment = comment)
                    }
                }

                // 8. ENTRADA PARA DEJAR COMENTARIOS
                item {
                    CommentInput(
                        onSendComment = { commentText, rating ->
                            viewModel.onCommentTextChanged(commentText)
                            viewModel.onRatingChanged(rating)
                            viewModel.onSubmitComment()
                        }
                    )
                }
            }
        }
    }
}

// -----------------------------------------------------------------------------
// PREVIEWS EN MODO CLARO Y OSCURO
// -----------------------------------------------------------------------------

@Preview(name = "Service Detail Light", showBackground = true)
@Composable
fun ServiceDetailScreenPreviewLight() {
    ServifyTheme(darkTheme = false) {
        ServiceDetailScreen(
            serviceId = "1",
            onBackClick = {},
            onBookClick = {}
        )
    }
}

@Preview(name = "Service Detail Dark", showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun ServiceDetailScreenPreviewDark() {
    ServifyTheme(darkTheme = true) {
        ServiceDetailScreen(
            serviceId = "1",
            onBackClick = {},
            onBookClick = {}
        )
    }
}
