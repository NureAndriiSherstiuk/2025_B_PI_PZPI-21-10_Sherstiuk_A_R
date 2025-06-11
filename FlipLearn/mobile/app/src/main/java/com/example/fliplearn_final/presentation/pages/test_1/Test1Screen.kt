package com.example.fliplearn_final.presentation.pages.test_1

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.example.fliplearn_final.R
import com.example.fliplearn_final.presentation.ui.components.CustomLinearProgressIndicator
import com.example.fliplearn_final.presentation.ui.components.CustomText
import com.example.fliplearn_final.presentation.ui.theme.LocalAppColors
import com.example.fliplearn_final.presentation.ui.theme.LocalAppTypography


@Composable
fun Test1Screen(
    viewModel: Test1ViewModel = hiltViewModel(),
    onBack: () -> Unit,
    dictionaryId:Int,
    userId: Int,
    navController: NavHostController,
    ) {
    val state = viewModel.uiState

    LaunchedEffect(dictionaryId) {
        viewModel.loadCards(dictionaryId)
    }

    if (state.isFinished) {
        Test1ResultScreen(userId = userId ,  viewModel = viewModel, onBack = onBack , dictionaryId = dictionaryId , navController = navController)
    } else {
        Scaffold(
            topBar = {
                TopBar(setName = "True or False", onBack = onBack)
            }
        ) { padding ->
            Test1ScreenContent(
                modifier = Modifier.padding(padding),
                viewModel = viewModel
            )
        }
    }
}

@Composable
private fun Test1ScreenContent(modifier: Modifier, viewModel: Test1ViewModel) {
    val uiState = viewModel.uiState

    val answerOptions = listOf(
        AnswerOption(1, "Правда"),
        AnswerOption(2, "Неправда")
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(15.dp)
    ) {
        CustomLinearProgressIndicator(
            progress = uiState.progress,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 20.dp)
        )
        Spacer(modifier = Modifier.height(100.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 25.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 35.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                CustomText(
                    text = uiState.currentCard?.term.orEmpty(),
                    style = LocalAppTypography.current.headlineLarge,
                    modifier = Modifier.padding(bottom = 25.dp)
                )
                CustomText(
                    text = uiState.currentCard?.translation.orEmpty(),
                    style = LocalAppTypography.current.headlineLarge,
                )
            }
        }
        Spacer(modifier = Modifier.height(120.dp))
        Column(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            CustomText(
                text = "Оберіть правильну відповідь:",
                style = LocalAppTypography.current.titleSmall,
                modifier = Modifier.padding(bottom = 35.dp)
            )
            answerOptions.forEach { option ->
                AnswerCard(
                    option = option,
                    onClick = {
                        val answer = option.id == 1
                        viewModel.onEvent(Test1Event.SubmitAnswer(answer))
                    }
                )
                Spacer(modifier = Modifier.height(12.dp))
            }
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 25.dp),
            contentAlignment = Alignment.Center
        ) {
            TextButton(
                onClick = { viewModel.onEvent(Test1Event.Skip) }
            ) {
                CustomText(
                    text = "Пропустити",
                    style = LocalAppTypography.current.bodyLarge
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TopBar(setName: String, onBack: () -> Unit) {
    TopAppBar(
        title = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(end = 24.dp),
                contentAlignment = Alignment.Center
            ) {
                CustomText(
                    text = setName,
                    style = LocalAppTypography.current.headlineMedium,
                    color = LocalAppColors.current.headerTextColor
                )
            }
        },
        navigationIcon = {
            IconButton(onClick = onBack) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_arrow_back),
                    contentDescription = "Back",
                    modifier = Modifier.size(20.dp)
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = LocalAppColors.current.primaryBackground
        )
    )
}


data class AnswerOption(
    val id: Int,
    val text: String
)

@Composable
fun AnswerCard(
    option: AnswerOption,
    onClick: (AnswerOption) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .border(
                width = 1.5.dp,
                color = LocalAppColors.current.borderColor,
                shape = RoundedCornerShape(7.dp)
            )
            .background(
                color = Color(0xB2FCFCFC),
                shape = RoundedCornerShape(7.dp)
            )
            .clickable { onClick(option) }
            .height(55.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 12.dp)
        ) {

            Box(
                modifier = Modifier
                    .size(25.dp)
                    .background(LocalAppColors.current.surfaceVariant, shape = CircleShape),
                contentAlignment = Alignment.Center
            ) {
                CustomText(
                    text = option.id.toString(),
                    color = LocalAppColors.current.primaryTextColor,
                )
            }

            Spacer(modifier = Modifier.width(10.dp))


            CustomText(
                text = option.text,
                style = LocalAppTypography.current.bodyLarge,
                color = LocalAppColors.current.headerTextColor,
            )
        }
    }
}