package com.example.fliplearn_final.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.Instant

@Entity(tableName = "user")
data class UserEntity(
    @PrimaryKey(autoGenerate = true) val user_id: Int = 0,
    val email: String,
    val username: String,
    val password: String,
    @ColumnInfo(name = "created_at")
    val createdAt: Instant = Instant.now()
)