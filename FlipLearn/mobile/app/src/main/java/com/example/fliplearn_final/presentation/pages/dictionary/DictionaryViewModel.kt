package com.example.fliplearn_final.presentation.pages.dictionary

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fliplearn_final.domain.usecase.dictionary.GetDictionaryWithCardsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue
import javax.inject.Inject

@HiltViewModel
class DictionaryViewModel @Inject constructor(
    private val getDictionaryWithCardsUseCase: GetDictionaryWithCardsUseCase
) : ViewModel() {

    var uiState by mutableStateOf(DictionaryWithCardsUiState())
        private set

    fun loadDictionary(dictionaryId: Int) {
        viewModelScope.launch {
            try {
                uiState = uiState.copy(isLoading = true)
                val result = getDictionaryWithCardsUseCase(dictionaryId)
                if (result != null) {
                    uiState = uiState.copy(
                        isLoading = false,
                        dictionary = result.dictionary,
                        cards = result.cards
                    )
                } else {
                    uiState = uiState.copy(
                        isLoading = false,
                        errorMessage = "Dictionary not found"
                    )
                }
            } catch (e: Exception) {
                uiState = uiState.copy(
                    isLoading = false,
                    errorMessage = e.message ?: "Unexpected error"
                )
            }
        }
    }
}

