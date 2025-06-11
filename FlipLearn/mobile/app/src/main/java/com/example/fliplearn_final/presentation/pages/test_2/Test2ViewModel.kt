package com.example.fliplearn_final.presentation.pages.test_2

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fliplearn_final.domain.model.TestResult
import com.example.fliplearn_final.domain.usecase.dictionary.GetDictionaryWithCardsUseCase
import com.example.fliplearn_final.domain.usecase.test.GetMeaningForTermUseCase
import com.example.fliplearn_final.domain.usecase.test.SaveTestResultUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.Instant
import javax.inject.Inject

@HiltViewModel
class Test2ViewModel @Inject constructor(
    private val getDictionaryWithCardsUseCase: GetDictionaryWithCardsUseCase,
    private val getMeaningForTermUseCase: GetMeaningForTermUseCase,
    private val saveTestResultUseCase: SaveTestResultUseCase
) : ViewModel() {

    private val _uiState = mutableStateOf(Test2UiState())
    val uiState: Test2UiState get() = _uiState.value
    var hasSavedResult = false

    fun loadCards(dictionaryId: Int) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)

            val dictionaryWithCards = getDictionaryWithCardsUseCase(dictionaryId)
            val allCards = dictionaryWithCards?.cards?.shuffled().orEmpty()

            val generatedCards = mutableListOf<Card>()

            for (currentCard in allCards) {
                val term = currentCard.term

                val correctMeaning = try {
                    getMeaningForTermUseCase(term).also {
                    }
                } catch (e: Exception) {
                    null
                }

                if (correctMeaning.isNullOrBlank()) {
                    continue
                }

                val distractors = allCards
                    .filter { it.term != term }
                    .map { it.meaning }
                    .distinct()
                    .filter { it != correctMeaning }
                    .shuffled()
                    .take(3)

                if (distractors.size < 3) {

                    val fallbackDistractors = listOf(
                        "An expression of gratitude",
                        "A greeting used to acknowledge someone's presence",
                        "A domesticated animal"
                    )

                    val needed = 3 - distractors.size
                    val additional = fallbackDistractors
                        .filter { it != correctMeaning && !distractors.contains(it) }
                        .take(needed)

                    val options = (distractors + additional + correctMeaning).shuffled()

                    generatedCards.add(
                        Card(
                            term = term,
                            correctOption = correctMeaning,
                            options = options
                        )
                    )
                } else {
                    val options = (distractors + correctMeaning).shuffled()

                    generatedCards.add(
                        Card(
                            term = term,
                            correctOption = correctMeaning,
                            options = options
                        )
                    )
                }

                delay(500)
            }

            _uiState.value = Test2UiState(
                cards = generatedCards,
                isLoading = false
            )
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

    fun onEvent(event: Test2Event) {
        when (event) {
            is Test2Event.SelectAnswer -> {
                if (uiState.isAnswerSelected) return

                val currentCard = uiState.cards[uiState.currentIndex]
                val isCorrect = event.selectedOption == currentCard.correctOption
                Log.d("Test2ViewModel", "Answer selected: '${event.selectedOption}', " +
                        "Correct: '${currentCard.correctOption}', IsCorrect: $isCorrect")

                _uiState.value = uiState.copy(
                    correctAnswers = if (isCorrect) uiState.correctAnswers + 1 else uiState.correctAnswers,
                    isAnswerSelected = true
                )

                viewModelScope.launch {
                    delay(200)
                    goToNextCard()
                }
            }

            is Test2Event.Skip -> {
                if (uiState.isAnswerSelected) return
                Log.d("Test2ViewModel", "User skipped card at index ${uiState.currentIndex}")
                goToNextCard()
            }

            is Test2Event.Restart -> {
                Log.d("Test2ViewModel", "Restart event received (not implemented)")
            }
        }
    }

    private fun goToNextCard() {
        val nextIndex = uiState.currentIndex + 1
        if (nextIndex < uiState.cards.size) {
            _uiState.value = uiState.copy(
                currentIndex = nextIndex,
                isAnswerSelected = false
            )
        } else {
            _uiState.value = uiState.copy(isFinished = true)
        }
    }
}
