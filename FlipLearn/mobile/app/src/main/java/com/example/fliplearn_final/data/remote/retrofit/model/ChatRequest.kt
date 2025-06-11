package com.example.fliplearn_final.data.remote.retrofit.model

data class ChatRequest(
    val model: String,
    val messages: List<Message>
)