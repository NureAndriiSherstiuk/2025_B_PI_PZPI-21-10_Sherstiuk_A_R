package com.example.fliplearn_final.domain.usecase.user

import com.example.fliplearn_final.data.local.datastore.UserPreferences
import com.example.fliplearn_final.domain.model.User
import com.example.fliplearn_final.domain.repository.user.UserRepository
import com.example.fliplearn_final.util.PasswordHasher
import jakarta.inject.Inject

class SignInUserUseCase @Inject constructor(
    private val repository: UserRepository,
    private val userPreferences: UserPreferences
) {
    suspend operator fun invoke(email: String, password: String): Result<User> {
        val user = repository.getUserByEmail(email.trim())
            ?: return Result.failure(Exception("Користувача не знайдено"))

        val isValid = PasswordHasher.verifyPassword(password, user.password)
        return if (isValid) {
            userPreferences.saveUserEmail(user.email)
            userPreferences.setLocallySignedIn(true)
            Result.success(user)
        } else {
            Result.failure(Exception("Невірний пароль"))
        }
    }
}
