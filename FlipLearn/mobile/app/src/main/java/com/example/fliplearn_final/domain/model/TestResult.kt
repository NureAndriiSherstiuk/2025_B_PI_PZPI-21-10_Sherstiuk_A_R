package com.example.fliplearn_final.domain.model

import java.time.Instant

data class TestResult(
    val id: Int = 0,
    val userId: Int,
    val dictionaryId: Int,
    val correctAnswers: Int,
    val totalQuestions: Int,
    val percentScore: Float,
    val completedAt: Instant
)