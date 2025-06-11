package com.example.fliplearn_final.domain.repository.google

interface GoogleAuthRepository {
    suspend fun signIn(): Boolean
    suspend fun signOut()
    fun isSignedIn(): Boolean
}