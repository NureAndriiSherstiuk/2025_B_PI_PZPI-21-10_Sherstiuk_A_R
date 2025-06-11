package com.example.fliplearn_final.presentation.pages.profile

sealed class ProfileEvent {
    data class UsernameChanged(val username: String) : ProfileEvent()
    data class EmailChanged(val email: String) : ProfileEvent()
    data class ThemeChanged(val theme: Theme) : ProfileEvent()
    object TogglePasswordVisibility : ProfileEvent()
    object SaveProfile : ProfileEvent()
}