package com.example.fliplearn_final.domain.model

import java.time.Instant

data class Card(
    val cardId: Int = 0,
    val dictionaryId: Int = 0,
    val term: String,
    val meaning: String,
    val translation: String,
    val createdAt: Instant = Instant.now()
)


