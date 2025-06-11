package com.example.fliplearn_final.presentation.pages.sign_up

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fliplearn_final.data.remote.auth.GoogleAuthClient
import com.example.fliplearn_final.domain.model.User
import com.example.fliplearn_final.domain.usecase.user.HandleGoogleSignInUseCase
import com.example.fliplearn_final.domain.usecase.user.SignUpUserUseCase
import com.example.fliplearn_final.util.PasswordHasher
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
class SignUpViewModel @Inject constructor(
    private val signUpUserUseCase: SignUpUserUseCase,
    private val googleAuthClient: GoogleAuthClient,
    private val  handleGoogleSignInUseCase: HandleGoogleSignInUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(SignUpUiState())
    val uiState: StateFlow<SignUpUiState> = _uiState.asStateFlow()

    private val emailValidator = Validator<String>(
        validationRule = { email ->
            email.matches(Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\$"))
        },
        errorMessage = "Некоректний формат електронної пошти"
    )

    private val nonEmptyValidator = Validator<String>(
        validationRule = { it.isNotBlank() },
        errorMessage = "Це поле не може бути порожнім"
    )

    private val passwordMatchValidator = Validator<Pair<String, String>>(
        validationRule = { (password, confirmPassword) ->
            password == confirmPassword
        },
        errorMessage = "Паролі не збігаються"
    )

    fun onEmailChange(email: String) {
        _uiState.update { it.copy(email = email) }
    }

    fun onUserNameChange(userName: String) {
        _uiState.update { it.copy(userName = userName) }
    }

    fun onPasswordChange(password: String) {
        _uiState.update { it.copy(password = password) }
    }

    fun onConfirmPasswordChange(confirmPassword: String) {
        _uiState.update { it.copy(confirmPassword = confirmPassword) }
    }

    fun togglePasswordVisibility() {
        _uiState.update { it.copy(isPasswordVisible = !it.isPasswordVisible) }
    }

    fun signUpWithGoogle (onSuccess: () -> Unit, onError: (String) -> Unit) {
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

    fun signUp(onSuccess: () -> Unit, onError: (String) -> Unit) {
        val state = uiState.value

        val errors = mutableMapOf<String, String?>()

        val emailValidation = emailValidator.validate(state.email.trim())
        if (!emailValidation.isValid) errors["email"] = emailValidation.error

        val usernameValidation = nonEmptyValidator.validate(state.userName.trim())
        if (!usernameValidation.isValid) errors["userName"] = usernameValidation.error

        val passwordValidation = nonEmptyValidator.validate(state.password)
        if (!passwordValidation.isValid) errors["password"] = passwordValidation.error

        if (state.confirmPassword.isBlank()) {
            errors["confirmPassword"] = "Це поле не може бути порожнім"
        } else {
            val matchValidation = passwordMatchValidator.validate(state.password to state.confirmPassword)
            if (!matchValidation.isValid) errors["confirmPassword"] = matchValidation.error
        }

        if (errors.isNotEmpty()) {
            _uiState.update { it.copy(fieldErrors = errors) }
            onError("Будь ласка, виправте помилки у формах")
            return
        } else {
            _uiState.update { it.copy(fieldErrors = emptyMap()) }
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            val salt = PasswordHasher.generateSalt()
            val hashedPassword = PasswordHasher.hashPassword(state.password, salt)

            val result = signUpUserUseCase(
                User(
                    email = state.email.trim(),
                    password = hashedPassword,
                    username = state.userName.trim()
                )
            )

            result
                .onSuccess {
                    _uiState.update { it.copy(isLoading = false) }
                    onSuccess()
                }
                .onFailure { error ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = error.message ?: "Щось пішло не так"
                        )
                    }
                    onError(error.message ?: "Помилка при реєстрації")
                }
        }
    }
}
