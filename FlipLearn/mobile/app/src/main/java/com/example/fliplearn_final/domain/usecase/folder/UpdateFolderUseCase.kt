package com.example.fliplearn_final.domain.usecase.folder

import com.example.fliplearn_final.domain.model.Folder
import com.example.fliplearn_final.domain.repository.folder.FolderRepository
import javax.inject.Inject

class UpdateFolderUseCase @Inject constructor(
    private val folderRepository: FolderRepository
) {
    suspend operator fun invoke(folder: Folder) {
        folderRepository.updateFolder(folder)
    }
}
