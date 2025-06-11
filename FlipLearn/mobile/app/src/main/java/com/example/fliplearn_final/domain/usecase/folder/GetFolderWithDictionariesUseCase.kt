package com.example.fliplearn_final.domain.usecase.folder

import com.example.fliplearn_final.domain.model.FolderWithDictionaries
import com.example.fliplearn_final.domain.repository.folder.FolderRepository
import javax.inject.Inject

class GetFolderWithDictionariesUseCase @Inject constructor(
    private val repository: FolderRepository
) {
    suspend operator fun invoke(folderId: Int): FolderWithDictionaries {
        return repository.getFolderWithDictionaries(folderId)
    }
}