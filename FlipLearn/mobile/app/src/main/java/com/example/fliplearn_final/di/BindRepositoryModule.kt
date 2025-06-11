package com.example.fliplearn_final.di

import com.example.fliplearn_final.data.repository.AIRepositoryImpl
import com.example.fliplearn_final.domain.repository.ai.AIRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class BindRepositoryModule {

    @Binds
    abstract fun bindAIRepository(
        impl: AIRepositoryImpl
    ): AIRepository
}