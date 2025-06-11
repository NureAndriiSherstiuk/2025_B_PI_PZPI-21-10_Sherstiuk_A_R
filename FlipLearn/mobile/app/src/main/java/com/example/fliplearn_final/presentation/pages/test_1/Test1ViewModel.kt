package com.example.fliplearn_final.presentation.pages.test_1

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fliplearn_final.domain.usecase.dictionary.GetDictionaryWithCardsUseCase
import com.example.fliplearn_final.domain.usecase.test.EvaluateTranslationUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.example.fliplearn_final.domain.model.TestResult
import com.example.fliplearn_final.domain.usecase.test.SaveTestResultUseCase
import java.time.Instant

@HiltViewModel
class Test1ViewModel @Inject constructor(
    private val getDictionaryWithCardsUseCase: GetDictionaryWithCardsUseCase,
    private val evaluateTranslationUseCase: EvaluateTranslationUseCase,
    private val saveTestResultUseCase: SaveTestResultUseCase
) : ViewModel() {

    var uiState by mutableStateOf(Test1UiState())
        private set

    private val TAG = "Test1ViewModel"
    var hasSavedResult = false

    fun loadCards(dictionaryId: Int) {
        viewModelScope.launch {
            try {
                val result = getDictionaryWithCardsUseCase(dictionaryId)
                result?.let {
                    val originalCards = it.cards.shuffled()
                    val totalCards = originalCards.size
                    val half = totalCards / 2

                    val correctCards = originalCards.take(half)
                    val incorrectCardsSource = originalCards.drop(half)

                    val translationsPool = originalCards.map { it.translation }.shuffled()

                    val incorrectCards = incorrectCardsSource.map { card ->
                        val wrongTranslation = translationsPool.firstOrNull { it != card.translation } ?: card.translation
                        card.copy(translation = wrongTranslation)
                    }

                    val randomizedCards = (correctCards + incorrectCards).shuffled()

                    uiState = uiState.copy(
                        cards = randomizedCards,
                        currentIndex = 0,
                        correctAnswers = 0,
                        isFinished = false
                    )
                } ?: Log.e(TAG, "No result returned from use case")
            } catch (e: Exception) {
                Log.e(TAG, "Error loading cards: ${e.localizedMessage}", e)
            }
        }
    }

    fun saveResult(userId: Int, dictionaryId: Int) {
        if (hasSavedResult) return
        hasSavedResult = true

        val correctAnswers = uiState.correctAnswers
        val totalQuestions = uiState.cards.size
        val percentScore = if (totalQuestions > 0) {
            (correctAnswers * 100f) / totalQuestions
        } else 0f

        val result = TestResult(
            id = 0,
            userId = userId,
            dictionaryId = dictionaryId,
            correctAnswers = correctAnswers,
            totalQuestions = totalQuestions,
            percentScore = percentScore,
            completedAt = Instant.now()
        )

        viewModelScope.launch {
            saveTestResultUseCase(result)
        }
    }


    fun onEvent(event: Test1Event) {
        when (event) {
            is Test1Event.SubmitAnswer -> {
                val card = uiState.currentCard ?: run {
                    Log.e(TAG, "Current card is null!")
                    return
                }

                viewModelScope.launch {
                    try {
                        val isCorrect = evaluateTranslationUseCase(card.term, card.translation)

                        val userCorrect = isCorrect == event.isTrueSelected
                        val newCorrect = if (userCorrect) uiState.correctAnswers + 1 else uiState.correctAnswers
                        val nextIndex = uiState.currentIndex + 1

                        uiState = if (nextIndex >= uiState.cards.size) {

                            uiState.copy(
                                currentIndex = nextIndex,
                                correctAnswers = newCorrect,
                                isFinished = true
                            )
                        } else {
                            uiState.copy(
                                currentIndex = nextIndex,
                                correctAnswers = newCorrect
                            )
                        }
                    } catch (e: Exception) {
                        Log.e(TAG, "Error during AI evaluation: ${e.localizedMessage}", e)
                    }
                }
            }

            Test1Event.Skip -> {
                val nextIndex = uiState.currentIndex + 1
                Log.d(TAG, "User skipped card. Moving to index: $nextIndex")
                uiState = if (nextIndex >= uiState.cards.size) {
                    Log.d(TAG, "Test finished after skip.")
                    uiState.copy(currentIndex = nextIndex, isFinished = true)
                } else {
                    uiState.copy(currentIndex = nextIndex)
                }
            }

            Test1Event.Restart -> {
                Log.d(TAG, "Test restarted.")
                uiState = uiState.copy(
                    currentIndex = 0,
                    correctAnswers = 0,
                    isFinished = false
                )
            }
        }
    }
}
