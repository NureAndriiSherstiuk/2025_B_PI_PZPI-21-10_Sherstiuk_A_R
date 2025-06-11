package com.example.fliplearn_final.presentation.pages.test_3

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.example.fliplearn_final.R
import com.example.fliplearn_final.presentation.ui.components.CustomButton
import com.example.fliplearn_final.presentation.ui.components.CustomLinearProgressIndicator
import com.example.fliplearn_final.presentation.ui.components.CustomText
import com.example.fliplearn_final.presentation.ui.theme.LocalAppColors
import com.example.fliplearn_final.presentation.ui.theme.LocalAppTypography


@Composable
fun Test3Screen(
    viewModel: Test3ViewModel = hiltViewModel(),
    onBack: () -> Unit,
    dictionaryId:Int,
    userId:Int,
    navController: NavHostController,

    ) {

    LaunchedEffect(dictionaryId) {
        viewModel.loadCards(dictionaryId)
    }

    val uiState = viewModel.uiState.value

    if (uiState.isTestFinished) {
        Test3ResultScreen(
            userId = userId,
            dictionaryId = dictionaryId,
            viewModel = viewModel,
            onBack = onBack,
            navController = navController
        )
    } else {
        Scaffold(
            topBar = {
                TopBar(setName = "Input test", onBack = onBack)
            }
        ) { padding ->
            Test3ScreenContent(
                modifier = Modifier.padding(padding),
                viewModel = viewModel
            )
        }
    }
}

@Composable
fun Test3ScreenContent(modifier: Modifier, viewModel: Test3ViewModel) {
    val uiState = viewModel.uiState.value

    if (uiState.isTestFinished) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CustomText(
                text = "Тест завершено! Правильних відповідей: ${uiState.correctAnswersCount}/${uiState.totalTerms}",
                style = LocalAppTypography.current.headlineMedium
            )
        }
        return
    }

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
                    text = uiState.currentTerm,
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
                text = "Напишіть правильну відповідь:",
                style = LocalAppTypography.current.titleSmall,
                modifier = Modifier.padding(bottom = 35.dp)
            )

            Test3OutlinedTextField(
                value = uiState.userInput,
                onValueChange = {
                    viewModel.onEvent(Test3Event.EnteredTranslation(it))
                },
                placeholderText = "Введіть переклад слова",
                isIcon = false,
                isPasswordVisible = false
            )

            Spacer(modifier = Modifier.height(10.dp))

            CustomButton(
                text = "Продовжити",
                onClick = {
                    viewModel.onEvent(Test3Event.SubmitAnswer)
                }
            )

            Spacer(modifier = Modifier.height(12.dp))
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 25.dp),
            contentAlignment = Alignment.Center
        ) {
            TextButton(
                onClick = {
                    viewModel.onEvent(Test3Event.SkipAnswer)
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


@Composable
private fun Test3OutlinedTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholderText: String,
    isIcon: Boolean = false,
    isPasswordVisible: Boolean
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = {
            CustomText(
                text = placeholderText,
                style = LocalAppTypography.current.labelMedium,
                color = LocalAppColors.current.hintTextColor
            )
        },
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(7.dp),
        colors = OutlinedTextFieldDefaults.colors(
            unfocusedBorderColor = LocalAppColors.current.surfaceVariant,
            unfocusedTextColor = LocalAppColors.current.headerTextColor,
            unfocusedPlaceholderColor = LocalAppColors.current.hintTextColor,
            focusedBorderColor = LocalAppColors.current.actionTextColor,
            focusedTextColor = LocalAppColors.current.headerTextColor,
        ),
        visualTransformation = if (isIcon && !isPasswordVisible)
            PasswordVisualTransformation()
        else
            VisualTransformation.None,
        keyboardOptions = if (isIcon)
            KeyboardOptions(keyboardType = KeyboardType.Password)
        else
            KeyboardOptions.Default,
        singleLine = true,
    )
}
