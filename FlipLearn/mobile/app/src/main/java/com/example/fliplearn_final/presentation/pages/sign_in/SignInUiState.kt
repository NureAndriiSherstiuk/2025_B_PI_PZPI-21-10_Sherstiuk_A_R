package com.example.fliplearn_final.presentation.pages.sign_in

data class SignInUiState(
    val email: String = "",
    val password: String = "",
    val isPasswordVisible: Boolean = false,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val fieldErrors: Map<String, String?> = emptyMap()
)


