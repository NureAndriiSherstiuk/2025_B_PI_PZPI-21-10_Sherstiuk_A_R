package com.example.fliplearn_final.presentation.pages.test_3

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.fliplearn_final.presentation.pages.test_result.TestResultScreen


@Composable
fun Test3ResultScreen(
    viewModel: Test3ViewModel = hiltViewModel(),
    onBack: () -> Unit,
    userId:Int,
    dictionaryId:Int,
    navController: NavController
) {
    val state = viewModel.uiState.value

    LaunchedEffect(Unit) {
        viewModel.saveResult(userId, dictionaryId)
    }

    TestResultScreen(
        correct = state.correctAnswersCount,
        total = state.cards.size,
        setName = "Input test",
        onRestart = {
            viewModel.onEvent(Test3Event.RestartTest)
        },

        onReview = {
            navController.popBackStack()
        },
        onBack = onBack
    )
}
