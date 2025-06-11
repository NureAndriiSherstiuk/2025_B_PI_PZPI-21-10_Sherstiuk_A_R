package com.example.fliplearn_final.presentation.pages.folder

import com.example.fliplearn_final.domain.model.FolderWithDictionaries


data class FolderWithDictionariesState(
    val isLoading: Boolean = false,
    val folder: FolderWithDictionaries? = null,
    val error: String? = null
)

data class DictionaryUiState(
    val id: Int,
    val title: String,
    val description: String?,
    val isPublic: Boolean,
    val label: String,
    val termsCount: Int,
    val username: String
)