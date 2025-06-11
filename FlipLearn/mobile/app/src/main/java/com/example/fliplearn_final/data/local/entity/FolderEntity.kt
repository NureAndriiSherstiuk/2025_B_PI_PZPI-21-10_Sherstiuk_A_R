package com.example.fliplearn_final.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

import java.time.Instant

@Entity(
    tableName = "folder",
    foreignKeys = [
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["user_id"],
            childColumns = ["user_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("user_id")]
)
data class FolderEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "folder_id")
    val folderId: Int = 0,

    @ColumnInfo(name = "user_id")
    val userId: Int,

    @ColumnInfo(name = "name")
    val name: String,

    @ColumnInfo(name = "description")
    val description: String? = null,

    @ColumnInfo(name = "created_at")
    val createdAt: Instant = Instant.now()
)

data class FolderWithStats(
    val folderId: Int,
    val userId: Int,
    val name: String,
    val description: String? = null,
    val dictionariesCount: Int? = null,
    val termsCount: Int? = null,
    val label: String? = "A1",
    val userName: String? = null,
    val createdAt: Instant = Instant.now()
)
