package com.example.fliplearn_final.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.fliplearn_final.data.local.entity.DictionaryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface DictionaryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDictionary(dictionary: DictionaryEntity): Long

    @Query("SELECT * FROM dictionary")
    fun getAllDictionaries(): Flow<List<DictionaryEntity>>

    @Query("SELECT * FROM dictionary WHERE dictionary_id = :id")
    suspend fun getDictionaryById(id: Int): DictionaryEntity?



    @Query("""
    SELECT * FROM dictionary
    WHERE dictionary_id NOT IN (
        SELECT dictionary_id FROM folder_dictionary_cross_ref WHERE folder_id = :folderId
    )
""")
    fun getAvailableDictionaries(folderId: Int): Flow<List<DictionaryEntity>>

    @Query("""
        SELECT d.* FROM dictionary d
        INNER JOIN folder_dictionary_cross_ref fdc ON d.dictionary_id = fdc.dictionary_id
        WHERE fdc.folder_id = :folderId
    """)
    fun getDictionariesByFolderId(folderId: Int): Flow<List<DictionaryEntity>>

}