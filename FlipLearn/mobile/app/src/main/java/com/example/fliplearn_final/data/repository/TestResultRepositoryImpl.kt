package com.example.fliplearn_final.data.repository

import com.example.fliplearn_final.data.local.dao.TestResultDao
import com.example.fliplearn_final.data.local.entity.TestResultEntity
import com.example.fliplearn_final.domain.model.TestResult
import com.example.fliplearn_final.domain.repository.test.TestResultRepository
import javax.inject.Inject


class TestResultRepositoryImpl @Inject constructor(
    private val dao: TestResultDao
) : TestResultRepository {

    override suspend fun saveResult(result: TestResult) {
        val entity = TestResultEntity(
            resultId = result.id,
            userId = result.userId,
            dictionaryId = result.dictionaryId,
            correctAnswers = result.correctAnswers,
            totalQuestions = result.totalQuestions,
            percentScore = result.percentScore,
            completedAt = result.completedAt
        )
        dao.insertTestResult(entity)
    }

    override suspend fun getResultsForUser(userId: Int): List<TestResult> {
        val entities = dao.getResultsForUser(userId)
        return entities.map { entity ->
            TestResult(
                id = entity.resultId,
                userId = entity.userId,
                dictionaryId = entity.dictionaryId,
                correctAnswers = entity.correctAnswers,
                totalQuestions = entity.totalQuestions,
                percentScore = entity.percentScore,
                completedAt = entity.completedAt
            )
        }
    }
}