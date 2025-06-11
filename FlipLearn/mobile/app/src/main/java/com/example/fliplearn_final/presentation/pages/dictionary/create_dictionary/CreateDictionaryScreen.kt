package com.example.fliplearn_final.presentation.pages.dictionary.create_dictionary

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.fliplearn_final.R
import com.example.fliplearn_final.presentation.ui.components.CustomText
import com.example.fliplearn_final.presentation.ui.components.CustomTextField
import com.example.fliplearn_final.presentation.ui.theme.LocalAppColors
import com.example.fliplearn_final.presentation.ui.theme.LocalAppTypography
import com.example.fliplearn_final.presentation.ui.widgets.AddCircleOutlined
import com.example.fliplearn_final.presentation.ui.widgets.Dropdown


@Composable
fun CreateDictionaryScreen(
    viewModel: CreateDictionaryViewModel = hiltViewModel(),
    onBack: () -> Unit
) {
    Scaffold(
        topBar = {
            TopBar(setName = "Створити словник", onBack = onBack , viewModel = viewModel)
        },
        content = {
            CreateDictionaryScreenContent(modifier = Modifier.padding(it), viewModel = viewModel)
        }
    )
}

@Composable
fun CreateDictionaryScreenContent(modifier: Modifier, viewModel: CreateDictionaryViewModel) {
    val state by viewModel.uiState
    val fromLangs = listOf("en", "ua")
    val toLangs = listOf("ua", "en")
    val accessOptions = listOf("Публічний", "Приватний")
    val labels = listOf("A1", "A2", "B1", "B2", "C1", "C2")

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(15.dp)
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            item {
                CustomTextField(
                    value = state.title,
                    onValueChange = { viewModel.onEvent(CreateDictionaryEvent.EnteredTitle(it)) },
                    placeholderText = "Назва словника"
                )
            }

            item {
                CustomTextField(
                    value = state.description,
                    onValueChange = { viewModel.onEvent(CreateDictionaryEvent.EnteredDescription(it)) },
                    placeholderText = "Опис словника"
                )
            }

            item {
                Spacer(modifier = Modifier.height(15.dp))
                Dropdown(
                    items = fromLangs,
                    textName = "Мова оригіналу",
                    selectedItem = state.fromLang,
                    modifier = Modifier.fillMaxWidth(),
                    fieldWidth = Dp.Unspecified,
                    onItemSelected = {
                        viewModel.onEvent(CreateDictionaryEvent.SelectedFromLang(it))
                    }
                )
            }

            item {
                Spacer(modifier = Modifier.height(15.dp))
                Dropdown(
                    items = toLangs,
                    textName = "Мова перекладу",
                    selectedItem = state.toLang,
                    modifier = Modifier.fillMaxWidth(),
                    fieldWidth = Dp.Unspecified,
                    onItemSelected = {
                        viewModel.onEvent(CreateDictionaryEvent.SelectedToLang(it))
                    }
                )
            }

            item {
                Spacer(modifier = Modifier.height(15.dp))
                Dropdown(
                    items = accessOptions,
                    textName = "Тип словника",
                    selectedItem = if (state.isPublic) "Публічний" else "Приватний",
                    modifier = Modifier.fillMaxWidth(),
                    fieldWidth = Dp.Unspecified,
                    onItemSelected = {
                        viewModel.onEvent(CreateDictionaryEvent.ChangedAccess(it == "Публічний"))
                    }
                )
            }

            item {
                Spacer(modifier = Modifier.height(15.dp))
                Dropdown(
                    items = labels,
                    textName = "Рівень англійської",
                    selectedItem = state.label,
                    modifier = Modifier.fillMaxWidth(),
                    fieldWidth = Dp.Unspecified,
                    onItemSelected = {
                        viewModel.onEvent(CreateDictionaryEvent.SelectedToLabel(it))
                    }
                )
            }

            items(state.cards.size) { index ->
                Spacer(modifier = Modifier.height(15.dp))
                val card = state.cards[index]
                CustomBox(
                    wordDefinition = card.term,
                    onWordDefinitionChange = {
                        viewModel.onEvent(CreateDictionaryEvent.ChangedCardTerm(index, it))
                    },
                    wordTranslation = card.translation,
                    onWordTranslationChange = {
                        viewModel.onEvent(CreateDictionaryEvent.ChangedCardTranslation(index, it))
                    },
                    wordMeaning = card.meaning,
                    onWordMeaningChange = {
                        viewModel.onEvent(CreateDictionaryEvent.ChangedCardMeaning(index, it))
                    }
                )

                Spacer(modifier = Modifier.height(10.dp))
            }
        }

        CardAdd(onClick = { viewModel.onEvent(CreateDictionaryEvent.AddCard) })
        Spacer(modifier = Modifier.height(55.dp))
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TopBar(setName: String, onBack: () -> Unit , viewModel: CreateDictionaryViewModel) {
    TopAppBar(
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                CustomText(
                    text = setName,
                    style = LocalAppTypography.current.displayMedium,
                    color = LocalAppColors.current.headerTextColor,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center
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
        actions = {
            IconButton(onClick = { viewModel.onEvent(CreateDictionaryEvent.CreateDictionary) }
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_submit),
                    contentDescription = "Confirmed dictionary creation",
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
fun CustomBox(
    wordDefinition: String,
    onWordDefinitionChange: (String) -> Unit,
    wordTranslation: String,
    onWordTranslationChange: (String) -> Unit,
    wordMeaning: String,
    onWordMeaningChange: (String) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight(align = Alignment.CenterVertically)
            .background(LocalAppColors.current.primaryBackground, shape = RoundedCornerShape(7.dp))
            .border(1.dp, LocalAppColors.current.surfaceVariant, shape = RoundedCornerShape(7.dp))
            .padding(10.dp)
    ) {
        Column {
            CustomTextField(
                value = wordDefinition,
                onValueChange = onWordDefinitionChange,
                placeholderText = "Термін"
            )

            Spacer(modifier = Modifier.height(8.dp))

            CustomTextField(
                value = wordTranslation,
                onValueChange = onWordTranslationChange,
                placeholderText = "Переклад"
            )

            Spacer(modifier = Modifier.height(8.dp))

            CustomTextField(
                value = wordMeaning,
                onValueChange = onWordMeaningChange,
                placeholderText = "Значення"
            )
        }
    }
}

@Composable
fun CardAdd(onClick: () -> Unit) {

    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val backgroundColor = if (isPressed) LocalAppColors.current.headerTextColor else LocalAppColors.current.primaryBackground

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 20.dp)
    ) {
        Button(
            onClick = onClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(45.dp)
                .border(1.dp, LocalAppColors.current.surfaceVariant, shape = RoundedCornerShape(7.dp)),
            colors = ButtonDefaults.buttonColors(containerColor = backgroundColor),
            shape = RoundedCornerShape(7.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                AddCircleOutlined(
                    modifier = Modifier
                        .size(48.dp)
                        .padding(end = 10.dp),
                    tint = LocalAppColors.current.headerTextColor,
                    strokeWidth = 25f
                )
                CustomText(
                    text = "Додати картку",
                    style = LocalAppTypography.current.labelMedium,
                    color = LocalAppColors.current.headerTextColor
                )
            }
        }
    }
}


