package co.edu.uniquindio.servify.data.repository.memory

import co.edu.uniquindio.servify.domain.repository.ImageRepository

class InMemoryImageRepository : ImageRepository {

    /** Subidas simuladas: (uri local, ruta remota). */
    val uploads: MutableList<Pair<String, String>> = mutableListOf()

    override suspend fun uploadImage(localUri: String, remotePath: String): String {
        uploads += localUri to remotePath
        return "https://fake.servify.test/$remotePath"
    }
}
