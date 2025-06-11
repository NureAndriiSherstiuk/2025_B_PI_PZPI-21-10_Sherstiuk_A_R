package com.example.fliplearn_final.presentation.pages.dictionary

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.example.fliplearn_final.R
import com.example.fliplearn_final.presentation.ui.components.CustomButton
import com.example.fliplearn_final.presentation.ui.components.CustomText
import com.example.fliplearn_final.presentation.ui.theme.LocalAppColors
import com.example.fliplearn_final.presentation.ui.theme.LocalAppTypography
import com.example.fliplearn_final.presentation.ui.widgets.FlashCardData
import com.example.fliplearn_final.presentation.ui.widgets.FlashCardList
import com.example.fliplearn_final.util.customCompare

@Composable
fun DictionaryScreen(
    viewModel: DictionaryViewModel = hiltViewModel(),
    dictionaryId: Int,
    onBack: () -> Unit,
    navController: NavHostController
) {

    LaunchedEffect(dictionaryId) {
        viewModel.loadDictionary(dictionaryId)
    }

    Scaffold(
        topBar = { TopBar( onBack = onBack) },
        content = {
            DictionaryScreenContent(
                modifier = Modifier.padding(it),
                state = viewModel.uiState,
                navController = navController
            )
        },
    )
}


@Composable
fun DictionaryScreenContent(modifier: Modifier ,state: DictionaryWithCardsUiState ,navController: NavHostController) {

    var sortMode by remember { mutableStateOf(SortMode.TERM) }
    val dictionary = state.dictionary ?: return
    val cards = state.cards

    val sortedCards = remember(cards, sortMode) {
        cards.sortedWith(
            Comparator { a, b ->
                when(sortMode) {
                    SortMode.TERM -> customCompare(a.term, b.term)
                    SortMode.MEANING -> customCompare(a.meaning, b.meaning)
                    SortMode.TRANSLATION -> customCompare(a.translation ?: "", b.translation ?: "")
                }
            }
        )
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 15.dp)
    ) {
        item {

            FlashCardList(
                cards = cards.map {
                    FlashCardData(it.term, it.meaning, it.translation)
                }
            )

            Spacer(modifier = Modifier.height(20.dp))

            CustomText(
                text = dictionary.title,
                style = LocalAppTypography.current.headlineMedium
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 5.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(20.dp)
                            .clip(CircleShape)
                            .background(LocalAppColors.current.surfaceVariant)
                    )
                    Spacer(Modifier.width(6.dp))
                    CustomText(
                        text = dictionary.userName,
                        style = LocalAppTypography.current.labelMedium,
                        color = LocalAppColors.current.primaryTextColor,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Surface(
                    color = LocalAppColors.current.elevatedSurface,
                    shape = RoundedCornerShape(3.dp),
                ) {
                    CustomText(
                        text = "${dictionary.termsCount} terms",
                        style = LocalAppTypography.current.labelMedium,
                        color = LocalAppColors.current.primaryTextColor,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                CustomButton(
                    text = "True or false",
                    onClick = {
                        navController.navigate("test1/${dictionary.dictionaryId}/${dictionary.userId}")
                    }
                )
                CustomButton(
                    text = "Multiple choice",
                    onClick = {
                        navController.navigate("test2/${dictionary.dictionaryId}/${dictionary.userId}")
                    }
                )
                CustomButton(
                    text = "Input",
                    onClick = {
                        navController.navigate("test3/${dictionary.dictionaryId}/${dictionary.userId}")
                    }
                )
            }

            Spacer(modifier = Modifier.height(25.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                CustomText(
                    text = "Терміни",
                    style = LocalAppTypography.current.titleSmall
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(start = 5.dp)
                ) {
                    CustomText(
                        text = "Сортувати (${sortMode.name.lowercase().replaceFirstChar { it.uppercase() }})",
                        style = LocalAppTypography.current.bodyMedium,
                        modifier = Modifier.padding(end = 5.dp)
                    )


                    IconButton(
                        onClick = {
                            sortMode = when(sortMode) {
                                SortMode.TERM -> SortMode.MEANING
                                SortMode.MEANING -> SortMode.TRANSLATION
                                SortMode.TRANSLATION -> SortMode.TERM
                            }
                        },
                        modifier = Modifier.size(20.dp)
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_sort),
                            contentDescription = "Sort"
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(17.dp))
        }

        items(sortedCards) { card ->
            Spacer(modifier = Modifier.height(10.dp))
            TermDefinitionCard(
                term = card.term,
                definition = card.meaning
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TopBar(onBack: () -> Unit) {
    TopAppBar(
        title = {

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
            containerColor = LocalAppColors.current.primaryBackground,
            titleContentColor = LocalAppColors.current.primaryBackground
        )
    )
}

@Composable
fun TermDefinitionCard(
    term: String,
    definition: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .border(
                width = 0.5.dp,
                color = LocalAppColors.current.primaryTextColor,
                shape = RoundedCornerShape(10.dp)
            )
            .background(
                color = LocalAppColors.current.primaryBackground,
                shape = RoundedCornerShape(10.dp)
            )
            .padding(16.dp)
    ) {
        Column {
            CustomText(
                text = term,
                style = LocalAppTypography.current.headlineSmall,
                modifier = Modifier.padding(bottom = 10.dp)
            )
            CustomText(
                text = definition,
                style = LocalAppTypography.current.bodyLarge,
                modifier = Modifier.padding(bottom = 10.dp , start = 10.dp )
            )
        }
    }
}