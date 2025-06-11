package com.example.fliplearn_final.domain.repository.dictionary

import com.example.fliplearn_final.domain.model.Card
import com.example.fliplearn_final.domain.model.Dictionary
import kotlinx.coroutines.flow.Flow

interface DictionaryRepository {
    suspend fun insertDictionary(dictionary: Dictionary): Long
    suspend fun getAllDictionaries(): Flow<List<Dictionary>>
    suspend fun getDictionaryById(id: Int): Dictionary?
    fun getAvailableDictionaries(folderId: Int): Flow<List<Dictionary>>
    fun getDictionariesByFolderId(folderId: Int): Flow<List<Dictionary>>

}