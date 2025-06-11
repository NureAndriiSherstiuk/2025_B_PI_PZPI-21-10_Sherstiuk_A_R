package com.example.fliplearn_final.presentation.pages.test_3

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import androidx.compose.runtime.State
import androidx.lifecycle.viewModelScope
import com.example.fliplearn_final.domain.model.TestResult
import com.example.fliplearn_final.domain.usecase.dictionary.GetDictionaryWithCardsUseCase
import com.example.fliplearn_final.domain.usecase.test.EvaluateInputTranslationUseCase
import com.example.fliplearn_final.domain.usecase.test.SaveTestResultUseCase
import kotlinx.coroutines.launch
import java.time.Instant

@HiltViewModel
class Test3ViewModel @Inject constructor(
    private val getDictionaryWithCardsUseCase: GetDictionaryWithCardsUseCase,
    private val evaluateInputTranslationUseCase: EvaluateInputTranslationUseCase,
    private val saveTestResultUseCase: SaveTestResultUseCase
) : ViewModel() {

    private val _uiState = mutableStateOf(Test3UiState())
    val uiState: State<Test3UiState> = _uiState
    var hasSavedResult = false


    fun loadCards(dictionaryId: Int) {
        viewModelScope.launch {
            val result = getDictionaryWithCardsUseCase(dictionaryId)
            result?.cards?.let { cards ->
                if (cards.isNotEmpty()) {
                    val firstCard = cards.first()
                    _uiState.value = _uiState.value.copy(
                        cards = cards,
                        totalTerms = cards.size,
                        currentTerm = firstCard.term,
                        correctTranslation = firstCard.translation
                    )
                }
            }
        }
    }
    fun saveResult(userId: Int, dictionaryId: Int) {
        if (hasSavedResult) return
        hasSavedResult = true

        val correctAnswers = uiState.value.correctAnswersCount
        val totalQuestions = uiState.value.cards.size
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


    fun onEvent(event: Test3Event) {
        when (event) {
            is Test3Event.EnteredTranslation -> {
                _uiState.value = _uiState.value.copy(userInput = event.value)
            }
            Test3Event.SubmitAnswer -> {
                checkAnswerAndMoveNext()
            }
            Test3Event.SkipAnswer -> {
                moveToNextCard()
            }
            Test3Event.RestartTest -> {
                resetTest()
            }
            Test3Event.FinishTest -> {
                _uiState.value = _uiState.value.copy(isTestFinished = true)
            }
        }
    }

    private fun checkAnswerAndMoveNext() {
        val userAnswer = _uiState.value.userInput.trim()
        val correctTerm = _uiState.value.currentTerm.trim()

        viewModelScope.launch {
            val isCorrect = evaluateInputTranslationUseCase(correctTerm, userAnswer)
            if (isCorrect) {
                _uiState.value = _uiState.value.copy(
                    correctAnswersCount = _uiState.value.correctAnswersCount + 1
                )
            }
            moveToNextCard()
        }
    }

    private fun moveToNextCard() {
        val nextIndex = _uiState.value.currentIndex + 1
        if (nextIndex < _uiState.value.cards.size) {
            val nextCard = _uiState.value.cards[nextIndex]
            _uiState.value = _uiState.value.copy(
                currentIndex = nextIndex,
                currentTerm = nextCard.term,
                correctTranslation = nextCard.translation,
                userInput = ""
            )
        } else {
            _uiState.value = _uiState.value.copy(
                isTestFinished = true
            )
        }
    }

    private fun resetTest() {
        val shuffled = _uiState.value.cards.shuffled()
        val first = shuffled.firstOrNull()
        _uiState.value = Test3UiState(
            cards = shuffled,
            currentIndex = 0,
            currentTerm = first?.term.orEmpty(),
            correctTranslation = first?.translation.orEmpty(),
            totalTerms = shuffled.size
        )
    }
}
