# Persistencia Offline y Observabilidad de Red en Servify Marketplace

## 1. Monitor de Conectividad (`ConnectivityMonitor`)
La aplicación cuenta con un monitor de red singleton (`ConnectivityMonitor`) registrado en `NetworkModule`. 
Utiliza el `ConnectivityManager.NetworkCallback` nativo de Android para emitir de forma reactiva el estado de internet (`isConnected: StateFlow<Boolean>`).

## 2. ViewModel y Banner Global (`AppStateViewModel` & `OfflineBanner`)
- **`AppStateViewModel`**: Inyecta `ConnectivityMonitor` y expone `isOnline` con `SharingStarted.WhileSubscribed(5000)` e `initialValue = true` para evitar destellos falsos de desconexión al iniciar.
- **`OfflineBanner`**: Componente visual en Jetpack Compose (`ui/components/feedback/OfflineBanner.kt`) animado con `AnimatedVisibility` (`expandVertically() + fadeIn()` y `shrinkVertically() + fadeOut()`). Renderiza un banner con estilo Material 3 (`errorContainer`) arriba del contenido de navegación en `AppNavigation.kt`.

## 3. Caché y Cola de Escrituras de Cloud Firestore
Cloud Firestore en Android opera con **persistencia offline en disco habilitada por defecto** (`isPersistenceEnabled = true`):

1. **Lectura con Snapshot Listeners**:
   Los repositorios `PublicationFirestoreRepositoryImpl` y `ServiceRequestFirestoreRepositoryImpl` utilizan `addSnapshotListener`. Cuando el dispositivo está sin conexión (`!isOnline`), los observadores continúan emitiendo los datos almacenados localmente en la caché SQLite de Firestore (`snapshot.metadata.isFromCache == true`).

2. **Cola de Escrituras Locales**:
   Cualquier modificación ejecutada sin conexión (`set`, `update`, `delete`, `WriteBatch`) se guarda en la cola local de escrituras de Firestore.

3. **Sincronización Automática al Reconectar**:
   Una vez que `ConnectivityMonitor` detecta el restablecimiento del acceso a internet, el SDK de Firestore envía y sincroniza automáticamente las operaciones encoladas con los servidores remotos de Firebase en orden estricto.
