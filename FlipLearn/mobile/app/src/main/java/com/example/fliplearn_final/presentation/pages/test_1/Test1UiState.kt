package com.example.fliplearn_final.presentation.pages.test_1

import com.example.fliplearn_final.domain.model.Card


data class Test1UiState(
    val currentIndex: Int = 0,
    val correctAnswers: Int = 0,
    val isFinished: Boolean = false,
    val cards: List<Card> = emptyList()
) {
    val progress: Float
        get() = if (cards.isEmpty()) 0f else currentIndex.toFloat() / cards.size

    val currentCard: Card?
        get() = cards.getOrNull(currentIndex)
}
