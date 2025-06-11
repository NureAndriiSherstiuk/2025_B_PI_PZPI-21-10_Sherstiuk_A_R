package com.example.fliplearn_final.domain.repository.test

import com.example.fliplearn_final.domain.model.TestResult

interface TestResultRepository {
    suspend fun saveResult(result: TestResult)
    suspend fun getResultsForUser(userId: Int): List<TestResult>
}