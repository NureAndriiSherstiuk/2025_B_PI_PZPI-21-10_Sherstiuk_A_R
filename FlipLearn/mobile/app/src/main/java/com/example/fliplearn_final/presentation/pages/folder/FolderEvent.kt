package com.example.fliplearn_final.presentation.pages.folder

sealed class FolderEvent {
    data class NameChanged(val newName: String) : FolderEvent()
    data class DescriptionChanged(val newDescription: String) : FolderEvent()
    data class ToggleSetSelection(val setId: Int) : FolderEvent()
    data class SetSelectionMode(val enabled: Boolean) : FolderEvent()
    object ClearSelection : FolderEvent()
    object ToggleBottomSheet : FolderEvent()
    data class AddDictionariesToFolder(val folderId: Int, val selectedSetIds: List<Int>) : FolderEvent()
}
