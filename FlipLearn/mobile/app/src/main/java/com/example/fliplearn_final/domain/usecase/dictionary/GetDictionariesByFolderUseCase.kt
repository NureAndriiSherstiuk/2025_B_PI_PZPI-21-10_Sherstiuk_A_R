package com.example.fliplearn_final.domain.usecase.dictionary


import com.example.fliplearn_final.domain.model.Dictionary
import com.example.fliplearn_final.domain.repository.dictionary.DictionaryRepository
import kotlinx.coroutines.flow.Flow

class GetDictionariesByFolderUseCase(
    private val repository: DictionaryRepository
) {
    operator fun invoke(folderId: Int): Flow<List<Dictionary>> {
        return repository.getDictionariesByFolderId(folderId)
    }
}

