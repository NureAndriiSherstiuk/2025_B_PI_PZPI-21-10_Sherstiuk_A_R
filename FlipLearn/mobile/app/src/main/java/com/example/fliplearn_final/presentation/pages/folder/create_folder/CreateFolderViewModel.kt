package com.example.fliplearn_final.presentation.pages.folder.create_folder

import android.util.Log
import com.example.fliplearn_final.domain.model.Folder
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fliplearn_final.data.local.datastore.UserPreferences
import com.example.fliplearn_final.domain.usecase.folder.CreateFolderUseCase
import com.example.fliplearn_final.domain.usecase.user.GetUserProfileInfoUseCase
import com.example.fliplearn_final.util.UiEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CreateFolderViewModel @Inject constructor(
    private val createFolderUseCase: CreateFolderUseCase,
    private val userPreferences: UserPreferences,
    private val getUserProfileInfoUseCase: GetUserProfileInfoUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(CreateFolderUiState())
    val uiState: StateFlow<CreateFolderUiState> = _uiState.asStateFlow()

    private val _uiEvent = Channel<UiEvent>()
    val uiEvent = _uiEvent.receiveAsFlow()

    init {
        loadUserId()
    }

    private fun loadUserId() {
        viewModelScope.launch {
            val email = userPreferences.getUserEmail()
            if (!email.isNullOrEmpty()) {
                val user = getUserProfileInfoUseCase(email)
                user?.let {
                    _uiState.update { currentState ->
                        currentState.copy(userId = it.id)
                    }
                }
            }
        }
    }

    fun onFolderNameChange(newName: String) {
        _uiState.update { it.copy(name = newName) }
    }

    fun onFolderDescriptionChange(newDescription: String) {
        _uiState.update { it.copy(description = newDescription) }
    }

    fun onCreateFolderClick() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            val folder = Folder(
                folderId = 0,
                userId = _uiState.value.userId,
                name = _uiState.value.name,
                description = _uiState.value.description,
            )

            runCatching {
                createFolderUseCase(folder)
            }.onSuccess {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        isSuccess = true,
                        name = "",
                        description = ""
                    )
                }
                _uiEvent.send(UiEvent.ShowToast("Папку успішно створено!"))
            }.onFailure { ex ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = ex.message ?: "Помилка при створенні папки"
                    )
                }
                Log.d("CreateFolder", "Error: ${ex.message}")
                _uiEvent.send(UiEvent.ShowToast("Помилка: ${ex.message ?: "невідома"}"))
            }
        }
    }
}
