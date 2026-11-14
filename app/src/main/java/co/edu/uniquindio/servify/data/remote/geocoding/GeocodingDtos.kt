package co.edu.uniquindio.servify.data.remote.geocoding

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class GeocodingResponseDto(
    val features: List<GeocodingFeatureDto>? = null
)

@Keep
data class GeocodingFeatureDto(
    val properties: GeocodingPropertiesDto? = null
)

@Keep
data class GeocodingPropertiesDto(
    @SerializedName("full_address") val fullAddress: String? = null,
    @SerializedName("place_formatted") val placeFormatted: String? = null,
    val name: String? = null
)
