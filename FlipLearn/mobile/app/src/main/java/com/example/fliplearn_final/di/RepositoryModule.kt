package com.example.fliplearn_final.di

import com.example.fliplearn_final.data.local.dao.CardDao
import com.example.fliplearn_final.data.local.dao.DictionaryDao
import com.example.fliplearn_final.data.local.dao.FolderDao
import com.example.fliplearn_final.data.local.dao.TestResultDao
import com.example.fliplearn_final.data.local.dao.UserDao
import com.example.fliplearn_final.data.repository.CardRepositoryImpl
import com.example.fliplearn_final.data.repository.DictionaryRepositoryImpl
import com.example.fliplearn_final.data.repository.FolderRepositoryImpl
import com.example.fliplearn_final.data.repository.TestResultRepositoryImpl
import com.example.fliplearn_final.data.repository.UserRepositoryImpl
import com.example.fliplearn_final.domain.repository.card.CardRepository
import com.example.fliplearn_final.domain.repository.dictionary.DictionaryRepository
import com.example.fliplearn_final.domain.repository.folder.FolderRepository
import com.example.fliplearn_final.domain.repository.test.TestResultRepository
import com.example.fliplearn_final.domain.repository.user.UserRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun provideUserRepository(userDao: UserDao): UserRepository =
        UserRepositoryImpl(userDao)

    @Provides
    @Singleton
    fun provideDictionaryRepository(
        dictionaryDao: DictionaryDao,
    ): DictionaryRepository {
        return DictionaryRepositoryImpl(dictionaryDao)
    }

    @Provides
    @Singleton
    fun provideCardRepository(
        cardDao: CardDao
    ): CardRepository = CardRepositoryImpl(cardDao)

    @Provides
    @Singleton
    fun provideFolderRepository(folderDao: FolderDao): FolderRepository =
        FolderRepositoryImpl(folderDao)

    @Provides
    @Singleton
    fun provideTestResultRepository(
        testResultDao: TestResultDao
    ): TestResultRepository {
        return TestResultRepositoryImpl(testResultDao)
    }

}


