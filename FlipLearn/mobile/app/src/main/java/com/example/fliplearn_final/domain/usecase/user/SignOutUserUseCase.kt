package com.example.fliplearn_final.domain.usecase.user

import com.example.fliplearn_final.data.local.datastore.UserPreferences
import jakarta.inject.Inject

class SignOutUserUseCase @Inject constructor(
    private val userPreferences: UserPreferences
) {
    suspend operator fun invoke(): Result<Unit> {
        return try {
            userPreferences.clearUserData()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}