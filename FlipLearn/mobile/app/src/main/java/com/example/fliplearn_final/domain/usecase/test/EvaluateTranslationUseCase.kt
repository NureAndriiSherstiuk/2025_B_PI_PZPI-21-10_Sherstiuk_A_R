package com.example.fliplearn_final.domain.usecase.test

import com.example.fliplearn_final.domain.repository.ai.AIRepository
import javax.inject.Inject

class EvaluateTranslationUseCase @Inject constructor(
    private val aiRepository: AIRepository
) {
    suspend operator fun invoke(term: String, translation: String): Boolean {
        return aiRepository.isTranslationCorrect(term, translation)
    }
}
