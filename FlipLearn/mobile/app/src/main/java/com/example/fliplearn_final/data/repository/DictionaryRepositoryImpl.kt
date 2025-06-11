package com.example.fliplearn_final.data.repository

import com.example.fliplearn_final.data.local.dao.DictionaryDao
import com.example.fliplearn_final.data.local.entity.DictionaryEntity
import com.example.fliplearn_final.domain.model.Dictionary
import com.example.fliplearn_final.domain.repository.dictionary.DictionaryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.Instant

class DictionaryRepositoryImpl(
    private val dictionaryDao: DictionaryDao
) : DictionaryRepository {
    override suspend fun insertDictionary(dictionary: Dictionary): Long {
        val entity = DictionaryEntity(
            dictionaryId = dictionary.dictionaryId,
            folderId = dictionary.folderId,
            userId = dictionary.userId,
            title = dictionary.title,
            description = dictionary.description,
            isPublic = dictionary.isPublic,
            fromLang = dictionary.fromLang,
            toLang = dictionary.toLang,
            label = dictionary.label,
            termsCount = dictionary.termsCount,
            userName = dictionary.userName,
            createdAt = Instant.now()
        )
        return dictionaryDao.insertDictionary(entity)
    }

    override suspend fun getAllDictionaries(): Flow<List<Dictionary>> {
        return dictionaryDao.getAllDictionaries().map { entityList  ->
            entityList.map {
                entity ->
                Dictionary(
                    dictionaryId = entity.dictionaryId,
                    folderId = entity.folderId,
                    userId = entity.userId,
                    title = entity.title,
                    description = entity.description,
                    isPublic = entity.isPublic,
                    fromLang = entity.fromLang,
                    toLang = entity.toLang,
                    label = entity.label,
                    termsCount = entity.termsCount,
                    userName = entity.userName,
                    createdAt = entity.createdAt
                )
            }
        }
    }

    override suspend fun getDictionaryById(id: Int): Dictionary? {
        val entity = dictionaryDao.getDictionaryById(id)
        return entity?.let {
            Dictionary(
                dictionaryId = it.dictionaryId,
                folderId = it.folderId,
                userId = it.userId,
                userName = it.userName,
                title = it.title,
                description = it.description,
                isPublic = it.isPublic,
                fromLang = it.fromLang,
                toLang = it.toLang,
                label = it.label,
                termsCount = it.termsCount,
                createdAt = it.createdAt
            )
        }
    }

    override fun getAvailableDictionaries(folderId: Int): Flow<List<Dictionary>> {
        return dictionaryDao.getAvailableDictionaries(folderId).map { entityList ->
            entityList.map { entity ->
                Dictionary(
                    dictionaryId = entity.dictionaryId,
                    folderId = entity.folderId,
                    userId = entity.userId,
                    title = entity.title,
                    description = entity.description,
                    isPublic = entity.isPublic,
                    fromLang = entity.fromLang,
                    toLang = entity.toLang,
                    label = entity.label,
                    termsCount = entity.termsCount,
                    userName = entity.userName,
                    createdAt = entity.createdAt
                )
            }
        }
    }

    override fun getDictionariesByFolderId(folderId: Int): Flow<List<Dictionary>> {
        return dictionaryDao.getDictionariesByFolderId(folderId).map { list ->
            list.map { dict ->
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
        }
    }


}

