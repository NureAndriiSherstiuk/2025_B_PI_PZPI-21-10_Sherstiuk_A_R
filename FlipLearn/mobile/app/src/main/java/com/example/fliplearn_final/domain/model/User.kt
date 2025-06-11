package com.example.fliplearn_final.domain.model
import java.time.Instant

data class User(
    val id: Int = 0,
    val email: String,
    val username: String,
    val password: String,
    val createdAt: Instant = Instant.now()
)