package com.example.fliplearn_final.presentation.pages.profile

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fliplearn_final.data.local.datastore.UserPreferences
import com.example.fliplearn_final.data.remote.auth.GoogleAuthClient
import com.example.fliplearn_final.domain.usecase.user.GetUserProfileInfoUseCase
import com.example.fliplearn_final.domain.usecase.user.SaveUserProfileChangesUseCase
import com.example.fliplearn_final.domain.usecase.user.SignOutUserUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val googleAuthClient: GoogleAuthClient,
    private val signOutUserUseCase: SignOutUserUseCase,
    private val getUserProfileInfoUseCase: GetUserProfileInfoUseCase,
    private val userPreferences: UserPreferences,
    private val saveUserProfileChangesUseCase: SaveUserProfileChangesUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    fun onEvent(event: ProfileEvent) {
        when (event) {
            is ProfileEvent.UsernameChanged -> updateUsername(event.username)
            is ProfileEvent.EmailChanged -> updateEmail(event.email)
            is ProfileEvent.ThemeChanged -> updateTheme(event.theme)
            is ProfileEvent.TogglePasswordVisibility -> togglePasswordVisibility()
            ProfileEvent.SaveProfile -> saveProfile()
        }
    }

    private fun updateUsername(username: String) {
        _uiState.update { it.copy(username = username) }
    }

    private fun updateEmail(email: String) {
        _uiState.update { it.copy(email = email) }
    }

    private fun updateTheme(theme: Theme) {
        _uiState.update { it.copy(selectedTheme = theme) }
        viewModelScope.launch {
            try {
                userPreferences.saveSelectedTheme(theme.name)
            } catch (e: Exception) {
                Log.e("ProfileViewModel", "Failed to save theme: ${e.message}")
            }
        }
    }

    private fun togglePasswordVisibility() {
        _uiState.update { it.copy(isPasswordVisible = !it.isPasswordVisible) }
    }


    fun saveProfile() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            val userId = _uiState.value.userId
            val email = _uiState.value.email
            val username = _uiState.value.username

            try {
                saveUserProfileChangesUseCase(userId, email, username)
                userPreferences.saveUserEmail(email)
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        showSuccess = true,
                        errorMessage = null
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = e.message ?: "Помилка при збереженні профілю"
                    )
                }
            }
        }
    }




    fun signOutAll(onSuccess: () -> Unit = {}, onError: (String) -> Unit = {}) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            try {
                val results = listOf(
                    async {
                        googleAuthClient.signOut()
                    },
                    async {
                        signOutUserUseCase()
                    }
                ).awaitAll()

                _uiState.update { it.copy(isLoading = false) }
                onSuccess()

            } catch (e: Exception) {
                e.printStackTrace()
                _uiState.update { it.copy(isLoading = false, errorMessage = e.message ?: "Помилка при виході") }
                onError(e.message ?: "Помилка при виході")
            }
        }
    }

    fun loadUserProfile() {
        viewModelScope.launch {
            val email = userPreferences.userEmail.firstOrNull()

            if (!email.isNullOrBlank()) {
                val user = getUserProfileInfoUseCase(email)

                if (user != null) {
                    _uiState.update {
                        it.copy(
                            userId = user.id,
                            username = user.username,
                            email = user.email,
                            errorMessage = null
                        )
                    }
                } else {
                    _uiState.update {
                        it.copy(errorMessage = "Користувача не знайдено")
                    }
                }
            } else {
                _uiState.update {
                    it.copy(errorMessage = "Email користувача не знайдено")
                }
            }
        }
    }



}