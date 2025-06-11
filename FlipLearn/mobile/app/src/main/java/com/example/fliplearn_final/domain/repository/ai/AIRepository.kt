package com.example.fliplearn_final.domain.repository.ai

interface AIRepository {
    suspend fun isTranslationCorrect(term: String, translation: String): Boolean
    suspend fun evaluateInputTranslation(term: String, userInput: String): Boolean
    suspend fun getMeaningForTerm(term: String): String
}
