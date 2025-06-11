package com.example.fliplearn_final.data.remote.retrofit.model

data class ChatResponse(
    val choices: List<Choice>
)

data class Choice(
    val message: Message
)