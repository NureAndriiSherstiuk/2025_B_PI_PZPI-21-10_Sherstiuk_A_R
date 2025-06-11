package com.example.fliplearn_final.presentation.pages.dictionary.create_dictionary

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fliplearn_final.data.local.datastore.UserPreferences
import com.example.fliplearn_final.domain.model.Card
import com.example.fliplearn_final.domain.model.Dictionary
import com.example.fliplearn_final.domain.usecase.dictionary.CreateDictionaryWithCardsUseCase
import com.example.fliplearn_final.domain.usecase.user.GetUserProfileInfoUseCase
import com.example.fliplearn_final.util.UiEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

@HiltViewModel
class CreateDictionaryViewModel @Inject constructor(
    private val createDictionaryWithCardsUseCase: CreateDictionaryWithCardsUseCase,
    private val getUserProfileInfoUseCase: GetUserProfileInfoUseCase,
    private val userPreferences: UserPreferences,
) : ViewModel() {

    private val _uiState = mutableStateOf(CreateDictionaryUiState())
    val uiState: State<CreateDictionaryUiState> = _uiState

    private val _uiEvent = Channel<UiEvent>()

    private fun resetForm() {
        _uiState.value = CreateDictionaryUiState(
            title = "",
            description = "",
            fromLang = "",
            toLang = "",
            isPublic = false,
            label = "A1",
            cards = listOf(CardInput())
        )
    }


    fun onEvent(event: CreateDictionaryEvent) {
        when (event) {
            is CreateDictionaryEvent.EnteredTitle -> {
                _uiState.value = _uiState.value.copy(title = event.value)
            }

            is CreateDictionaryEvent.EnteredDescription -> {
                _uiState.value = _uiState.value.copy(description = event.value)
            }

            is CreateDictionaryEvent.SelectedFromLang -> {
                _uiState.value = _uiState.value.copy(fromLang = event.lang)
            }

            is CreateDictionaryEvent.SelectedToLang -> {
                _uiState.value = _uiState.value.copy(toLang = event.lang)
            }
            is CreateDictionaryEvent.SelectedToLabel -> {
                _uiState.value = _uiState.value.copy(label = event.label)
            }

            is CreateDictionaryEvent.ChangedAccess -> {
                _uiState.value = _uiState.value.copy(isPublic = event.isPublic)
            }

            is CreateDictionaryEvent.ChangedCardTerm -> {
                val updatedCards = _uiState.value.cards.toMutableList()
                updatedCards[event.index] = updatedCards[event.index].copy(term = event.term)
                _uiState.value = _uiState.value.copy(cards = updatedCards)
            }

            is CreateDictionaryEvent.ChangedCardMeaning -> {
                val updatedCards = _uiState.value.cards.toMutableList()
                updatedCards[event.index] = updatedCards[event.index].copy(meaning = event.meaning)
                _uiState.value = _uiState.value.copy(cards = updatedCards)
            }

            is CreateDictionaryEvent.ChangedCardTranslation -> {
                val updatedCards = _uiState.value.cards.toMutableList()
                updatedCards[event.index] = updatedCards[event.index].copy(translation = event.translation)
                _uiState.value = _uiState.value.copy(cards = updatedCards)
            }

            CreateDictionaryEvent.AddCard -> {
                _uiState.value = _uiState.value.copy(
                    cards = _uiState.value.cards + CardInput()
                )
            }

            CreateDictionaryEvent.CreateDictionary -> {
                viewModelScope.launch {
                    try {
                        val email = userPreferences.userEmail.firstOrNull()

                        if (email == null) {
                            _uiEvent.send(UiEvent.ShowToast("Не вдалося отримати email"))
                            return@launch
                        }

                        val user = getUserProfileInfoUseCase(email)

                        val cards = _uiState.value.cards.map {
                            Card(
                                term = it.term,
                                meaning = it.meaning,
                                translation = it.translation
                            )
                        }

                        val termsCount = cards.count { it.term.isNotBlank() }

                        val dictionary = Dictionary(
                            title = _uiState.value.title,
                            description = _uiState.value.description,
                            fromLang = _uiState.value.fromLang,
                            toLang = _uiState.value.toLang,
                            isPublic = _uiState.value.isPublic,
                            label = _uiState.value.label,
                            termsCount = termsCount,
                            userName = user?.username ?: "username",
                            userId = user?.id ?: 1
                        )


                        createDictionaryWithCardsUseCase(dictionary, cards)
                        resetForm()
                        _uiEvent.send(UiEvent.ShowToast("Словник створено успішно"))


                    } catch (e: Exception) {
                        val errorMsg = "❗Помилка створення словника: ${e.localizedMessage}"
                        Log.e("CreateDictionary", errorMsg, e)
                        _uiEvent.send(UiEvent.ShowToast("Помилка: ${e.localizedMessage}"))
                    }
                }
            }

        }
    }
}
