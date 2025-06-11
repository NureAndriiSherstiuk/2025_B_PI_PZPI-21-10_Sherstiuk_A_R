package com.example.fliplearn_final.data.local.dao

import androidx.room.*
import com.example.fliplearn_final.data.local.entity.FolderDictionaryCrossRef
import com.example.fliplearn_final.data.local.entity.FolderEntity
import com.example.fliplearn_final.data.local.entity.FolderWithDictionariesEntity
import com.example.fliplearn_final.data.local.entity.FolderWithStats
import kotlinx.coroutines.flow.Flow

@Dao
interface FolderDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFolder(folder: FolderEntity)

    @Query("""
    SELECT 
        f.folder_id AS folderId,
        f.user_id AS userId,
        f.name AS name,
        f.description AS description,
        f.created_at AS createdAt,
        COUNT(DISTINCT fd.dictionary_id) AS dictionariesCount,
        COUNT(c.card_id) AS termsCount,
        d.label AS label,
        u.username AS userName
    FROM folder f
    LEFT JOIN folder_dictionary_cross_ref fd ON fd.folder_id = f.folder_id
    LEFT JOIN dictionary d ON d.dictionary_id = fd.dictionary_id
    LEFT JOIN card c ON c.dictionary_id = d.dictionary_id
    LEFT JOIN user u ON u.user_id = f.user_id
    WHERE f.user_id = :userId
    GROUP BY f.folder_id
""")
    fun getFoldersWithStatsByUserId(userId: Int): Flow<List<FolderWithStats>>



    @Update
    suspend fun updateFolder(folder: FolderEntity)

    @Delete
    suspend fun deleteFolder(folder: FolderEntity)

    @Query("SELECT * FROM folder WHERE user_id = :userId")
    suspend fun getFoldersByUserId(userId: Int): List<FolderEntity>

    @Query("SELECT * FROM folder WHERE folder_id = :folderId LIMIT 1")
    suspend fun getFolderById(folderId: Int): FolderEntity?

    @Transaction
    @Query("SELECT * FROM folder WHERE folder_id = :folderId")
    suspend fun getFolderWithDictionaries(folderId: Int): FolderWithDictionariesEntity

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCrossRef(crossRef: FolderDictionaryCrossRef)
}
