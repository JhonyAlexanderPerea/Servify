package co.edu.uniquindio.servify.domain.location

import kotlinx.coroutines.flow.Flow

/**
 * Contrato de la capa de dominio para obtener y observar la ubicación geográfica del usuario.
 */
interface LocationTracker {
    /**
     * Obtiene la ubicación actual del usuario como (latitud, longitud).
     * Devuelve null si no hay permisos concedidos, GPS desactivado o si falla la lectura.
     */
    suspend fun getCurrentLocation(): Pair<Double, Double>?

    /**
     * Observa las actualizaciones de la ubicación del usuario de forma reactiva.
     */
    fun observeUserLocation(): Flow<Pair<Double, Double>?>
}
