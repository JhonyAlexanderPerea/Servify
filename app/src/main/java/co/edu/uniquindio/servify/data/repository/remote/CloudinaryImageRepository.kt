package co.edu.uniquindio.servify.data.repository.remote

import android.content.Context
import android.net.Uri
import co.edu.uniquindio.servify.R
import co.edu.uniquindio.servify.domain.repository.ImageRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.DataOutputStream
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CloudinaryImageRepository @Inject constructor(
    @param:ApplicationContext private val context: Context
) : ImageRepository {

    override suspend fun uploadImage(localUri: String, remotePath: String): String =
        withContext(Dispatchers.IO) {
            val cloudName = context.getString(R.string.cloudinary_cloud_name)
            val uploadPreset = context.getString(R.string.cloudinary_upload_preset)
            require(cloudName.isNotBlank() && uploadPreset.isNotBlank()) {
                "Cloudinary no está configurado en local.properties"
            }

            val boundary = "ServifyBoundary${System.currentTimeMillis()}"
            val connection = (URL("https://api.cloudinary.com/v1_1/$cloudName/image/upload")
                .openConnection() as HttpURLConnection).apply {
                requestMethod = "POST"
                doOutput = true
                connectTimeout = 15_000
                readTimeout = 60_000
                setChunkedStreamingMode(0)
                setRequestProperty("Content-Type", "multipart/form-data; boundary=$boundary")
            }
            try {
                DataOutputStream(connection.outputStream).use { out ->
                    fun field(name: String, value: String) {
                        out.writeBytes("--$boundary\r\nContent-Disposition: form-data; name=\"$name\"\r\n\r\n$value\r\n")
                    }
                    field("upload_preset", uploadPreset)
                    field("public_id", remotePath)
                    out.writeBytes(
                        "--$boundary\r\nContent-Disposition: form-data; name=\"file\"; filename=\"image\"\r\n" +
                            "Content-Type: application/octet-stream\r\n\r\n"
                    )
                    val input = context.contentResolver.openInputStream(Uri.parse(localUri))
                        ?: throw IOException("No se pudo leer la imagen seleccionada")
                    input.use { it.copyTo(out) }
                    out.writeBytes("\r\n--$boundary--\r\n")
                }

                val code = connection.responseCode
                val body = (if (code in 200..299) connection.inputStream else connection.errorStream)
                    ?.bufferedReader()?.use { it.readText() }.orEmpty()
                if (code !in 200..299) throw IOException("Cloudinary respondió $code: $body")

                // La respuesta trae la URL pública de la imagen en secure_url
                JSONObject(body).getString("secure_url")
            } finally {
                connection.disconnect()
            }
        }
}
