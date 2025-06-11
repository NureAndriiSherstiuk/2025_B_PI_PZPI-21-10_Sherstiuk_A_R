package com.example.fliplearn_final.domain.repository.user

import com.example.fliplearn_final.domain.model.User

interface UserRepository {
    suspend fun signUpUser(user: User)
    suspend fun getUserByEmail(email: String): User?
    suspend fun updateUserProfile(userId: Int, email: String, username: String)
}