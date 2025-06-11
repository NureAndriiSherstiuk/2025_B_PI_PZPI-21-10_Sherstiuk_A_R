package com.example.fliplearn_final.domain.repository.folder

import com.example.fliplearn_final.domain.model.Folder
import com.example.fliplearn_final.domain.model.FolderWithDictionaries
import kotlinx.coroutines.flow.Flow

interface FolderRepository {
    suspend fun createFolder(folder: Folder)
    suspend fun getFoldersByUserId(userId: Int): List<Folder>
    suspend fun updateFolder(folder: Folder)
    suspend fun deleteFolder(folder: Folder)
    suspend fun getFolderById(folderId: Int): Folder?
    fun getFoldersWithStatsByUserId(userId: Int): Flow<List<Folder>>
    suspend fun getFolderWithDictionaries(folderId: Int): FolderWithDictionaries
    suspend fun addDictionaryToFolder(folderId: Int, dictionaryId: Int)
}