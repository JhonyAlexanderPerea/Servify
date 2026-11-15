package co.edu.uniquindio.servify.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Image
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade

/**
 * Componente reutilizable para la carga asíncrona y eficiente de imágenes remotas usando Coil 3.
 * Maneja automáticamente estados de carga, error, placeholders y compatibilidad con @Preview.
 */
@Composable
fun ServifyAsyncImage(
    imageUrl: String?,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Crop,
    placeholderRes: Int? = null,
    errorRes: Int? = null,
    fallbackIcon: ImageVector = Icons.Default.Image
) {
    val isPreview = LocalInspectionMode.current
    val context = LocalContext.current

    if (isPreview || imageUrl.isNullOrBlank()) {
        Box(
            modifier = modifier.background(MaterialTheme.colorScheme.surfaceContainerHigh),
            contentAlignment = Alignment.Center
        ) {
            if (placeholderRes != null) {
                AsyncImage(
                    model = placeholderRes,
                    contentDescription = contentDescription,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = contentScale
                )
            } else {
                Icon(
                    imageVector = fallbackIcon,
                    contentDescription = contentDescription,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                    modifier = Modifier.size(32.dp)
                )
            }
        }
    } else {
        val imageRequest = ImageRequest.Builder(context)
            .data(imageUrl)
            .crossfade(true)
            .build()

        AsyncImage(
            model = imageRequest,
            contentDescription = contentDescription,
            modifier = modifier,
            contentScale = contentScale,
            error = errorRes?.let { painterResource(id = it) },
            placeholder = placeholderRes?.let { painterResource(id = it) }
        )
    }
}
