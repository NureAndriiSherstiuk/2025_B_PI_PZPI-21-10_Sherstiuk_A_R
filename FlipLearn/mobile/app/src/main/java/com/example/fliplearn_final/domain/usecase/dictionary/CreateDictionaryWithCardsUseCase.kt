package com.example.fliplearn_final.domain.usecase.dictionary

import com.example.fliplearn_final.domain.model.Card
import com.example.fliplearn_final.domain.model.Dictionary
import com.example.fliplearn_final.domain.repository.card.CardRepository
import com.example.fliplearn_final.domain.repository.dictionary.DictionaryRepository




class CreateDictionaryWithCardsUseCase(
    private val dictionaryRepository: DictionaryRepository,
    private val cardRepository: CardRepository
) {
    suspend operator fun invoke(dictionary: Dictionary, cards: List<Card>) {
        require(cards.isNotEmpty()) { "At least one card must be provided" }

        val dictionaryId = dictionaryRepository.insertDictionary(dictionary)

        val cardsWithDictionaryId = cards.map { it.copy(dictionaryId = dictionaryId.toInt()) }

        cardRepository.insertCards(cardsWithDictionaryId)
    }
}
