package co.edu.uniquindio.servify.data.repository.remote

import android.content.Context
import co.edu.uniquindio.servify.R
import co.edu.uniquindio.servify.data.remote.ai.AiRequestLimiter
import co.edu.uniquindio.servify.data.remote.ai.AiResponseParser
import co.edu.uniquindio.servify.data.remote.ai.AiUnavailableException
import co.edu.uniquindio.servify.data.remote.ai.ChatCompletionRequestDto
import co.edu.uniquindio.servify.data.remote.ai.ChatMessageDto
import co.edu.uniquindio.servify.data.remote.ai.OpenRouterApi
import co.edu.uniquindio.servify.domain.model.AiClassification
import co.edu.uniquindio.servify.domain.model.CatalogCategory
import co.edu.uniquindio.servify.domain.repository.AiClassifierRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class OpenRouterClassifierRepository @Inject constructor(
    @param:ApplicationContext private val context: Context,
    private val api: OpenRouterApi,
    private val limiter: AiRequestLimiter
) : AiClassifierRepository {

    override suspend fun classify(
        title: String,
        description: String,
        catalog: List<CatalogCategory>
    ): AiClassification? {
        val model = context.getString(R.string.openrouter_model)
        val apiKey = context.getString(R.string.openrouter_api_key)
        if (model.isBlank() || apiKey.isBlank()) {
            throw AiUnavailableException("OpenRouter no está configurado")
        }
        if (!limiter.tryAcquire()) {
            throw AiUnavailableException("Límite de peticiones de IA alcanzado")
        }

        val system = buildString {
            appendLine("Eres un clasificador de servicios locales para una app de Colombia.")
            appendLine("Elige la categoría y la subcategoría que mejor describen el servicio, usando EXCLUSIVAMENTE este catálogo:")
            catalog.forEach { appendLine("- ${it.name}: ${it.subcategories.joinToString(", ")}") }
            appendLine("Responde SOLO con un objeto JSON, sin texto adicional ni markdown, con esta forma exacta:")
            appendLine("""{"category":"<nombre exacto>","subcategory":"<nombre exacto>","confidence":<entero de 0 a 100>}""")
            append("""Si ninguna categoría aplica responde {"category":"","subcategory":"","confidence":0}.""")
        }
        // Se limita el texto enviado para controlar el consumo.
        val user = "Título: ${title.take(100)}\nDescripción: ${description.take(500)}"

        val response = api.chatCompletion(
            ChatCompletionRequestDto(
                model = model,
                messages = listOf(
                    ChatMessageDto("system", system),
                    ChatMessageDto("user", user)
                )
            )
        )
        return AiResponseParser.parse(response.choices?.firstOrNull()?.message?.content, catalog)
    }
}
