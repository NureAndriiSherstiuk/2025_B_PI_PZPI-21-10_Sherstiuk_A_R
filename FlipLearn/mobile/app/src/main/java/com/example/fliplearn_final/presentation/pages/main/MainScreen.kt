package com.example.fliplearn_final.presentation.pages.main

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.graphics.ColorFilter
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.fliplearn_final.R
import com.example.fliplearn_final.presentation.navigation.BottomNavItem
import com.example.fliplearn_final.presentation.navigation.CreateNavItem
import com.example.fliplearn_final.presentation.navigation.HideRoutes
import com.example.fliplearn_final.presentation.navigation.mainNavGraph
import com.example.fliplearn_final.presentation.ui.components.CustomSearchInput
import com.example.fliplearn_final.presentation.ui.components.CustomText
import com.example.fliplearn_final.presentation.ui.theme.LocalAppColors
import com.example.fliplearn_final.presentation.ui.theme.LocalAppTypography
import com.example.fliplearn_final.presentation.ui.widgets.BottomSheetMenu





@Composable
fun MainScreen(
    rootNavController: NavHostController
) {
    val bottomNavController = rememberNavController()
    var showBottomSheet by remember { mutableStateOf(false) }

    val navBackStackEntry by bottomNavController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val hideBottomBarRoutes = listOf(
        CreateNavItem.CreateDictionary.route,
        CreateNavItem.CreateFolder.route,
        HideRoutes.FolderDetailed.route,
        HideRoutes.DictionaryDetailed.route,
        HideRoutes.Test1.route,
        HideRoutes.Test2.route,
        HideRoutes.Test3.route
    )


    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            bottomBar = {
                if (hideBottomBarRoutes.none { currentRoute?.startsWith(it) == true }) {
                    BottomBar(
                        navController = bottomNavController,
                        onAddClick = { showBottomSheet = true }
                    )
                }
            }
        ) { innerPadding ->
            NavHost(
                navController = bottomNavController,
                startDestination = BottomNavItem.Home.route,
                modifier = Modifier.padding(innerPadding)
            ) {
                mainNavGraph(
                    navController = bottomNavController,
                    rootNavController = rootNavController,
                    onNavigateToStart = {
                        rootNavController.navigate("start") {
                            popUpTo(0) { inclusive = true }
                        }
                    }
                )
            }
        }

        if (showBottomSheet) {
            BottomSheetMenu(
                showBottomSheet = showBottomSheet,
                onDismiss = { showBottomSheet = false },
                onCreateDictionary = {
                    showBottomSheet = false
                    bottomNavController.navigate("create_dictionary")
                },
                onCreateFolder = {
                    showBottomSheet = false
                    bottomNavController.navigate("create_folder")
                }
            )
        }
    }
}



@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TopBar() {
    TopAppBar(
        modifier = Modifier.height(100.dp),
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_logo),
                    contentDescription = "Logo",
                    modifier = Modifier.size(20.dp),
                    colorFilter = ColorFilter.tint(LocalAppColors.current.headerTextColor)
                )

                CustomText(
                    text = "FlipLearn",
                    style = LocalAppTypography.current.titleLarge,
                    color = LocalAppColors.current.headerTextColor
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
fun MainScreenContent (
    viewModel: MainViewModel = hiltViewModel(),
    navController: NavController,
) {
    Scaffold (
        topBar = {TopBar()},
        content = {
            MainContent(modifier = Modifier.padding(it) , viewModel = viewModel, navController = navController)
        }
    )
}

@Composable
 fun MainContent(modifier: Modifier , viewModel: MainViewModel , navController: NavController) {

    val dictionaries by viewModel.dictionaries.collectAsState()
    val folders by viewModel.folders.collectAsState()
    var searchValue by remember { mutableStateOf(TextFieldValue()) }
    val query = searchValue.text.trim().lowercase()


    val filteredDicts = dictionaries.filter { dict ->
        dict.title.lowercase().contains(query)
                || (dict.description?.lowercase()?.contains(query) ?: false)
                || dict.label.lowercase().contains(query)
    }
    val filteredFolders = folders.filter { folder ->
        folder?.let { f ->
            f.name.lowercase().contains(query) ||
                    f.description?.lowercase()?.contains(query) ?: false ||
                    f.label?.lowercase()?.contains(query) ?: false
        } ?: false
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(15.dp)
    ) {

        RememberedSearchInput(
            modifier     = Modifier.fillMaxWidth(),
            placeholder  = "Словник, папка",
            value        = searchValue,
            onValueChange= { searchValue = it }
        )

        Spacer(modifier = Modifier.height(28.dp))

        CustomText(
            text = "Ваші словники",
            style = LocalAppTypography.current.labelSmall
        )
        Spacer(modifier = Modifier.height(5.dp))

        if(filteredDicts.isEmpty()) {
            CustomText(
                text = "У вас поки що немає словників",
                style = LocalAppTypography.current.bodyMedium,
                modifier = Modifier.padding(vertical = 16.dp)
            )
        } else {
            DictionaryGrid(
                dictionaries = filteredDicts,
                onDictionaryClick = {  dictionaryId ->
                    navController.navigate("dictionary_detail/$dictionaryId")
                },
                modifier = Modifier.heightIn(max = 300.dp)
            )
        }

        Spacer(modifier = Modifier.height(15.dp))

        CustomText(
            text = "Ваші папки",
            style = LocalAppTypography.current.labelSmall
        )
        Spacer(modifier = Modifier.height(5.dp))

        if(filteredFolders.isEmpty()) {
            CustomText(
                text = "У вас поки що немає папок",
                style = LocalAppTypography.current.bodyMedium,
                modifier = Modifier.padding(vertical = 16.dp)
            )
        } else {
            FolderGrid(
                folders = filteredFolders,
                onFolderClick = {
                        folderId ->
                    navController.navigate("folder_detail/$folderId")
                },
                modifier = Modifier.heightIn(max = 300.dp)
            )
        }
    }
}



@Composable
fun BottomBar(navController: NavHostController, onAddClick: () -> Unit) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route.orEmpty()


    Column {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(LocalAppColors.current.headerTextColor.copy(alpha = 0.2f))
        )
        NavigationBar(
            containerColor = LocalAppColors.current.primaryBackground
        ) {
            BottomNavItem.items.forEach { item ->
                val isSelected = currentRoute == item.route
                NavigationBarItem(
                    icon = {
                        Icon(
                            painter = painterResource(id = item.icon),
                            contentDescription = item.label
                        )
                    },
                    selected = isSelected,
                    onClick = {
                        when {
                            item == BottomNavItem.Add -> onAddClick()
                            currentRoute != item.route -> {
                                navController.navigate(item.route) {
                                    popUpTo(navController.graph.startDestinationRoute ?: item.route) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        }
                    }
                )
            }
        }
    }
}





@Composable
private fun RememberedSearchInput(
    modifier: Modifier = Modifier,
    placeholder: String = "Словник, папка",
    value: TextFieldValue,
    onValueChange: (TextFieldValue) -> Unit
) {
    CustomSearchInput(
        value          = value,
        onValueChange  = onValueChange,
        modifier       = modifier,
        placeholder    = placeholder
    )
}


@Composable
private  fun FolderGrid(
    folders: List<FolderUiState>,
    onFolderClick: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = modifier
    ) {
        items(folders, key = { it.id }) { fold ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp)
                    .clickable { onFolderClick(fold.id) },
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
                            text = fold.name,
                            style = LocalAppTypography.current.labelLarge,
                            color = LocalAppColors.current.headerTextColor,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.weight(1f)
                        )
                        Surface(color = LocalAppColors.current.elevatedSurface, shape = RoundedCornerShape(5.dp)) {
                            CustomText(
                                text = fold.label ?:"A1",
                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp),
                                style = LocalAppTypography.current.labelSmall
                            )
                        }

                    }

                    Spacer(Modifier.height(4.dp))

                    Row(modifier = Modifier.fillMaxWidth(), Arrangement.spacedBy(5.dp)) {
                        Surface(
                            color = LocalAppColors.current.secondaryBackground,
                            shape = RoundedCornerShape(3.dp),
                        ) {
                            CustomText(
                                text = "${fold.dictionariesCount} sets",
                                style = LocalAppTypography.current.labelSmall,
                                color = LocalAppColors.current.primaryTextColor,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                            )
                        }

                        Surface(
                            color = LocalAppColors.current.secondaryTintColor,
                            shape = RoundedCornerShape(3.dp),
                        ) {
                            CustomText(
                                text = "${fold.termsCount} terms",
                                style = LocalAppTypography.current.labelSmall,
                                color = LocalAppColors.current.primaryTextColor,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                            )
                        }
                    }



                    Spacer(Modifier.height(4.dp))

                    CustomText(
                        text = fold.description!!,
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
                            text = "${fold.username}",
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



@Composable
private  fun DictionaryGrid(
    dictionaries: List<DictionaryUiState>,
    onDictionaryClick: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = modifier
    ) {
        items(dictionaries, key = { it.id }) { dict ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp)
                    .clickable { onDictionaryClick(dict.id) },
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
                            text = dict.username,
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

