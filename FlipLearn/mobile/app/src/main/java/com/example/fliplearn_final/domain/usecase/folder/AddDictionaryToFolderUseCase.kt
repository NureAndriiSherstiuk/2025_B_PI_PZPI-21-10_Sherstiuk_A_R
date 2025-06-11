package com.example.fliplearn_final.domain.usecase.folder

import com.example.fliplearn_final.domain.repository.folder.FolderRepository
import javax.inject.Inject


class AddDictionaryToFolderUseCase @Inject constructor(
    private val folderRepository: FolderRepository
) {
    suspend operator fun invoke(folderId: Int, dictionaryId: Int) {
        folderRepository.addDictionaryToFolder(folderId, dictionaryId)
    }
}
