package co.edu.uniquindio.servify.domain.usecase.location

import co.edu.uniquindio.servify.domain.model.Location
import co.edu.uniquindio.servify.domain.repository.GeocodingRepository
import javax.inject.Inject

/**
 * Caso de uso para obtener la dirección legible a partir de coordenadas geográficas (geocodificación inversa).
 */
class GetAddressFromLocationUseCase @Inject constructor(
    private val geocodingRepository: GeocodingRepository
) {
    suspend operator fun invoke(location: Location): String? = runCatching {
        geocodingRepository.reverse(location)
    }.getOrNull()
}
