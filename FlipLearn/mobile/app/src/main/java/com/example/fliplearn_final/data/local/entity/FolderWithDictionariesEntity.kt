package com.example.fliplearn_final.data.local.entity

import androidx.room.Embedded
import androidx.room.Relation

data class FolderWithDictionariesEntity(
    @Embedded val folder: FolderEntity,
    @Relation(
        parentColumn = "folder_id",
        entityColumn = "folder_id"
    )
    val dictionaries: List<DictionaryEntity>
)