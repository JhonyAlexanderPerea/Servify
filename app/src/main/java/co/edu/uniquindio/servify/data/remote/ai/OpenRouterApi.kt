package co.edu.uniquindio.servify.data.remote.ai

import retrofit2.http.Body
import retrofit2.http.POST

interface OpenRouterApi {
    @POST("chat/completions")
    suspend fun chatCompletion(@Body request: ChatCompletionRequestDto): ChatCompletionResponseDto
}
