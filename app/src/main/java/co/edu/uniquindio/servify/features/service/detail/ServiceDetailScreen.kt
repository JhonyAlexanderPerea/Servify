package co.edu.uniquindio.servify.features.service.detail

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import co.edu.uniquindio.servify.features.home.component.StarRatingRow
import co.edu.uniquindio.servify.features.service.component.CommentInput
import co.edu.uniquindio.servify.features.service.component.CommentItem
import co.edu.uniquindio.servify.features.service.component.ExpandableDescriptionCard
import co.edu.uniquindio.servify.features.service.component.ImageGalleryBanner
import co.edu.uniquindio.servify.features.service.component.ProviderCard
import co.edu.uniquindio.servify.features.service.component.ServiceDetailBottomBar
import co.edu.uniquindio.servify.features.service.component.ServiceDetailTopBar
import co.edu.uniquindio.servify.features.service.component.TrustSystemCard
import co.edu.uniquindio.servify.features.service.model.CommentItemData
import co.edu.uniquindio.servify.features.service.model.ServiceDetailMockData
import co.edu.uniquindio.servify.ui.theme.ServifyOnSurface
import co.edu.uniquindio.servify.ui.theme.ServifyOnSurfaceVariant
import co.edu.uniquindio.servify.ui.theme.ServifyPrimary
import co.edu.uniquindio.servify.ui.theme.ServifySurface
import co.edu.uniquindio.servify.ui.theme.ServifyTheme
import co.edu.uniquindio.servify.ui.theme.ServifyTextStyle

@Composable
fun ServiceDetailScreen(
    serviceId: String,
    onBackClick: () -> Unit,
    onBookClick: () -> Unit,
    modifier: Modifier = Modifier,
    onShareClick: () -> Unit = {}
) {
    var detailData by remember { mutableStateOf(ServiceDetailMockData.sampleDetail) }

    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .background(ServifySurface)
            .statusBarsPadding()
            .navigationBarsPadding(),
        topBar = {
            ServiceDetailTopBar(
                onBackClick = onBackClick,
                isFavorite = detailData.isFavorite,
                onFavoriteToggle = {
                    detailData = detailData.copy(isFavorite = !detailData.isFavorite)
                },
                onShareClick = onShareClick
            )
        },
        bottomBar = {
            ServiceDetailBottomBar(
                priceRange = detailData.priceRange,
                hourlyPrice = detailData.hourlyPrice,
                onBookClick = onBookClick
            )
        }
    ) { paddingValues ->

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
                        text = "Reseñas y opiniones (${detailData.comments.size})",
                        style = ServifyTextStyle.TitleTight.copy(fontSize = 18.sp),
                        color = ServifyOnSurface
                    )
                }
            }

            // 7. LISTA DE COMENTARIOS
            items(detailData.comments, key = { it.id }) { comment ->
                CommentItem(comment = comment)
            }

            // 8. ENTRADA PARA DEJAR COMENTARIOS
            item {
                CommentInput(
                    onSendComment = { commentText, rating ->
                        val newComment = CommentItemData(
                            id = "c_${System.currentTimeMillis()}",
                            authorName = "Tú (Jhony Ramírez)",
                            authorInitials = "JR",
                            authorAvatarColor = ServifyPrimary,
                            rating = rating.toDouble(),
                            date = "Ahora mismo",
                            commentText = commentText
                        )
                        detailData = detailData.copy(
                            comments = listOf(newComment) + detailData.comments,
                            reviewCount = detailData.reviewCount + 1
                        )
                    }
                )
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
