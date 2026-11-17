package co.edu.uniquindio.servify.navigation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.edu.uniquindio.servify.data.remote.connectivity.ConnectivityMonitor
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

/**
 * ViewModel a nivel de aplicación para la observabilidad global de conectividad de red y estado del sistema.
 *
 * **Comportamiento de Persistencia Offline en Firestore:**
 * Cloud Firestore en Android mantiene habilitada por defecto la persistencia en disco
 * (`isPersistenceEnabled = true`).
 * Por lo tanto:
 * - Las consultas en tiempo real (`addSnapshotListener`) continúan emitiendo datos cacheados localmente.
 * - Las operaciones de escritura (crear/editar publicación, solicitar servicio, etc.) se encolan
 *   localmente en el almacenamiento persistente de la app y se sincronizan automáticamente con
 *   el servidor cuando `ConnectivityMonitor` reporte el restablecimiento de la conexión.
 */
@HiltViewModel
class AppStateViewModel @Inject constructor(
    connectivityMonitor: ConnectivityMonitor
) : ViewModel() {

    val isOnline: StateFlow<Boolean> = connectivityMonitor.isConnected
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = true
        )
}
