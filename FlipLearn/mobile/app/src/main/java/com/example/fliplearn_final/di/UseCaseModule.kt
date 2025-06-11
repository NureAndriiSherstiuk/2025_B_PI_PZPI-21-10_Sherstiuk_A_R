package com.example.fliplearn_final.di

import android.content.Context
import com.example.fliplearn_final.data.local.datastore.UserPreferences
import com.example.fliplearn_final.data.remote.auth.GoogleAuthClient
import com.example.fliplearn_final.domain.repository.card.CardRepository
import com.example.fliplearn_final.domain.repository.dictionary.DictionaryRepository
import com.example.fliplearn_final.domain.repository.folder.FolderRepository
import com.example.fliplearn_final.domain.repository.test.TestResultRepository
import com.example.fliplearn_final.domain.repository.user.UserRepository
import com.example.fliplearn_final.domain.usecase.dictionary.CreateDictionaryWithCardsUseCase
import com.example.fliplearn_final.domain.usecase.dictionary.GetAllDictionariesUseCase
import com.example.fliplearn_final.domain.usecase.dictionary.GetAvailableDictionariesUseCase
import com.example.fliplearn_final.domain.usecase.dictionary.GetDictionariesByFolderUseCase
import com.example.fliplearn_final.domain.usecase.dictionary.GetDictionaryWithCardsUseCase
import com.example.fliplearn_final.domain.usecase.folder.CreateFolderUseCase
import com.example.fliplearn_final.domain.usecase.folder.DeleteFolderUseCase
import com.example.fliplearn_final.domain.usecase.folder.GetFoldersWithStatsUseCase
import com.example.fliplearn_final.domain.usecase.test.GetTestResultsForUserUseCase
import com.example.fliplearn_final.domain.usecase.test.SaveTestResultUseCase
import com.example.fliplearn_final.domain.usecase.user.GetUserProfileInfoUseCase
import com.example.fliplearn_final.domain.usecase.user.SaveUserProfileChangesUseCase
import com.example.fliplearn_final.domain.usecase.user.SignInUserUseCase
import com.example.fliplearn_final.domain.usecase.user.SignOutUserUseCase
import com.example.fliplearn_final.domain.usecase.user.SignUpUserUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object UseCaseModule {

    @Provides
    fun provideCreateDictionaryWithCardsUseCase(
        dictionaryRepository: DictionaryRepository,
        cardRepository: CardRepository
    ): CreateDictionaryWithCardsUseCase {
        return CreateDictionaryWithCardsUseCase(dictionaryRepository, cardRepository)
    }

    @Provides
    fun provideCreateFolderUseCase(
        repository: FolderRepository
    ): CreateFolderUseCase = CreateFolderUseCase(repository)

    @Provides
    fun provideSignUpUserUseCase(
        repository: UserRepository,
        userPreferences: UserPreferences
    ): SignUpUserUseCase = SignUpUserUseCase(repository, userPreferences)

    @Provides
    fun provideGoogleAuthClient(@ApplicationContext context: Context): GoogleAuthClient {
        return GoogleAuthClient(context)
    }

    @Provides
    fun provideUserPreferences(@ApplicationContext context: Context): UserPreferences {
        return UserPreferences(context)
    }

    @Provides
    fun provideSignInUserUseCase(
        repository: UserRepository,
        userPreferences: UserPreferences
    ): SignInUserUseCase = SignInUserUseCase(repository, userPreferences)

    @Provides
    fun provideSignOutUserUseCase(
        userPreferences: UserPreferences
    ): SignOutUserUseCase = SignOutUserUseCase(userPreferences)

    @Provides
    fun provideGetUserProfileInfoUseCase(
        repository: UserRepository
    ): GetUserProfileInfoUseCase = GetUserProfileInfoUseCase(repository)

    @Provides
    fun provideSaveUserProfileChangesUseCase(
        repository: UserRepository
    ): SaveUserProfileChangesUseCase = SaveUserProfileChangesUseCase(repository)

    @Provides
    fun provideGetAllDictionariesUseCase(
        repository: DictionaryRepository
    ): GetAllDictionariesUseCase = GetAllDictionariesUseCase(repository)

    @Provides
    fun provideGetDictionaryWithCardsUseCase(
        dictionaryRepository: DictionaryRepository,
        cardRepository: CardRepository
    ): GetDictionaryWithCardsUseCase {
        return GetDictionaryWithCardsUseCase(dictionaryRepository, cardRepository)
    }

    @Provides
    fun provideSaveTestResultUseCase(repository: TestResultRepository): SaveTestResultUseCase {
        return SaveTestResultUseCase(repository)
    }

    @Provides
    fun provideGetTestResultsForUserUseCase(repository: TestResultRepository): GetTestResultsForUserUseCase {
        return GetTestResultsForUserUseCase(repository)
    }

    @Provides
    fun provideGetFoldersWithStatsUseCase(
        repository: FolderRepository
    ): GetFoldersWithStatsUseCase = GetFoldersWithStatsUseCase(repository)

    @Provides
    fun provideGetAvailableDictionariesUseCase(
        dictionaryRepository: DictionaryRepository
    ): GetAvailableDictionariesUseCase {
        return GetAvailableDictionariesUseCase(dictionaryRepository)
    }

    @Provides
    fun provideGetDictionariesByFolderUseCase(
        dictionaryRepository: DictionaryRepository
    ): GetDictionariesByFolderUseCase {
        return GetDictionariesByFolderUseCase(dictionaryRepository)
    }

    @Provides
    fun provideDeleteFolderUseCase(folderRepository: FolderRepository): DeleteFolderUseCase {
        return DeleteFolderUseCase(folderRepository)
    }


}