package com.example.fliplearn_final.domain.model

import java.time.Instant

data class Folder(
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


data class FolderWithDictionaries(
    val folderId: Int,
    val name: String,
    val description: String?,
    val createdAt: Instant,
    val dictionaries: List<Dictionary>
)
