package com.example.fliplearn_final.domain.usecase.test

import com.example.fliplearn_final.domain.repository.ai.AIRepository
import javax.inject.Inject

class GetMeaningForTermUseCase @Inject constructor(
    private val aiRepository: AIRepository
) {
    suspend operator fun invoke(term: String): String {
        return try {
            aiRepository.getMeaningForTerm(term)
        } catch (e: Exception) {

            "Error: ${e.message}"
        }
    }

}



