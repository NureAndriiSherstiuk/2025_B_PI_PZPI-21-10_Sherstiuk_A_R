package com.example.fliplearn_final.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.fliplearn_final.data.local.entity.UserEntity

@Dao
interface UserDao {
    @Insert
    suspend fun insertUser(user: UserEntity): Long
    @Query("SELECT * FROM user WHERE email = :email LIMIT 1")
    suspend fun getUserByEmail(email: String): UserEntity?
    @Query("UPDATE user SET email = :email, username = :username WHERE user_id = :userId")
    suspend fun updateUserProfile(userId: Int, email: String, username: String)
}