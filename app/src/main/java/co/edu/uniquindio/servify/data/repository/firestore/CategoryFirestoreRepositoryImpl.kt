package co.edu.uniquindio.servify.data.repository.firestore

import co.edu.uniquindio.servify.data.model.dto.CategoryDto
import co.edu.uniquindio.servify.data.seed.SeedCategories
import co.edu.uniquindio.servify.domain.model.Category
import co.edu.uniquindio.servify.domain.repository.CategoryRepository
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementación de la capa de datos para categorías en Firebase Firestore.
 * Observa la colección "categories" y utiliza las categorías por defecto si está vacía.
 */
@Singleton
class CategoryFirestoreRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : CategoryRepository {

    private val collection = firestore.collection("categories")

    override fun observeCategories(): Flow<List<Category>> = callbackFlow {
        val subscription = collection.addSnapshotListener { snapshot, error ->
            if (error != null || snapshot == null || snapshot.isEmpty) {
                trySend(SeedCategories.categories)
                return@addSnapshotListener
            }
            val list = snapshot.documents.mapNotNull { doc ->
                try {
                    doc.toObject(CategoryDto::class.java)?.copy(id = doc.id)?.toDomain()
                } catch (_: Exception) {
                    null
                }
            }
            if (list.isEmpty()) {
                trySend(SeedCategories.categories)
            } else {
                trySend(list)
            }
        }
        awaitClose { subscription.remove() }
    }
}
