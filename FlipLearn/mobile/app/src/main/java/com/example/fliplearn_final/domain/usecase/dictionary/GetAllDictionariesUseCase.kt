package com.example.fliplearn_final.domain.usecase.dictionary

import com.example.fliplearn_final.domain.model.Dictionary
import com.example.fliplearn_final.domain.repository.dictionary.DictionaryRepository
import kotlinx.coroutines.flow.Flow

class GetAllDictionariesUseCase(
    private val repository: DictionaryRepository
) {
    suspend operator fun invoke(): Flow<List<Dictionary>> {
        return repository.getAllDictionaries()
    }
}