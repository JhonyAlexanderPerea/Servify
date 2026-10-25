package co.edu.uniquindio.servify.domain.repository

import co.edu.uniquindio.servify.domain.model.Publication
import kotlinx.coroutines.flow.Flow

/**
 * Fuente única de verdad de las publicaciones. En la fase 2 vive en memoria; en la fase 3 se
 * reemplaza por Room/Firestore sin tocar casos de uso ni ViewModels.
 */
interface PublicationRepository {
    val publications: Flow<List<Publication>>

    fun observeByAuthor(authorId: String): Flow<List<Publication>>

    fun getById(id: String): Publication?

    /** Lectura síncrona (la usa GamificationService para evaluar insignias). */
    fun snapshot(): List<Publication>

    fun add(publication: Publication)

    /** Aplica [transform] de forma atómica. Devuelve la publicación actualizada o null si no existe. */
    fun update(id: String, transform: (Publication) -> Publication): Publication?

    /** Elimina la publicación. Devuelve false si no existía. */
    fun delete(id: String): Boolean
}
