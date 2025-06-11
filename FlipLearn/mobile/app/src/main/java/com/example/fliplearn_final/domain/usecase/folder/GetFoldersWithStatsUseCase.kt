package com.example.fliplearn_final.domain.usecase.folder

import com.example.fliplearn_final.domain.model.Folder
import com.example.fliplearn_final.domain.repository.folder.FolderRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetFoldersWithStatsUseCase @Inject constructor(
    private val folderRepository: FolderRepository
) {
    operator fun invoke(userId: Int): Flow<List<Folder>> {
        return folderRepository.getFoldersWithStatsByUserId(userId)
    }
}