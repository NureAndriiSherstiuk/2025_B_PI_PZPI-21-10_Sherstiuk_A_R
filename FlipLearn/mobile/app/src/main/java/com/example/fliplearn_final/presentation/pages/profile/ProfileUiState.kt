package com.example.fliplearn_final.presentation.pages.profile

data class ProfileUiState(
    val userId: Int = 0,
    val username: String = "",
    val email: String = "",
    val themes: List<Theme> = listOf(Theme.Light, Theme.Dark),
    val selectedTheme: Theme = Theme.Light,
    val isPasswordVisible: Boolean = false,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val showSuccess: Boolean = false
)
