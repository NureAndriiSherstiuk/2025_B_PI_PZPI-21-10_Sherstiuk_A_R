package com.example.fliplearn_final.presentation.pages.folder


import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fliplearn_final.data.local.datastore.UserPreferences
import com.example.fliplearn_final.domain.model.Dictionary
import com.example.fliplearn_final.domain.model.Folder
import com.example.fliplearn_final.domain.usecase.dictionary.GetAvailableDictionariesUseCase
import com.example.fliplearn_final.domain.usecase.dictionary.GetDictionariesByFolderUseCase
import com.example.fliplearn_final.domain.usecase.folder.AddDictionaryToFolderUseCase
import com.example.fliplearn_final.domain.usecase.folder.DeleteFolderUseCase
import com.example.fliplearn_final.domain.usecase.folder.GetFolderWithDictionariesUseCase
import com.example.fliplearn_final.domain.usecase.folder.GetFoldersWithStatsUseCase
import com.example.fliplearn_final.domain.usecase.folder.UpdateFolderUseCase
import com.example.fliplearn_final.domain.usecase.user.GetUserProfileInfoUseCase
import com.example.fliplearn_final.presentation.pages.main.FolderUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FolderViewModel @Inject constructor(
    private val getFolderWithDictionariesUseCase: GetFolderWithDictionariesUseCase,
    private val userPreferences: UserPreferences,
    private val getAvailableDictionariesUseCase: GetAvailableDictionariesUseCase,
    private val addDictionaryToFolder: AddDictionaryToFolderUseCase,
    private val getUserProfileInfoUseCase: GetUserProfileInfoUseCase,
    private val getFoldersWithStatsUseCase: GetFoldersWithStatsUseCase,
    private val getDictionariesByFolderUseCase: GetDictionariesByFolderUseCase,
    private val updateFolderUseCase: UpdateFolderUseCase,
    private val deleteFolderUseCase: DeleteFolderUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(FolderWithDictionariesState())
    val state: StateFlow<FolderWithDictionariesState> = _state

    private val _isEditMode = mutableStateOf(false)
    val isEditMode: State<Boolean> = _isEditMode

    private val _showMenu = mutableStateOf(false)
    val showMenu: State<Boolean> = _showMenu

    private val _dictionariesInFolder = MutableStateFlow<List<Dictionary>>(emptyList())
    val dictionariesInFolder: StateFlow<List<Dictionary>> = _dictionariesInFolder

    private val _folderName = mutableStateOf("")
    val folderName: State<String> = _folderName

    private val _folderDescription = mutableStateOf("")
    val folderDescription: State<String> = _folderDescription

    private val _navigateToMain = MutableStateFlow(false)
    val navigateToMain: StateFlow<Boolean> = _navigateToMain


    private val _folders = MutableStateFlow<List<FolderUiState>>(emptyList())
    val folders: StateFlow<List<FolderUiState>> = _folders

    private val _isDictionaryPickerVisible = mutableStateOf(false)
    val isDictionaryPickerVisible: State<Boolean> = _isDictionaryPickerVisible

    private val _availableDictionaries = mutableStateListOf<Dictionary>()
    val availableDictionaries: List<Dictionary> get() = _availableDictionaries

    private var currentFolderId: Int? = null


    fun onFolderNameChange(newName: String) {
        _folderName.value = newName
    }

    fun onFolderDescriptionChange(newDescription: String) {
        _folderDescription.value = newDescription
    }


    fun toggleEditMode() {
        _isEditMode.value = !_isEditMode.value
    }

    fun showMenu() {
        _showMenu.value = true
    }

    fun hideMenu() {
        _showMenu.value = false
    }

    fun showDictionaryPicker(folderId: Int) {
        currentFolderId = folderId
        viewModelScope.launch {
            getAvailableDictionariesUseCase(folderId).collect { available ->
                _availableDictionaries.clear()
                _availableDictionaries.addAll(available)
                _isDictionaryPickerVisible.value = true
            }
        }
    }

    fun hideDictionaryPicker() {
        _isDictionaryPickerVisible.value = false
    }

    fun addDictionaryToFolder(dictionary: Dictionary) {
        val folderId = currentFolderId ?: return
        viewModelScope.launch {
            addDictionaryToFolder(folderId, dictionary.dictionaryId)
            loadDictionariesInFolder(folderId)
            loadFolder(folderId)
            hideDictionaryPicker()
        }
    }

    init {
        viewModelScope.launch {
            val email = userPreferences.userEmail.firstOrNull()
            if (!email.isNullOrBlank()) {
                val user = getUserProfileInfoUseCase(email)
                val userId = user?.id
                if (userId != null) {
                    getFoldersWithStatsUseCase(userId).collect { folders ->
                        _folders.value = folders.map {
                            FolderUiState(
                                id = it.folderId,
                                name = it.name,
                                description = it.description,
                                dictionariesCount = it.dictionariesCount,
                                termsCount = it.termsCount,
                                label = it.label,
                                username = it.userName
                            )
                        }
                    }
                }
            }
        }
    }

    fun loadFolder(folderId: Int) {
        currentFolderId = folderId
        viewModelScope.launch {
            _state.value = FolderWithDictionariesState(isLoading = true)
            try {
                val result = getFolderWithDictionariesUseCase(folderId)
                _state.value = FolderWithDictionariesState(folder = result)
            } catch (e: Exception) {
                _state.value = FolderWithDictionariesState(error = e.message ?: "Unknown error")
            }
        }
    }

    fun loadDictionariesInFolder(folderId: Int) {
        viewModelScope.launch {
            getDictionariesByFolderUseCase(folderId).collect { dictionaries ->
                _dictionariesInFolder.value = dictionaries
            }
        }
    }

    fun updateFolder() {
        viewModelScope.launch {
            val email = userPreferences.userEmail.firstOrNull()
            if (!email.isNullOrBlank()) {
                val user = getUserProfileInfoUseCase(email)
                val userId = user?.id
                if (userId != null) {
                    _state.value.folder?.let { fwd ->
                        val folder = Folder(
                            folderId = fwd.folderId,
                            name = _folderName.value,
                            description = _folderDescription.value,
                            userId = userId
                        )
                        try {
                            updateFolderUseCase(folder)

                            loadFolder(folder.folderId)

                        } catch (e: Exception) {
                            Log.e("FolderViewModel", "Error updating folder", e)
                        }
                    }
                } else {
                    Log.w("FolderViewModel", "User ID is null, cannot update folder")
                }
            } else {
                Log.w("FolderViewModel", "Email is blank, cannot update folder")
            }
        }
    }

    fun deleteFolder() {
        viewModelScope.launch {
            val email = userPreferences.userEmail.firstOrNull()
            if (!email.isNullOrBlank()) {
                val user = getUserProfileInfoUseCase(email)
                val userId = user?.id
                if (userId != null) {
                    _state.value.folder?.let { folder ->
                        try {
                            val folderToDelete = Folder(
                                folderId = folder.folderId,
                                name = folder.name,
                                description = folder.description,
                                userId = userId
                            )
                            deleteFolderUseCase(folderToDelete)
                            Log.d("FolderViewModel", "Folder deleted successfully")
                            _navigateToMain.value = true

                        } catch (e: Exception) {
                            Log.e("FolderViewModel", "Error deleting folder", e)
                        }
                    }
                }
            }

        }
    }

}
