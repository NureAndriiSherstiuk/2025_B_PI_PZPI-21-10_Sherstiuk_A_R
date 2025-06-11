package com.example.fliplearn_final.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey


@Entity(
    tableName = "folder_dictionary_cross_ref",
    primaryKeys = ["folder_id", "dictionary_id"],
    foreignKeys = [
        ForeignKey(entity = FolderEntity::class, parentColumns = ["folder_id"], childColumns = ["folder_id"], onDelete = ForeignKey.CASCADE),
        ForeignKey(entity = DictionaryEntity::class, parentColumns = ["dictionary_id"], childColumns = ["dictionary_id"], onDelete = ForeignKey.CASCADE)
    ]
)
data class FolderDictionaryCrossRef(
    @ColumnInfo(name = "folder_id") val folderId: Int,
    @ColumnInfo(name = "dictionary_id") val dictionaryId: Int
)
