package com.example.fliplearn_final.presentation.pages.test_1


import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.fliplearn_final.presentation.pages.test_result.TestResultScreen

@Composable
fun Test1ResultScreen(
    viewModel: Test1ViewModel = hiltViewModel(),
    onBack: () -> Unit,
    userId:Int,
    dictionaryId:Int,
    navController: NavController
) {
    val state = viewModel.uiState

    LaunchedEffect(Unit) {
        viewModel.saveResult(userId, dictionaryId)
    }

    TestResultScreen(
        correct = state.correctAnswers,
        total = state.cards.size,
        setName = "True or False",
        onRestart = { viewModel.onEvent(Test1Event.Restart) },
        onReview = { navController.popBackStack() },
        onBack = onBack
    )
}
