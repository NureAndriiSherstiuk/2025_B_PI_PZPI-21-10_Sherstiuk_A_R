package com.example.fliplearn_final.data.local.entity

import androidx.room.*
import java.time.Instant

@Entity(
    tableName = "test_result",
    foreignKeys = [
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["user_id"],
            childColumns = ["user_id"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = DictionaryEntity::class,
            parentColumns = ["dictionary_id"],
            childColumns = ["dictionary_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["user_id"]),
        Index(value = ["dictionary_id"])
    ]
)
data class TestResultEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "result_id")
    val resultId: Int = 0,

    @ColumnInfo(name = "user_id")
    val userId: Int,

    @ColumnInfo(name = "dictionary_id")
    val dictionaryId: Int,

    @ColumnInfo(name = "correct_answers")
    val correctAnswers: Int,

    @ColumnInfo(name = "total_questions")
    val totalQuestions: Int,

    @ColumnInfo(name = "percent_score")
    val percentScore: Float,

    @ColumnInfo(name = "completed_at")
    val completedAt: Instant = Instant.now()
)
