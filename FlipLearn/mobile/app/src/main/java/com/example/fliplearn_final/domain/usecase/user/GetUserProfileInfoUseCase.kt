package com.example.fliplearn_final.domain.usecase.user

import com.example.fliplearn_final.domain.model.User
import com.example.fliplearn_final.domain.repository.user.UserRepository

class GetUserProfileInfoUseCase(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(email: String): User? {
        return userRepository.getUserByEmail(email)
    }
}