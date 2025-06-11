package com.example.fliplearn_final.domain.usecase.test

import com.example.fliplearn_final.domain.repository.ai.AIRepository
import javax.inject.Inject

class EvaluateInputTranslationUseCase @Inject constructor(
    private val aiRepository: AIRepository
) {
    suspend operator fun invoke(term: String, userInput: String): Boolean {
        return aiRepository.evaluateInputTranslation(term, userInput)
    }
}