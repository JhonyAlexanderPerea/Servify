package co.edu.uniquindio.servify.data.remote.geocoding

import retrofit2.http.GET
import retrofit2.http.Query

interface MapboxGeocodingApi {
    @GET("search/geocode/v6/reverse")
    suspend fun reverse(
        @Query("longitude") longitude: Double,
        @Query("latitude") latitude: Double,
        @Query("access_token") accessToken: String,
        @Query("language") language: String = "es",
        @Query("limit") limit: Int = 1
    ): GeocodingResponseDto
}
