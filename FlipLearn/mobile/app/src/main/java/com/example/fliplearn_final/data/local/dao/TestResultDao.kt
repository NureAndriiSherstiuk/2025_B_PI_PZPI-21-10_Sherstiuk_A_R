package com.example.fliplearn_final.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.fliplearn_final.data.local.entity.TestResultEntity

@Dao
interface TestResultDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTestResult(result: TestResultEntity)

    @Query("SELECT * FROM test_result WHERE user_id = :userId ORDER BY completed_at DESC")
    suspend fun getResultsForUser(userId: Int): List<TestResultEntity>
}
