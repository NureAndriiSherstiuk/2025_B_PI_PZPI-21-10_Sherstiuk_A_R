package com.example.fliplearn_final.domain.repository.card

import com.example.fliplearn_final.domain.model.Card

interface CardRepository {
    suspend fun insertCards(cards: List<Card>)
    suspend fun getCardsByDictionaryId(dictionaryId: Int): List<Card>
}