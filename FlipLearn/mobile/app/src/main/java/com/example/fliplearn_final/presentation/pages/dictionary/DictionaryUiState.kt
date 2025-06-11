package com.example.fliplearn_final.presentation.pages.dictionary

import com.example.fliplearn_final.domain.model.Card
import com.example.fliplearn_final.domain.model.Dictionary


data class DictionaryWithCardsUiState(
    val isLoading: Boolean = true,
    val dictionary: Dictionary? = null,
    val cards: List<Card> = emptyList(),
    val errorMessage: String? = null
)