package com.example.fliplearn_final.presentation.pages.folder.create_folder


data class CreateFolderUiState(
    val userId: Int = 0,
    val name: String = "",
    val description: String = "",
    val isLoading: Boolean = false,
    val error: String? = null,
    val isSuccess: Boolean = false
)
