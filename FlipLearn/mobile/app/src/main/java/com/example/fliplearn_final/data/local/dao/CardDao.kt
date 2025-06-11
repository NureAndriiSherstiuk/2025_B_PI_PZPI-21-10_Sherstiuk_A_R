package com.example.fliplearn_final.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.fliplearn_final.data.local.entity.CardEntity

@Dao
interface CardDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCards(cards: List<CardEntity>)

    @Query("SELECT * FROM card WHERE dictionary_id = :dictionaryId")
    suspend fun getCardsByDictionaryId(dictionaryId: Int): List<CardEntity>
}