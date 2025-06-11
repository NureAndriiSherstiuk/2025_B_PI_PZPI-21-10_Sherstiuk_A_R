package com.example.fliplearn_final.presentation.pages.sign_in

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fliplearn_final.data.remote.auth.GoogleAuthClient
import com.example.fliplearn_final.domain.usecase.user.HandleGoogleSignInUseCase
import com.example.fliplearn_final.domain.usecase.user.SignInUserUseCase
import com.example.fliplearn_final.util.Validator
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SignInViewModel @Inject constructor(
    private val signInUserUseCase: SignInUserUseCase,
    private val googleAuthClient: GoogleAuthClient,
    private val  handleGoogleSignInUseCase: HandleGoogleSignInUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(SignInUiState())
    val uiState: StateFlow<SignInUiState> = _uiState.asStateFlow()

    private val emailValidator = Validator<String>(
        validationRule = { email ->
            email.matches(Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\$"))
        },
        errorMessage = "Некоректний формат електронної пошти"
    )

    private val passwordValidator = Validator<String>(
        validationRule = { it.isNotBlank() },
        errorMessage = "Пароль не може бути порожнім"
    )

    fun onEmailChange(newEmail: String) {
        _uiState.update { it.copy(email = newEmail) }
    }

    fun onPasswordChange(newPassword: String) {
        _uiState.update { it.copy(password = newPassword) }
    }

    fun onPasswordVisibilityToggle() {
        _uiState.update { it.copy(isPasswordVisible = !it.isPasswordVisible) }
    }


    fun signInWithGoogle(
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            val signedIn = try {
                googleAuthClient.signIn()
            } catch (e: Exception) {
                e.printStackTrace()
                false
            }

            if (!signedIn) {
                _uiState.update { it.copy(isLoading = false, errorMessage = "Помилка входу через Google") }
                onError("Помилка входу через Google")
                return@launch
            }

            val firebaseUser = FirebaseAuth.getInstance().currentUser
            val email = firebaseUser?.email
            val username = firebaseUser?.displayName ?: "Google User"

            if (email.isNullOrBlank()) {
                _uiState.update { it.copy(isLoading = false, errorMessage = "Email не знайдено") }
                onError("Email не знайдено")
                return@launch
            }

            val result = handleGoogleSignInUseCase(email, username)

            result.fold(
                onSuccess = {
                    _uiState.update { it.copy(isLoading = false) }
                    onSuccess()
                },
                onFailure = { error ->
                    _uiState.update { it.copy(isLoading = false, errorMessage = error.message) }
                    onError(error.message ?: "Сталася помилка")
                }
            )
        }
    }



    fun signIn(onSuccess: () -> Unit, onError: (String) -> Unit) {
        val state = uiState.value
        val emailValidation = emailValidator.validate(state.email.trim())
        val passwordValidation = passwordValidator.validate(state.password)

        val fieldErrors = mutableMapOf<String, String?>()

        if (!emailValidation.isValid) {
            fieldErrors["email"] = emailValidation.error
        }

        if (!passwordValidation.isValid) {
            fieldErrors["password"] = passwordValidation.error
        }

        if (fieldErrors.isNotEmpty()) {
            _uiState.update {
                it.copy(fieldErrors = fieldErrors)
            }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null, fieldErrors = emptyMap()) }

            val result = signInUserUseCase(state.email.trim(), state.password)

            result
                .onSuccess {
                    _uiState.update { it.copy(isLoading = false) }
                    onSuccess()
                }
                .onFailure { error ->
                    _uiState.update {
                        it.copy(isLoading = false, errorMessage = error.message)
                    }
                    onError(error.message ?: "Помилка авторизації")
                }
        }
    }
}
