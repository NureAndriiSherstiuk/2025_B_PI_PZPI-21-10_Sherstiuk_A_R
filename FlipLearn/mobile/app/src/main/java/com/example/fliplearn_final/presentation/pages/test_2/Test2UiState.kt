package com.example.fliplearn_final.presentation.pages.test_2

data class Test2UiState(
    val cards: List<Card> = emptyList(),
    val currentIndex: Int = 0,
    val correctAnswers: Int = 0,
    val isAnswerSelected: Boolean = false,
    val isFinished: Boolean = false,
    val isLoading: Boolean = false
)


data class Card(
    val term: String,
    val correctOption: String,
    val options: List<String>
)
