package co.edu.uniquindio.servify.data.remote.ai

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class ChatMessageDto(
    val role: String? = null,
    val content: String? = null
)

@Keep
data class ResponseFormatDto(
    val type: String = "json_object"
)

@Keep
data class ChatCompletionRequestDto(
    val model: String,
    val messages: List<ChatMessageDto>,
    val temperature: Double = 0.0,
    @SerializedName("max_tokens") val maxTokens: Int = 120,
    @SerializedName("response_format") val responseFormat: ResponseFormatDto = ResponseFormatDto()
)

@Keep
data class ChoiceDto(
    val message: ChatMessageDto? = null
)

@Keep
data class ChatCompletionResponseDto(
    val choices: List<ChoiceDto>? = null
)

@Keep
data class AiClassificationDto(
    val category: String? = null,
    val subcategory: String? = null,
    val confidence: Int? = null
)
