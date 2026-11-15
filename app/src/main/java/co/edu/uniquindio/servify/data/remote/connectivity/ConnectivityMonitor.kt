package co.edu.uniquindio.servify.data.remote.connectivity

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Monitor de conectividad de red usando ConnectivityManager.NetworkCallback.
 * Expone un StateFlow con el estado actual de conectividad.
 *
 * Para tests, se puede pasar `context = null` y se comportará como si siempre hubiera conexión.
 */
@Singleton
open class ConnectivityMonitor @Inject constructor(
    @ApplicationContext private val context: Context?,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) {

    private val connectivityManager = context?.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager
    private val _isConnected = MutableStateFlow(true)
    open val isConnected = _isConnected.asStateFlow()

    // Scope para lanzar corrutinas desde callbacks que no son suspend
    private val scope = CoroutineScope(SupervisorJob() + dispatcher)

    private val networkCallback = object : ConnectivityManager.NetworkCallback() {
        override fun onCapabilitiesChanged(network: Network, networkCapabilities: NetworkCapabilities) {
            val hasInternet = networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) &&
                networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
            scope.launch { _isConnected.value = hasInternet }
        }

        override fun onLost(network: Network) {
            scope.launch { _isConnected.value = false }
        }
    }

    init {
        if (connectivityManager != null) {
            val networkRequest = NetworkRequest.Builder()
                .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                .build()
            connectivityManager.registerNetworkCallback(networkRequest, networkCallback)
            // Estado inicial
            val activeNetwork = connectivityManager.activeNetwork
            if (activeNetwork != null) {
                val capabilities = connectivityManager.getNetworkCapabilities(activeNetwork)
                _isConnected.value = capabilities?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) == true &&
                    capabilities?.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED) == true
            } else {
                _isConnected.value = false
            }
        } else {
            // Sin contexto (tests): asumimos que hay conexión
            _isConnected.value = true
        }
    }

    /**
     * Registra el callback en el ciclo de vida de un componente (Activity/Fragment/ViewModel)
     * para limpieza automática.
     */
    open fun bindToLifecycle(owner: LifecycleOwner) {
        connectivityManager?.let { cm ->
            owner.lifecycle.addObserver(object : LifecycleEventObserver {
                override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
                    if (event == Lifecycle.Event.ON_DESTROY) {
                        cm.unregisterNetworkCallback(networkCallback)
                    }
                }
            })
        }
    }

    /** Verificación puntual síncrona (para validaciones rápidas). */
    open suspend fun checkConnectivity(): Boolean = withContext(dispatcher) {
        connectivityManager?.let { cm ->
            val activeNetwork = cm.activeNetwork
            val capabilities = activeNetwork?.let { cm.getNetworkCapabilities(it) }
            capabilities?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) == true &&
                capabilities?.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED) == true
        } ?: true // Sin contexto (tests): asumimos conexión
    }
}