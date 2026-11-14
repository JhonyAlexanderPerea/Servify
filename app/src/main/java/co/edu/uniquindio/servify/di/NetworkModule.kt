package co.edu.uniquindio.servify.di

import android.content.Context
import co.edu.uniquindio.servify.R
import co.edu.uniquindio.servify.data.remote.ai.OpenRouterApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Named
import javax.inject.Singleton

/** Clientes HTTP de la aplicación: OpenRouter y Mapbox. */
@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    @Named("openrouter")
    fun provideOpenRouterRetrofit(@ApplicationContext context: Context): Retrofit {
        val apiKey = context.getString(R.string.openrouter_api_key)
        val client = OkHttpClient.Builder()
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .addInterceptor { chain ->
                val request = chain.request()
                // La clave SOLO se adjunta a las peticiones a openrouter.ai, nunca a otros hosts.
                if (request.url.host == "openrouter.ai") {
                    chain.proceed(
                        request.newBuilder()
                            .header("Authorization", "Bearer $apiKey")
                            .build()
                    )
                } else {
                    chain.proceed(request)
                }
            }
            .build()
        return Retrofit.Builder()
            .baseUrl("https://openrouter.ai/api/v1/")
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideOpenRouterApi(@Named("openrouter") retrofit: Retrofit): OpenRouterApi =
        retrofit.create(OpenRouterApi::class.java)

    @Provides
    @Singleton
    @Named("mapbox")
    fun provideMapboxRetrofit(): Retrofit = Retrofit.Builder()
        .baseUrl("https://api.mapbox.com/")
        .client(
            OkHttpClient.Builder()
                .connectTimeout(15, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build()
        )
        .addConverterFactory(GsonConverterFactory.create())
        .build()
}
