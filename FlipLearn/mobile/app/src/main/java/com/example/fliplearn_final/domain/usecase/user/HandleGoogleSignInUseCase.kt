package com.example.fliplearn_final.domain.usecase.user

import com.example.fliplearn_final.data.local.datastore.UserPreferences
import com.example.fliplearn_final.domain.model.User
import com.example.fliplearn_final.domain.repository.user.UserRepository
import javax.inject.Inject

class HandleGoogleSignInUseCase @Inject constructor(
    private val userRepository: UserRepository,
    private val signUpUserUseCase: SignUpUserUseCase,
    private val userPreferences: UserPreferences
) {
    suspend operator fun invoke(email: String, username: String): Result<Unit> {
        val existingUser = userRepository.getUserByEmail(email)

        return if (existingUser == null) {
            val newUser = User(
                id = 0,
                email = email,
                username = username,
                password = "google-auth",
            )
            signUpUserUseCase(newUser)
        } else {
            userPreferences.saveUserEmail(existingUser.email)
            userPreferences.setLocallySignedIn(true)
            Result.success(Unit)
        }
    }
}
