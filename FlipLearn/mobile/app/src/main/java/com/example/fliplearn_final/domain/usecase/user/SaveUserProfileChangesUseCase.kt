package com.example.fliplearn_final.domain.usecase.user

import com.example.fliplearn_final.domain.repository.user.UserRepository
import jakarta.inject.Inject

class SaveUserProfileChangesUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(userId: Int, email: String, username: String) {
        userRepository.updateUserProfile(userId, email, username)
    }
}

