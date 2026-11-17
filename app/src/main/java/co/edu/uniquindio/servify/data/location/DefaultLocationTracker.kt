package co.edu.uniquindio.servify.data.location

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import androidx.core.content.ContextCompat
import co.edu.uniquindio.servify.domain.location.LocationTracker
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DefaultLocationTracker @Inject constructor(
    @param:ApplicationContext private val context: Context
) : LocationTracker {

    private val fusedLocationClient by lazy {
        LocationServices.getFusedLocationProviderClient(context)
    }

    private val userLocationFlow = MutableStateFlow<Pair<Double, Double>?>(null)

    private fun hasLocationPermission(): Boolean {
        val fineLocation = ContextCompat.checkSelfPermission(
            context, Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        val coarseLocation = ContextCompat.checkSelfPermission(
            context, Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        return fineLocation || coarseLocation
    }

    override suspend fun getCurrentLocation(): Pair<Double, Double>? {
        if (!hasLocationPermission()) {
            return null
        }

        return runCatching {
            val currentLocation: Location? = try {
                fusedLocationClient.getCurrentLocation(
                    Priority.PRIORITY_BALANCED_POWER_ACCURACY,
                    null
                ).await()
            } catch (_: Exception) {
                null
            }

            val loc = currentLocation ?: fusedLocationClient.lastLocation.await()
            if (loc != null) {
                val pair = Pair(loc.latitude, loc.longitude)
                userLocationFlow.value = pair
                pair
            } else {
                null
            }
        }.getOrNull()
    }

    override fun observeUserLocation(): Flow<Pair<Double, Double>?> = userLocationFlow.asStateFlow()
}
