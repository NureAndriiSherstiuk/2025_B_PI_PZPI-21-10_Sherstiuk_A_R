package com.example.fliplearn_final.domain.usecase.user

import com.example.fliplearn_final.data.local.datastore.UserPreferences
import com.example.fliplearn_final.domain.model.User
import com.example.fliplearn_final.domain.repository.user.UserRepository
import jakarta.inject.Inject

class SignUpUserUseCase @Inject constructor(
    private val userRepository: UserRepository,
    private val userPreferences: UserPreferences
) {
    suspend operator fun invoke(user: User): Result<Unit> {
        return try {
            userRepository.signUpUser(user)
            userPreferences.saveUserEmail(user.email)
            userPreferences.setLocallySignedIn(true)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

