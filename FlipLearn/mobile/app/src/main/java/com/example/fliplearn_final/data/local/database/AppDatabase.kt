package com.example.fliplearn_final.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.fliplearn_final.data.local.dao.CardDao
import com.example.fliplearn_final.data.local.dao.DictionaryDao
import com.example.fliplearn_final.data.local.dao.FolderDao
import com.example.fliplearn_final.data.local.dao.TestResultDao
import com.example.fliplearn_final.data.local.dao.UserDao
import com.example.fliplearn_final.data.local.entity.CardEntity
import com.example.fliplearn_final.data.local.entity.DictionaryEntity
import com.example.fliplearn_final.data.local.entity.FolderDictionaryCrossRef
import com.example.fliplearn_final.data.local.entity.UserEntity
import com.example.fliplearn_final.data.local.entity.FolderEntity
import com.example.fliplearn_final.data.local.entity.TestResultEntity

@Database(entities = [UserEntity::class ,
    FolderEntity::class ,
    DictionaryEntity::class ,
    CardEntity::class ,
    TestResultEntity::class,
    FolderDictionaryCrossRef::class
    ], version = 12)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun folderDao(): FolderDao
    abstract fun  dictionaryDao(): DictionaryDao
    abstract fun  cardDao(): CardDao
    abstract fun testResultDao(): TestResultDao
}