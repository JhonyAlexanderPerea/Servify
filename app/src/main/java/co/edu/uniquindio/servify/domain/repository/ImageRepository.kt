package co.edu.uniquindio.servify.domain.repository

interface ImageRepository {
    /** Sube la imagen local ([localUri], en texto) y devuelve su URL pública (https). [remotePath] es la ruta lógica. */
    suspend fun uploadImage(localUri: String, remotePath: String): String
}
