package com.example.fliplearn_final.presentation.pages.dictionary.create_dictionary


data class CardInput(
    val term: String = "",
    val meaning: String = "",
    val translation: String = ""
)

data class CreateDictionaryUiState(
    val title: String = "",
    val description: String = "",
    val fromLang: String = "",
    val toLang: String = "",
    val isPublic: Boolean = true,
    val label: String = "",
    val cards: List<CardInput> = listOf(CardInput()),
    val isSubmitting: Boolean = false,
    val error: String? = null
)
