package com.example.fliplearn_final.domain.model

import java.time.Instant

data class Dictionary(
    val dictionaryId: Int = 0,
    val folderId: Int? = null,
    val userId: Int,
    val title: String,
    val description: String? = null,
    val isPublic: Boolean = false,
    val fromLang: String,
    val toLang: String,
    val label: String = "A1",
    val termsCount: Int = 0,
    val userName: String,
    val createdAt: Instant = Instant.now()
)
