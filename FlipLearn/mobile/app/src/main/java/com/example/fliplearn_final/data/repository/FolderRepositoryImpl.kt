package com.example.fliplearn_final.data.repository

import com.example.fliplearn_final.data.local.dao.FolderDao
import com.example.fliplearn_final.data.local.entity.FolderDictionaryCrossRef
import com.example.fliplearn_final.data.local.entity.FolderEntity
import com.example.fliplearn_final.domain.model.Dictionary
import com.example.fliplearn_final.domain.model.Folder
import com.example.fliplearn_final.domain.model.FolderWithDictionaries
import com.example.fliplearn_final.domain.repository.folder.FolderRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map


class FolderRepositoryImpl(
    private val folderDao: FolderDao
) : FolderRepository {

    override suspend fun createFolder(folder: Folder) {
        val folderEntity = FolderEntity(
            folderId = folder.folderId,
            userId = folder.userId,
            name = folder.name,
            description = folder.description,
            createdAt = folder.createdAt
        )
        folderDao.insertFolder(folderEntity)
    }

    override suspend fun getFoldersByUserId(userId: Int): List<Folder> {
        return folderDao.getFoldersByUserId(userId).map { entity ->
            Folder(
                folderId = entity.folderId,
                userId = entity.userId,
                name = entity.name,
                description = entity.description,
                createdAt = entity.createdAt
            )
        }
    }

    override suspend fun updateFolder(folder: Folder) {
        val folderEntity = FolderEntity(
            folderId = folder.folderId,
            userId = folder.userId,
            name = folder.name,
            description = folder.description,
            createdAt = folder.createdAt
        )
        folderDao.updateFolder(folderEntity)
    }

    override suspend fun deleteFolder(folder: Folder) {
        val folderEntity = FolderEntity(
            folderId = folder.folderId,
            userId = folder.userId,
            name = folder.name,
            description = folder.description,
            createdAt = folder.createdAt
        )
        folderDao.deleteFolder(folderEntity)
    }

    override suspend fun getFolderById(folderId: Int): Folder? {
        return folderDao.getFolderById(folderId)?.let { entity ->
            Folder(
                folderId = entity.folderId,
                userId = entity.userId,
                name = entity.name,
                description = entity.description,
                createdAt = entity.createdAt
            )
        }
    }
    override fun getFoldersWithStatsByUserId(userId: Int): Flow<List<Folder>> {
        return folderDao.getFoldersWithStatsByUserId(userId).map { list ->
            list.map { stats ->
                Folder(
                    folderId = stats.folderId,
                    userId = stats.userId,
                    name = stats.name,
                    description = stats.description,
                    createdAt = stats.createdAt,
                    dictionariesCount = stats.dictionariesCount,
                    termsCount = stats.termsCount,
                    label = stats.label,
                    userName = stats.userName
                )
            }
        }
    }

    override suspend fun getFolderWithDictionaries(folderId: Int): FolderWithDictionaries {
        val entity = folderDao.getFolderWithDictionaries(folderId)
        return FolderWithDictionaries(
            folderId = entity.folder.folderId,
            name = entity.folder.name,
            description = entity.folder.description,
            createdAt = entity.folder.createdAt,
            dictionaries = entity.dictionaries.map { dict ->
                Dictionary(
                    dictionaryId = dict.dictionaryId,
                    folderId = dict.folderId,
                    userId = dict.userId,
                    userName = dict.userName,
                    title = dict.title,
                    description = dict.description,
                    isPublic = dict.isPublic,
                    fromLang = dict.fromLang,
                    toLang = dict.toLang,
                    label = dict.label,
                    termsCount = dict.termsCount,
                    createdAt = dict.createdAt
                )
            }
        )
    }

    override suspend fun addDictionaryToFolder(folderId: Int, dictionaryId: Int) {
        folderDao.insertCrossRef(FolderDictionaryCrossRef(folderId, dictionaryId))
    }


}