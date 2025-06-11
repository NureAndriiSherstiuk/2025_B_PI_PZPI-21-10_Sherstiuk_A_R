package com.example.fliplearn_final.di

import android.content.Context
import androidx.room.Room
import com.example.fliplearn_final.data.local.dao.CardDao
import com.example.fliplearn_final.data.local.dao.DictionaryDao
import com.example.fliplearn_final.data.local.dao.FolderDao
import com.example.fliplearn_final.data.local.dao.TestResultDao
import com.example.fliplearn_final.data.local.database.AppDatabase
import com.example.fliplearn_final.data.local.dao.UserDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                "fliplearn_database"
            )
            .build()
    }

    @Provides
    fun provideUserDao(database: AppDatabase): UserDao = database.userDao()

    @Provides
    fun provideFolderDao(database: AppDatabase): FolderDao = database.folderDao()
    @Provides
    fun provideDictionaryDao(database: AppDatabase): DictionaryDao = database.dictionaryDao()

    @Provides
    fun  provideCardDao(database: AppDatabase): CardDao = database.cardDao()

    @Provides
    fun  provideTestResultDao(database: AppDatabase): TestResultDao = database.testResultDao()
}