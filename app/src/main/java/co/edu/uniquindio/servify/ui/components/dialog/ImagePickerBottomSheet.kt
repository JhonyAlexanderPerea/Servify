package co.edu.uniquindio.servify.ui.components.dialog

import android.Manifest
import android.content.res.Configuration
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.PhotoCamera
import androidx.compose.material.icons.rounded.PhotoLibrary
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import co.edu.uniquindio.servify.R
import co.edu.uniquindio.servify.ui.theme.ServifyTextStyle
import co.edu.uniquindio.servify.ui.theme.ServifyTheme
import co.edu.uniquindio.servify.ui.util.createTempImageUri

/**
 * Modal Bottom Sheet reutilizable para seleccionar imágenes desde la Galería (PickVisualMedia)
 * o capturarlas con la Cámara (TakePicture + FileProvider).
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ImagePickerBottomSheet(
    isVisible: Boolean,
    onDismiss: () -> Unit,
    onImageSelected: (Uri) -> Unit,
    onPermissionDenied: (() -> Unit)? = null
) {
    if (!isVisible) return

    val context = LocalContext.current
    var cameraTempUri by remember { mutableStateOf<Uri?>(null) }

    // Launcher de Galería (PickVisualMedia)
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            onImageSelected(uri)
            onDismiss()
        }
    }

    // Launcher de Cámara (TakePicture)
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success: Boolean ->
        if (success) {
            cameraTempUri?.let { uri ->
                onImageSelected(uri)
                onDismiss()
            }
        }
    }

    // Launcher de Permiso de Cámara (RequestPermission)
    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            val uri = context.createTempImageUri()
            cameraTempUri = uri
            cameraLauncher.launch(uri)
        } else {
            onPermissionDenied?.invoke()
        }
    }

    val openCamera = {
        val uri = context.createTempImageUri()
        cameraTempUri = uri
        cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
    }

    val openGallery = {
        galleryLauncher.launch(
            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
        )
    }

    ServifyBottomSheet(
        onDismissRequest = onDismiss,
        title = stringResource(R.string.image_picker_title)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        ) {
            ImagePickerOptionItem(
                icon = Icons.Rounded.PhotoCamera,
                title = stringResource(R.string.image_picker_camera),
                onClick = openCamera
            )

            Spacer(modifier = Modifier.height(8.dp))

            ImagePickerOptionItem(
                icon = Icons.Rounded.PhotoLibrary,
                title = stringResource(R.string.image_picker_gallery),
                onClick = openGallery
            )
        }
    }
}

@Composable
private fun ImagePickerOptionItem(
    icon: ImageVector,
    title: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surfaceContainerHigh,
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(24.dp)
            )

            Spacer(modifier = Modifier.width(16.dp))

            Text(
                text = title,
                style = ServifyTextStyle.SmallMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Preview(name = "Image Picker Sheet - Light", showBackground = true)
@Preview(name = "Image Picker Sheet - Dark", showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun ImagePickerBottomSheetPreview() {
    ServifyTheme {
        ImagePickerBottomSheet(
            isVisible = true,
            onDismiss = {},
            onImageSelected = {}
        )
    }
}
