package com.example.fliplearn_final.data.remote.retrofit

import com.example.fliplearn_final.BuildConfig
import com.example.fliplearn_final.data.remote.retrofit.model.ChatRequest
import com.example.fliplearn_final.data.remote.retrofit.model.ChatResponse
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface OpenRouterApi {
    @Headers(
        "Authorization: Bearer ${BuildConfig.OPENROUTER_API_KEY}",
        "HTTP-Referer: https://fliplearn.com",
        "X-Title: FlipLearn",
        "Content-Type: application/json"
    )
    @POST("chat/completions")
    suspend fun getCompletion(@Body request: ChatRequest): ChatResponse
}
