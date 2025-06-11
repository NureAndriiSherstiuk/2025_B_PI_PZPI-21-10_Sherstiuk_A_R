package com.example.fliplearn_final.presentation.pages.test_3

import com.example.fliplearn_final.domain.model.Card


data class Test3UiState(
    val currentIndex: Int = 0,
    val correctAnswersCount: Int = 0,
    val currentTerm: String = "",
    val totalTerms: Int = 0,
    val cards: List<Card> = emptyList(),
    val isTestFinished: Boolean = false,
    val userInput: String = "",
    val correctTranslation: String = "",
) {
    val currentCard: Card?
        get() = cards.getOrNull(currentIndex)

    val progress: Float
        get() = if (cards.isEmpty()) 0f else currentIndex.toFloat() / cards.size

}
