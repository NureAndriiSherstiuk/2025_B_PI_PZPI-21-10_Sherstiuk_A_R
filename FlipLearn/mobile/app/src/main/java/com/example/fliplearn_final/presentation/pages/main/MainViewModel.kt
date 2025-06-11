package com.example.fliplearn_final.presentation.pages.main


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fliplearn_final.data.local.datastore.UserPreferences
import com.example.fliplearn_final.domain.usecase.dictionary.GetAllDictionariesUseCase
import com.example.fliplearn_final.domain.usecase.folder.GetFoldersWithStatsUseCase
import com.example.fliplearn_final.domain.usecase.user.GetUserProfileInfoUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val getAllDictionariesUseCase: GetAllDictionariesUseCase,
    private val getFoldersWithStatsUseCase: GetFoldersWithStatsUseCase,
    private val getUserProfileInfoUseCase: GetUserProfileInfoUseCase,
    private val userPreferences: UserPreferences
) : ViewModel() {

    private val _dictionaries = MutableStateFlow<List<DictionaryUiState>>(emptyList())
    val dictionaries: StateFlow<List<DictionaryUiState>> = _dictionaries

    private val _folders = MutableStateFlow<List<FolderUiState>>(emptyList())
    val folders: StateFlow<List<FolderUiState>> = _folders



    init {
        viewModelScope.launch {
            getAllDictionariesUseCase().collect { dictionaries ->
                _dictionaries.value = dictionaries.map {
                    DictionaryUiState(
                        id = it.dictionaryId,
                        title = it.title,
                        description = it.description,
                        isPublic = it.isPublic,
                        label = it.label,
                        termsCount = it.termsCount,
                        username = it.userName
                    )
                }
            }
        }

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

}