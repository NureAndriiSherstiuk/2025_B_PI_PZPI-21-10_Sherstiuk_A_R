package com.example.fliplearn_final.presentation.pages.test_2

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.example.fliplearn_final.R
import com.example.fliplearn_final.presentation.ui.components.CustomLinearProgressIndicator
import com.example.fliplearn_final.presentation.ui.components.CustomText
import com.example.fliplearn_final.presentation.ui.theme.LocalAppColors
import com.example.fliplearn_final.presentation.ui.theme.LocalAppTypography


@Composable
fun Test2Screen (
    viewModel: Test2ViewModel = hiltViewModel(),
    onBack: () -> Unit,
    dictionaryId:Int,
    userId: Int,
    navController: NavHostController,
    ) {


    val uiState = viewModel.uiState



    LaunchedEffect(dictionaryId) {
        viewModel.loadCards(dictionaryId)
    }

    if (uiState.isLoading) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(color = LocalAppColors.current.primaryButtonColor)
        }
        return
    }

    if (uiState.cards.size < 4) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                CustomText(
                    text = "Для проходження цього тесту потрібно  4 картки у словнику.",
                    style = LocalAppTypography.current.bodyLarge,
                    color = LocalAppColors.current.primaryTextColor,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                OutlinedButton(
                    onClick = onBack,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(7.dp),
                    border = ButtonDefaults.outlinedButtonBorder.copy(
                        brush = SolidColor(LocalAppColors.current.hintTextColor)
                    )
                ) {
                    CustomText(
                        text = "Повернутися до словника",
                        color = LocalAppColors.current.headerTextColor,
                        style = LocalAppTypography.current.labelLarge
                    )
                }
            }
        }
        return
    }


    if (uiState.isFinished){
        Test2ResultScreen (
            userId = userId,
            dictionaryId = dictionaryId,
            viewModel = viewModel,
            onBack = onBack,
            navController = navController
        )
    } else {
        Scaffold(
            topBar = {
                TopBar(setName = "Multiple choice", onBack = onBack)
            }
        ) { padding ->
            Test2ScreenContent(
                modifier = Modifier.padding(padding),
                viewModel = viewModel
            )
        }
    }
}


@Composable
fun Test2ScreenContent(modifier: Modifier, viewModel: Test2ViewModel) {
    val uiState = viewModel.uiState
    val currentCard = uiState.cards.getOrNull(uiState.currentIndex)
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(15.dp)
    ) {

        CustomLinearProgressIndicator(
            progress = (uiState.currentIndex + 1).toFloat() / uiState.cards.size,
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
                    text = currentCard?.term ?: "—",
                    style = LocalAppTypography.current.headlineLarge,
                    modifier = Modifier.padding(bottom = 25.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(120.dp))

        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            CustomText(
                text = "Оберіть правильну відповідь:",
                style = LocalAppTypography.current.titleSmall,
                modifier = Modifier.padding(bottom = 35.dp)
            )

            currentCard?.options?.forEachIndexed { index, option ->
                AnswerCard(
                    option = AnswerOption(index + 1, option),
                    onClick = {
                        if (!uiState.isAnswerSelected) {
                            viewModel.onEvent(Test2Event.SelectAnswer(option))
                        }
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
                onClick = {
                    if (!uiState.isAnswerSelected) {
                        viewModel.onEvent(Test2Event.Skip)
                    }
                }
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
                overflow = TextOverflow.Clip,
                maxLines = 1
            )
        }
    }
}