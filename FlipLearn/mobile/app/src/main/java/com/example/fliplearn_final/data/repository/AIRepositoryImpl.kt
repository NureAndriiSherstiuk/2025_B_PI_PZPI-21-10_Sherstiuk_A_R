package com.example.fliplearn_final.data.repository

import com.example.fliplearn_final.data.remote.retrofit.OpenRouterApi
import com.example.fliplearn_final.data.remote.retrofit.model.ChatRequest
import com.example.fliplearn_final.data.remote.retrofit.model.Message
import com.example.fliplearn_final.domain.repository.ai.AIRepository
import javax.inject.Inject


class AIRepositoryImpl @Inject constructor(
    private val api: OpenRouterApi
) : AIRepository {
    override suspend fun isTranslationCorrect(term: String, translation: String): Boolean {
        val request = ChatRequest(
            model = "meta-llama/llama-3.3-8b-instruct:free",
            messages = listOf(
                Message("user", "Is '$translation' the correct translation for the word '$term'? One word is in English and one in Ukrainian. Only respond with 'true' if they are accurate direct translations of each other. Respond only with 'true' or 'false'.")
            )
        )

        val response = api.getCompletion(request)
        val answer = response.choices.firstOrNull()?.message?.content?.trim()?.lowercase()

        return answer == "true"
    }


    override suspend fun evaluateInputTranslation(term: String, userInput: String): Boolean {
        val request = ChatRequest(
            model = "meta-llama/llama-3.3-8b-instruct:free",
            messages = listOf(
                Message(
                    "user",
                    """
                Determine whether '$term' and '$userInput' are correct translations of each other between English and Ukrainian. 
                Either word can be in English or Ukrainian.
                Only reply with 'true' if the two words mean the same thing in both languages.
                Respond with just 'true' or 'false'.
                """.trimIndent()
                )
            )
        )

        val response = api.getCompletion(request)
        val answer = response.choices.firstOrNull()?.message?.content?.trim()?.lowercase()

        return answer == "true"
    }




    override suspend fun getMeaningForTerm(term: String): String {
        val request = ChatRequest(
            model = "meta-llama/llama-3.3-8b-instruct:free",
            messages = listOf(
                Message(
                    "user",
                    "Provide only the single most frequent and commonly used meaning of the English word '$term'. Respond with a concise definition, without examples or alternate meanings."
                )
            )
        )
        val response = api.getCompletion(request)
        return response.choices.firstOrNull()?.message?.content?.trim().orEmpty()
    }

}
