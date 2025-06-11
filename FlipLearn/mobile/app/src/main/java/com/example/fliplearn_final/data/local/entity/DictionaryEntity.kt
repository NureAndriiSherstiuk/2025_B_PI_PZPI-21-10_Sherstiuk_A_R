package com.example.fliplearn_final.data.local.entity


import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ColumnInfo
import androidx.room.ForeignKey
import androidx.room.Index
import java.time.Instant

@Entity(
    tableName = "dictionary",
    foreignKeys = [
        ForeignKey(
            entity = FolderEntity::class,
            parentColumns = ["folder_id"],
            childColumns = ["folder_id"],
            onDelete = ForeignKey.SET_NULL
        ),
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["user_id"],
            childColumns = ["user_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["folder_id"]),
        Index(value = ["user_id"])
    ]
)
data class DictionaryEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "dictionary_id")
    val dictionaryId: Int = 0,

    @ColumnInfo(name = "folder_id")
    val folderId: Int? = null,

    @ColumnInfo(name = "user_id")
    val userId: Int,

    @ColumnInfo(name = "user_name")
    val userName: String,

    @ColumnInfo(name = "title")
    val title: String,

    @ColumnInfo(name = "description")
    val description: String? = null,

    @ColumnInfo(name = "is_public")
    val isPublic: Boolean = false,

    @ColumnInfo(name = "from_lang")
    val fromLang: String,

    @ColumnInfo(name = "to_lang")
    val toLang: String,

    @ColumnInfo(name = "label")
    val label: String = "A1",

    @ColumnInfo(name = "terms_count")
    val termsCount: Int = 0,

    @ColumnInfo(name = "created_at")
    val createdAt: Instant = Instant.now())
