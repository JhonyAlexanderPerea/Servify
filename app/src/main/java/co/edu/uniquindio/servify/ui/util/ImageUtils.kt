package co.edu.uniquindio.servify.ui.util

import android.content.Context
import android.net.Uri
import androidx.core.content.FileProvider
import java.io.File

/**
 * Crea un [Uri] seguro utilizando [FileProvider] para guardar imágenes temporales tomadas por la cámara.
 */
fun Context.createTempImageUri(): Uri {
    val directory = File(cacheDir, "images").apply {
        if (!exists()) {
            mkdirs()
        }
    }
    val tempFile = File.createTempFile("servify_${System.currentTimeMillis()}_", ".jpg", directory)
    return FileProvider.getUriForFile(
        this,
        "${packageName}.fileprovider",
        tempFile
    )
}
