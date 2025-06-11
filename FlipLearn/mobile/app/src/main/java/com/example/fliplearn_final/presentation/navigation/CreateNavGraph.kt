package com.example.fliplearn_final.presentation.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.example.fliplearn_final.presentation.pages.dictionary.create_dictionary.CreateDictionaryScreen
import com.example.fliplearn_final.presentation.pages.folder.create_folder.CreateFolderScreen

fun NavGraphBuilder.createScreenGraph(navController: NavHostController) {
    composable(CreateNavItem.CreateFolder.route) {
        CreateFolderScreen(
            onBack = { navController.popBackStack() }
        )
    }
    composable(CreateNavItem.CreateDictionary.route) {
        CreateDictionaryScreen(
            onBack = { navController.popBackStack() }
        )
    }
}

