package com.example.fliplearn_final.presentation.pages.dictionary.create_dictionary

sealed interface CreateDictionaryEvent {
    data class EnteredTitle(val value: String) : CreateDictionaryEvent
    data class EnteredDescription(val value: String) : CreateDictionaryEvent
    data class SelectedFromLang(val lang: String) : CreateDictionaryEvent
    data class SelectedToLang(val lang: String) : CreateDictionaryEvent
    data class SelectedToLabel(val label: String): CreateDictionaryEvent
    data class ChangedAccess(val isPublic: Boolean) : CreateDictionaryEvent
    data class ChangedCardTerm(val index: Int, val term: String) : CreateDictionaryEvent
    data class ChangedCardMeaning(val index: Int, val meaning: String) : CreateDictionaryEvent
    data class ChangedCardTranslation(val index: Int, val translation: String) : CreateDictionaryEvent
    object AddCard : CreateDictionaryEvent
    object CreateDictionary : CreateDictionaryEvent
}
