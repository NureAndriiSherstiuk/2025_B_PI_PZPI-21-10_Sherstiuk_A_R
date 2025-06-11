package com.example.fliplearn_final.presentation.pages.sign_up

data class SignUpUiState(
    val email: String = "",
    val userName: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val isPasswordVisible: Boolean = false,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val fieldErrors: Map<String, String?> = emptyMap()
)
