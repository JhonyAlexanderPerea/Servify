package co.edu.uniquindio.servify.di

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Módulo de inyección de dependencias para Firebase (Firestore y Auth).
 *
 * **Documentación de Persistencia y Caché Offline de Cloud Firestore:**
 *
 * 1. **Persistencia Habilitada por Defecto:**
 *    En el SDK de Android para Cloud Firestore, la persistencia en disco está habilitada por defecto
 *    (`isPersistenceEnabled = true`). Las lecturas y escrituras se gestionan automáticamente a través de la caché local de SQLite.
 *
 * 2. **Lectura de Datos con Snapshot Listeners:**
 *    Los observadores en tiempo real (`addSnapshotListener`) en repositorios como `PublicationFirestoreRepositoryImpl`
 *    y `ServiceRequestFirestoreRepositoryImpl` continúan emitiendo snapshots de la caché local cuando la aplicación
 *    está sin conexión (`!isOnline`). La propiedad `snapshot.metadata.isFromCache` indica si los datos provienen de la caché local.
 *
 * 3. **Cola de Escrituras Offline:**
 *    Todas las operaciones de modificación (`set`, `update`, `delete` o `WriteBatch`) ejecutadas mientras el dispositivo
 *    no tiene conexión a internet se persisten inmediatamente en la cola local de escrituras de Firestore. Cuando
 *    `ConnectivityMonitor` detecta que se restableció la conexión a internet, el SDK de Firestore sincroniza
 *    automáticamente y en orden cronológico la cola de cambios pendientes con los servidores de Firebase.
 */
@Module
@InstallIn(SingletonComponent::class)
object FirebaseModule {

    @Provides
    @Singleton
    fun provideFirebaseFirestore(): FirebaseFirestore = FirebaseFirestore.getInstance()

    @Provides
    @Singleton
    fun provideFirebaseAuth(): FirebaseAuth = FirebaseAuth.getInstance()
}
