package co.edu.uniquindio.servify.features.service.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Image
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import co.edu.uniquindio.servify.R
import co.edu.uniquindio.servify.ui.components.ServifyAsyncImage

/**
 * Carrusel horizontal reutilizable para mostrar imágenes de una publicación.
 * Soporta 1 a 5 imágenes con indicadores de página y contador visual.
 */
@Composable
fun PublicationImageSlider(
    images: List<String>,
    modifier: Modifier = Modifier,
    aspectRatio: Float = 16f / 10f,
    showPageCounter: Boolean = true,
    onImageClick: ((Int) -> Unit)? = null
) {
    val pageCount = images.size

    val sliderModifier = modifier
        .aspectRatio(aspectRatio)
        .clip(RoundedCornerShape(16.dp))

    if (pageCount == 0) {
        Box(
            modifier = sliderModifier.background(MaterialTheme.colorScheme.surfaceVariant),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Image,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(48.dp)
            )
        }
        return
    }

    val pagerState = rememberPagerState(pageCount = { pageCount })

    Box(modifier = sliderModifier) {
        HorizontalPager(state = pagerState) { page ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clickable { onImageClick?.invoke(page) }
            ) {
                ServifyAsyncImage(
                    imageUrl = images[page],
                    contentDescription = stringResource(
                        R.string.publication_image_slide_cd,
                        page + 1,
                        pageCount
                    ),
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop,
                    fallbackIcon = Icons.Default.Image
                )
            }
        }

        if (showPageCounter && pageCount > 1) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(8.dp)
                    .background(
                        color = Color.Black.copy(alpha = 0.55f),
                        shape = RoundedCornerShape(4.dp)
                    )
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text(
                    text = stringResource(
                        R.string.publication_image_counter,
                        pagerState.currentPage + 1,
                        pageCount
                    ),
                    color = Color.White,
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)
                )
            }
        }

        if (pageCount > 1) {
            Row(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                repeat(pageCount) { index ->
                    val isCurrentPage = index == pagerState.currentPage
                    Box(
                        modifier = Modifier
                            .size(if (isCurrentPage) 8.dp else 6.dp)
                            .clip(RoundedCornerShape(50))
                            .background(
                                if (isCurrentPage) Color.White
                                else Color.White.copy(alpha = 0.5f)
                            )
                    )
                }
            }
        }
    }
}
