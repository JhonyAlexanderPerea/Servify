package co.edu.uniquindio.servify.features.service.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.HomeRepairService
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import co.edu.uniquindio.servify.features.home.component.LevelBadge
import co.edu.uniquindio.servify.features.home.component.StarRatingRow
import co.edu.uniquindio.servify.features.service.model.CommentItemData
import co.edu.uniquindio.servify.features.service.model.ServiceDetailData
import co.edu.uniquindio.servify.features.service.model.TrustFactor
import co.edu.uniquindio.servify.ui.components.button.ServifyFilledButton
import co.edu.uniquindio.servify.ui.components.input.ServifyTextField
import co.edu.uniquindio.servify.ui.theme.ServifyExtra
import co.edu.uniquindio.servify.ui.theme.ServifyTextStyle
import co.edu.uniquindio.servify.ui.theme.contentColorOn

// -----------------------------------------------------------------------------
// TOP APP BAR DEL DETALLE DE SERVICIO
// -----------------------------------------------------------------------------

@Composable
fun ServiceDetailTopBar(
    onBackClick: () -> Unit,
    isFavorite: Boolean,
    onFavoriteToggle: () -> Unit,
    onShareClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surfaceVariant)
                .clickable(onClick = onBackClick),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "Volver",
                tint = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.size(22.dp)
            )
        }

        Text(
            text = "Detalle del Servicio",
            style = ServifyTextStyle.SmallMedium,
            color = MaterialTheme.colorScheme.onSurface
        )

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .clickable(onClick = onFavoriteToggle),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                    contentDescription = "Favorito",
                    tint = if (isFavorite) ServifyExtra.favorite else MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.size(22.dp)
                )
            }

            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .clickable(onClick = onShareClick),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Share,
                    contentDescription = "Compartir",
                    tint = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.size(22.dp)
                )
            }
        }
    }
}

// -----------------------------------------------------------------------------
// GALERÍA DE IMÁGENES / BANNER HERO
// -----------------------------------------------------------------------------

@Composable
fun ImageGalleryBanner(
    detail: ServiceDetailData,
    modifier: Modifier = Modifier
) {
    var activeImageIndex by remember { mutableStateOf(0) }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(220.dp)
            .clip(RoundedCornerShape(24.dp))
            .background(MaterialTheme.colorScheme.primaryContainer)
    ) {
        Box(
            modifier = Modifier
                .matchParentSize()
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(ServifyExtra.heroGradientStart, ServifyExtra.heroGradientEnd)
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    imageVector = Icons.Default.HomeRepairService,
                    contentDescription = null,
                    tint = ServifyExtra.onHero,
                    modifier = Modifier.size(64.dp)
                )
                Text(
                    text = detail.subcategory,
                    style = ServifyTextStyle.BodyMedium.copy(fontWeight = FontWeight.Bold),
                    color = ServifyExtra.onHero
                )
            }
        }

        Box(
            modifier = Modifier
                .padding(12.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surface)
                .padding(horizontal = 12.dp, vertical = 6.dp)
                .align(Alignment.TopStart)
        ) {
            Text(
                text = "${detail.category} • ${detail.subcategory}",
                style = ServifyTextStyle.Caption.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        Row(
            modifier = Modifier
                .padding(bottom = 12.dp)
                .align(Alignment.BottomCenter),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            repeat(detail.images.size) { index ->
                val isActive = index == activeImageIndex
                Box(
                    modifier = Modifier
                        .size(if (isActive) 18.dp else 8.dp, 8.dp)
                        .clip(CircleShape)
                        .background(
                            if (isActive) ServifyExtra.onHero else ServifyExtra.onHero.copy(alpha = 0.5f)
                        )
                        .clickable { activeImageIndex = index }
                )
            }
        }
    }
}

// -----------------------------------------------------------------------------
// TARJETA DEL PROVEEDOR (PROVIDER CARD)
// -----------------------------------------------------------------------------

@Composable
fun ProviderCard(
    detail: ServiceDetailData,
    onContactProviderClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(52.dp)
                            .clip(CircleShape)
                            .background(detail.providerColor),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = detail.providerInitials,
                            style = ServifyTextStyle.TitleTight.copy(
                                color = contentColorOn(detail.providerColor),
                                fontSize = 20.sp
                            )
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = detail.providerName,
                                style = ServifyTextStyle.SmallMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp
                                ),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            if (detail.isVerified) {
                                Spacer(modifier = Modifier.width(4.dp))
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = "Verificado",
                                    tint = MaterialTheme.colorScheme.secondary,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(2.dp))

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            LevelBadge(level = detail.providerLevel)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Desde ${detail.providerMemberSince}",
                                style = ServifyTextStyle.Caption,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .padding(12.dp),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "${detail.completedJobs}",
                        style = ServifyTextStyle.SmallMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Trabajos",
                        style = ServifyTextStyle.Caption,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Box(
                    modifier = Modifier
                        .width(1.dp)
                        .height(28.dp)
                        .background(MaterialTheme.colorScheme.outlineVariant)
                )

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    StarRatingRow(rating = detail.rating, reviewCount = detail.reviewCount)
                    Text(
                        text = "Calificación",
                        style = ServifyTextStyle.Caption,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Box(
                    modifier = Modifier
                        .width(1.dp)
                        .height(28.dp)
                        .background(MaterialTheme.colorScheme.outlineVariant)
                )

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "${detail.trustIndex}%",
                        style = ServifyTextStyle.SmallMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.secondary
                        )
                    )
                    Text(
                        text = "Confianza",
                        style = ServifyTextStyle.Caption,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

// -----------------------------------------------------------------------------
// TRUST SYSTEM CARD (SISTEMA DE CONFIANZA Y FACTORES VERIFICADOS)
// -----------------------------------------------------------------------------

@Composable
fun TrustSystemCard(
    trustIndex: Int,
    trustFactors: List<TrustFactor>,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.secondary.copy(alpha = 0.3f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.secondary),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Shield,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSecondary,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Column {
                        Text(
                            text = "Índice de Confianza Servify",
                            style = ServifyTextStyle.SmallMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.secondary
                        )
                        Text(
                            text = "Garantía y perfil verificado por el sistema",
                            style = ServifyTextStyle.Caption,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.secondary)
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = "$trustIndex%",
                        style = ServifyTextStyle.SmallMedium.copy(
                            color = MaterialTheme.colorScheme.onSecondary,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                trustFactors.forEach { factor ->
                    Row(
                        verticalAlignment = Alignment.Top,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(
                            imageVector = Icons.Default.VerifiedUser,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.secondary,
                            modifier = Modifier
                                .padding(top = 2.dp)
                                .size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = factor.title,
                                style = ServifyTextStyle.Caption.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            )
                            Text(
                                text = factor.description,
                                style = ServifyTextStyle.Caption,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
    }
}

// -----------------------------------------------------------------------------
// TARJETA DE DESCRIPCIÓN DESPLEGABLE
// -----------------------------------------------------------------------------

@Composable
fun ExpandableDescriptionCard(
    description: String,
    zone: String,
    city: String,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Descripción del servicio",
                style = ServifyTextStyle.SmallMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = description,
                style = ServifyTextStyle.SmallRelaxed,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = if (expanded) Int.MAX_VALUE else 4,
                overflow = TextOverflow.Ellipsis
            )

            Row(
                modifier = Modifier
                    .clip(CircleShape)
                    .clickable { expanded = !expanded }
                    .padding(vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (expanded) "Mostrar menos" else "Leer descripción completa",
                    style = ServifyTextStyle.Caption.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(4.dp))
                Icon(
                    imageVector = if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(16.dp)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Place,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "Zona de cobertura: $zone ($city)",
                    style = ServifyTextStyle.Caption.copy(fontWeight = FontWeight.Medium),
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}

// -----------------------------------------------------------------------------
// SECCIÓN DE COMENTARIOS / RESEÑAS
// -----------------------------------------------------------------------------

@Composable
fun CommentItem(
    comment: CommentItemData,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(comment.authorAvatarColor),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = comment.authorInitials,
                            style = ServifyTextStyle.Caption.copy(
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Column {
                        Text(
                            text = comment.authorName,
                            style = ServifyTextStyle.SmallMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = comment.date,
                            style = ServifyTextStyle.Caption,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = null,
                        tint = ServifyExtra.gold,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(2.dp))
                    Text(
                        text = "${comment.rating}",
                        style = ServifyTextStyle.Caption.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = comment.commentText,
                style = ServifyTextStyle.Small,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
fun CommentInput(
    onSendComment: (String, Int) -> Unit,
    modifier: Modifier = Modifier
) {
    var text by remember { mutableStateOf("") }
    var selectedRating by remember { mutableStateOf(5) }

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text(
                text = "Deja tu opinión o consulta",
                style = ServifyTextStyle.SmallMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(6.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "Calificación: ",
                    style = ServifyTextStyle.Caption,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                repeat(5) { index ->
                    val starNumber = index + 1
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = "$starNumber estrellas",
                        tint = if (starNumber <= selectedRating) ServifyExtra.gold else MaterialTheme.colorScheme.outlineVariant,
                        modifier = Modifier
                            .size(20.dp)
                            .clickable { selectedRating = starNumber }
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                ServifyTextField(
                    value = text,
                    onValueChange = { text = it },
                    label = "Escribe un comentario...",
                    singleLine = false,
                    modifier = Modifier.weight(1f)
                )

                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(if (text.isNotBlank()) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant)
                        .clickable(enabled = text.isNotBlank()) {
                            onSendComment(text, selectedRating)
                            text = ""
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Send,
                        contentDescription = "Enviar",
                        tint = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

// -----------------------------------------------------------------------------
// BOTTOM APP BAR FIJA DEL DETALLE
// -----------------------------------------------------------------------------

@Composable
fun ServiceDetailBottomBar(
    priceRange: String,
    hourlyPrice: String,
    onBookClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 8.dp,
        shadowElevation = 8.dp
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 12.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Tarifa estimada",
                    style = ServifyTextStyle.Caption,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = hourlyPrice,
                    style = ServifyTextStyle.TitleTight.copy(
                        fontSize = 18.sp,
                        color = MaterialTheme.colorScheme.primary
                    )
                )
            }

            ServifyFilledButton(
                text = "Agendar Cita",
                icon = Icons.Default.CalendarMonth,
                onClick = onBookClick,
                modifier = Modifier.width(180.dp)
            )
        }
    }
}
