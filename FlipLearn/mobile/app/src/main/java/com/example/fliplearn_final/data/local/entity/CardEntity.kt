package com.example.fliplearn_final.data.local.entity

import androidx.room.*
import java.time.Instant

@Entity(
    tableName = "card",
    foreignKeys = [
        ForeignKey(
            entity = DictionaryEntity::class,
            parentColumns = ["dictionary_id"],
            childColumns = ["dictionary_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["dictionary_id"])
    ]
)
data class CardEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "card_id")
    val cardId: Int = 0,

    @ColumnInfo(name = "dictionary_id")
    val dictionaryId: Int,

    @ColumnInfo(name = "term")
    val term: String,

    @ColumnInfo(name = "meaning")
    val meaning: String,

    @ColumnInfo(name = "translation")
    val translation: String,

    @ColumnInfo(name = "created_at")
    val createdAt: Instant = Instant.now()
)
