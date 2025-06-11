package com.example.fliplearn_final.presentation.pages.main

data class DictionaryUiState(
    val id: Int,
    val title: String,
    val description: String?,
    val isPublic: Boolean,
    val label: String,
    val termsCount: Int,
    val username: String
)


data class FolderUiState(
    val id: Int,
    val name: String,
    val description: String?,
    val dictionariesCount: Int?,
    val termsCount: Int?,
    val label: String? = "A1",
    val username: String?
)
