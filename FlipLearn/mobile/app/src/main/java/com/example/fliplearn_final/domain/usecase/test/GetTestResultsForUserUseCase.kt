package com.example.fliplearn_final.domain.usecase.test

import com.example.fliplearn_final.domain.model.TestResult
import com.example.fliplearn_final.domain.repository.test.TestResultRepository
import javax.inject.Inject

class GetTestResultsForUserUseCase @Inject constructor(
    private val repository: TestResultRepository
) {
    suspend operator fun invoke(userId: Int): List<TestResult> {
        return repository.getResultsForUser(userId)
    }
}