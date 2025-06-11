package com.example.fliplearn_final.data.repository

import com.example.fliplearn_final.data.local.dao.CardDao
import com.example.fliplearn_final.data.local.entity.CardEntity
import com.example.fliplearn_final.domain.model.Card
import com.example.fliplearn_final.domain.repository.card.CardRepository
import java.time.Instant

class CardRepositoryImpl(
    private val cardDao: CardDao
) : CardRepository {
    override suspend fun insertCards(cards: List<Card>) {
        val entities = cards.map { card ->
            CardEntity(
                cardId = card.cardId,
                dictionaryId = card.dictionaryId,
                term = card.term,
                meaning = card.meaning,
                translation = card.translation,
                createdAt = Instant.now()
            )
        }
        cardDao.insertCards(entities)
    }

    override suspend fun getCardsByDictionaryId(dictionaryId: Int): List<Card> {
        return cardDao.getCardsByDictionaryId(dictionaryId).map { entity ->
            Card(
                cardId = entity.cardId,
                dictionaryId = entity.dictionaryId,
                term = entity.term,
                meaning = entity.meaning,
                translation = entity.translation,
                createdAt = entity.createdAt
            )
        }
    }
}