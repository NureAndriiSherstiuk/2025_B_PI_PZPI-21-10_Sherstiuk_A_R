package com.example.fliplearn_final.presentation.pages.test_2

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.fliplearn_final.presentation.pages.test_result.TestResultScreen


@Composable
fun Test2ResultScreen(
    viewModel: Test2ViewModel = hiltViewModel(),
    onBack: () -> Unit,
    dictionaryId:Int,
    userId:Int,
    navController: NavController
) {
    val state = viewModel.uiState

    LaunchedEffect(Unit) {
        viewModel.saveResult(userId, dictionaryId)
    }

    TestResultScreen(
        correct = state.correctAnswers,
        total = state.cards.size,
        setName = "Multiple choice",
        onRestart = { viewModel.onEvent(Test2Event.Restart) },
        onReview = {
            navController.popBackStack()
        },
        onBack = onBack
    )
}


