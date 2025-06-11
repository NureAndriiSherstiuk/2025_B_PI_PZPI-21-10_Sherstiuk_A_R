package com.example.fliplearn_final.presentation.pages.folder

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.example.fliplearn_final.R
import com.example.fliplearn_final.presentation.ui.components.CustomTextField
import com.example.fliplearn_final.domain.model.Dictionary
import com.example.fliplearn_final.presentation.navigation.BottomNavItem
import com.example.fliplearn_final.presentation.ui.components.CustomText
import com.example.fliplearn_final.presentation.ui.theme.LocalAppColors
import com.example.fliplearn_final.presentation.ui.theme.LocalAppTypography


@Composable
fun FolderScreen(
    onBack: () -> Unit,
    folderId: Int,
    navController: NavHostController,
    viewModel: FolderViewModel = hiltViewModel()
) {

    val navigateToMain by viewModel.navigateToMain.collectAsState()

    LaunchedEffect(navigateToMain) {
        if (navigateToMain) {
            navController.navigate(BottomNavItem.Home.route) {
                popUpTo("folder_detail/{folderId}") { inclusive = true }
            }
        }
    }

    LaunchedEffect(folderId) {
        viewModel.loadDictionariesInFolder(folderId)
        viewModel.loadFolder(folderId)
    }


    Scaffold(
        topBar = {
            TopBar(onBack = onBack , viewModel = viewModel , folderId = folderId)
        },
        content = {
            FolderContentScreen(modifier = Modifier.padding(it), viewModel = viewModel , navController = navController)
        }
    )
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TopBar(onBack: () -> Unit, viewModel: FolderViewModel, folderId: Int) {
    val isEditMode = viewModel.isEditMode.value
    val showMenu by viewModel.showMenu

    TopAppBar(
        title = {},
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
            Row(horizontalArrangement = Arrangement.spacedBy(5.dp)) {
                Box {
                    IconButton(onClick = { viewModel.showMenu() }) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_more_button),
                            contentDescription = "Options",
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    DropdownMenu(
                        expanded = showMenu,
                        onDismissRequest = { viewModel.hideMenu() },
                        containerColor = LocalAppColors.current.primaryBackground
                    ) {
                        DropdownMenuItem(
                            text = { CustomText(text = "редагувати") },
                            onClick = {
                                viewModel.hideMenu()
                                viewModel.toggleEditMode()
                            }
                        )
                        DropdownMenuItem(
                            text = { CustomText(text = "видалити") },
                            onClick = {
                                viewModel.deleteFolder()
                                viewModel.hideMenu()
                            }
                        )
                    }
                }

                if (isEditMode) {
                    IconButton(onClick = {
                        viewModel.updateFolder()
                        viewModel.toggleEditMode()
                    }) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_submit),
                            contentDescription = "Finished editing",
                            modifier = Modifier.size(24.dp)
                        )
                    }
                } else {
                    IconButton(onClick = { viewModel.showDictionaryPicker(folderId) }) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_add_dictionary),
                            contentDescription = "Add Dictionary",
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = LocalAppColors.current.primaryBackground,
            titleContentColor = LocalAppColors.current.primaryBackground
        )
    )
}


@Composable
fun FolderContentScreen(modifier: Modifier, viewModel: FolderViewModel, navController: NavHostController) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(15.dp)
    ) {
        val state by viewModel.state.collectAsStateWithLifecycle()
        val folders by viewModel.folders.collectAsState()
        val dictionariesInFolder by viewModel.dictionariesInFolder.collectAsState()
        val isEditMode = viewModel.isEditMode.value

        val name by viewModel.folderName
        val description by viewModel.folderDescription

        state.folder?.let { folder ->
            if(isEditMode) {
                CustomTextField(
                    value = name,
                    onValueChange = { newValue ->
                        viewModel.onFolderNameChange(newValue)
                    },
                    placeholderText = "Введіть назву"
                )
            } else {
                CustomText(
                    text = folder.name,
                    style = LocalAppTypography.current.headlineLarge,
                )
            }


            folders.forEach { fold ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(22.dp)
                                .clip(CircleShape)
                                .background(LocalAppColors.current.surfaceVariant)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        CustomText(
                            text = "${fold.username}",
                            style = LocalAppTypography.current.labelMedium,
                            color = LocalAppColors.current.primaryTextColor
                        )
                    }

                    Surface(
                        color = LocalAppColors.current.elevatedSurface,
                        shape = RoundedCornerShape(5.dp)
                    ) {
                        CustomText(
                            text = "${fold.dictionariesCount} sets",
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            style = LocalAppTypography.current.labelMedium,
                            color = LocalAppColors.current.primaryTextColor
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(15.dp))

            if(isEditMode) {
                CustomTextField(
                    value = description,
                    onValueChange = { newDescription ->
                        viewModel.onFolderDescriptionChange(newDescription)
                    },
                    placeholderText = "Введіть опис",
                    backgroundColor = LocalAppColors.current.overlayBackground
                )
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            color = LocalAppColors.current.overlayBackground,
                            shape = RoundedCornerShape(7.dp)
                        )
                        .padding(16.dp)
                ) {
                    CustomText(text = "${folder.description}")
                }
            }

            Spacer(modifier = Modifier.height(15.dp))
            if (dictionariesInFolder.isEmpty()) {
                CustomText(
                    text = "У вас поки що немає словників",
                    style = LocalAppTypography.current.bodyMedium,
                    modifier = Modifier.padding(vertical = 16.dp)
                )
            } else {
                DictionaryGrid(
                    dictionaries = dictionariesInFolder,
                    onDictionaryClick = { dictId ->
                        navController.navigate("dictionary_detail/$dictId")
                    },
                    modifier = Modifier.heightIn(max = 300.dp)
                )
            }
        }
    }

    if (viewModel.isDictionaryPickerVisible.value) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(LocalAppColors.current.primaryTextColor.copy(alpha = 0.6f)),
            contentAlignment = Alignment.Center
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth(0.85f)
                    .heightIn(min = 200.dp, max = 400.dp),
                shape = RoundedCornerShape(12.dp),
                color = LocalAppColors.current.primaryBackground
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    CustomText(
                        text = "Choose Dictionary",
                        style = LocalAppTypography.current.headlineSmall,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )

                    viewModel.availableDictionaries.forEach { dict ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { viewModel.addDictionaryToFolder(dict) }
                                .padding(vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            CustomText(
                                text = dict.title,
                                style = LocalAppTypography.current.bodyMedium
                            )
                        }
                    }

                    Spacer(Modifier.height(8.dp))

                    CustomText(
                        text = "Cancel",
                        style = LocalAppTypography.current.labelMedium,
                        color = LocalAppColors.current.primaryTextColor,
                        modifier = Modifier
                            .align(Alignment.End)
                            .clickable { viewModel.hideDictionaryPicker() }
                    )
                }
            }
        }
    }
}


@Composable
private  fun DictionaryGrid(
    dictionaries: List<Dictionary>,
    onDictionaryClick: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = modifier
    ) {
        items(dictionaries, key = { it.dictionaryId }) { dict ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp)
                    .clickable { onDictionaryClick(dict.dictionaryId) },
                shape = RoundedCornerShape(5.dp),
                border = BorderStroke(1.dp, LocalAppColors.current.headerTextColor.copy(alpha = 0.2f)),
                colors =  CardDefaults.cardColors(
                    containerColor = LocalAppColors.current.primaryBackground,
                    contentColor   = LocalAppColors.current.primaryTextColor
                )
            ) {
                Column(modifier = Modifier
                    .padding(8.dp)
                    .fillMaxSize()
                ) {

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        CustomText(
                            text = dict.title,
                            style = LocalAppTypography.current.labelLarge,
                            color = LocalAppColors.current.headerTextColor,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.weight(1f)
                        )
                        Surface(color = LocalAppColors.current.elevatedSurface, shape = RoundedCornerShape(5.dp)) {
                            CustomText(
                                text = dict.label,
                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp),
                                style = LocalAppTypography.current.labelSmall
                            )
                        }

                    }

                    Spacer(Modifier.height(4.dp))


                    Surface(
                        color = LocalAppColors.current.secondaryTintColor,
                        shape = RoundedCornerShape(3.dp),
                    ) {
                        CustomText(
                            text = "${dict.termsCount} terms",
                            style = LocalAppTypography.current.labelSmall,
                            color = LocalAppColors.current.primaryTextColor,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                        )
                    }




                    Spacer(Modifier.height(4.dp))

                    CustomText(
                        text = dict.description!!,
                        style = LocalAppTypography.current.bodySmall,
                        color = LocalAppColors.current.headerTextColor,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )

                    Spacer(Modifier.height(4.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(16.dp)
                                .clip(CircleShape)
                                .background(LocalAppColors.current.surfaceVariant)
                        )
                        Spacer(Modifier.width(6.dp))
                        CustomText(
                            text = dict.userName,
                            style = LocalAppTypography.current.labelSmall,
                            color = LocalAppColors.current.primaryTextColor,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
        }
    }
}

