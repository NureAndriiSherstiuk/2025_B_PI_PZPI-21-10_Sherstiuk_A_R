package com.example.fliplearn_final.domain.usecase.dictionary

import com.example.fliplearn_final.domain.model.Card
import com.example.fliplearn_final.domain.model.Dictionary
import com.example.fliplearn_final.domain.repository.card.CardRepository
import com.example.fliplearn_final.domain.repository.dictionary.DictionaryRepository


data class DictionaryWithCards(
    val dictionary: Dictionary,
    val cards: List<Card>
)

class GetDictionaryWithCardsUseCase(
    private val dictionaryRepository: DictionaryRepository,
    private val cardRepository: CardRepository
) {
    suspend operator fun invoke(dictionaryId: Int): DictionaryWithCards? {
        val dictionary = dictionaryRepository.getDictionaryById(dictionaryId)
        return dictionary?.let {
            val cards = cardRepository.getCardsByDictionaryId(dictionaryId)
            DictionaryWithCards(dictionary = it, cards = cards)
        }
    }
}