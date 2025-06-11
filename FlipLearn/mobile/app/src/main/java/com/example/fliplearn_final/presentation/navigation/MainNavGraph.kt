package com.example.fliplearn_final.presentation.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.example.fliplearn_final.presentation.pages.dictionary.DictionaryScreen
import com.example.fliplearn_final.presentation.pages.folder.FolderScreen
import com.example.fliplearn_final.presentation.pages.main.MainScreenContent
import com.example.fliplearn_final.presentation.pages.profile.ProfileScreen
import com.example.fliplearn_final.presentation.pages.test_1.Test1Screen
import com.example.fliplearn_final.presentation.pages.test_2.Test2Screen
import com.example.fliplearn_final.presentation.pages.test_3.Test3Screen


fun NavGraphBuilder.mainNavGraph(
    navController: NavHostController,
    rootNavController: NavHostController,
    onNavigateToStart: () -> Unit
) {
    composable(BottomNavItem.Home.route) { MainScreenContent(navController = navController) }
    composable(BottomNavItem.Profile.route) { ProfileScreen(onNavigateToStart = onNavigateToStart) }

    composable("dictionary_detail/{dictionaryId}") { backStackEntry ->
        val dictionaryId = backStackEntry.arguments?.getString("dictionaryId")?.toIntOrNull()
        dictionaryId?.let {
            DictionaryScreen(
                dictionaryId = it,
                onBack = { navController.popBackStack() },
                navController = navController
            )
        }
    }

    composable("folder_detail/{folderId}") { backStackEntry ->
        val folderId = backStackEntry.arguments?.getString("folderId")?.toIntOrNull()
        if (folderId != null) {
            FolderScreen(
                onBack = { navController.popBackStack() },
                folderId = folderId,
                navController = navController
            )
        }

    }

    composable("test1/{dictionaryId}/{userId}") { backStackEntry ->
        val dictionaryId = backStackEntry.arguments?.getString("dictionaryId")?.toIntOrNull()
        val userId = backStackEntry.arguments?.getString("userId")?.toIntOrNull()

        if (dictionaryId != null && userId != null) {
            Test1Screen(
                dictionaryId = dictionaryId,
                userId = userId,
                onBack = { navController.popBackStack() },
                navController = navController
            )
        }
    }

    composable("test2/{dictionaryId}/{userId}") { backStackEntry ->
        val dictionaryId = backStackEntry.arguments?.getString("dictionaryId")?.toIntOrNull()
        val userId = backStackEntry.arguments?.getString("userId")?.toIntOrNull()

        if (dictionaryId != null && userId != null) {
            Test2Screen(
                dictionaryId = dictionaryId,
                userId = userId,
                onBack = { navController.popBackStack() },
                navController = navController
            )
        }

    }

    composable("test3/{dictionaryId}/{userId}") { backStackEntry ->
        val dictionaryId = backStackEntry.arguments?.getString("dictionaryId")?.toIntOrNull()
        val userId = backStackEntry.arguments?.getString("userId")?.toIntOrNull()

        if (dictionaryId != null && userId != null) {
            Test3Screen(
                dictionaryId = dictionaryId,
                userId = userId,
                onBack = { navController.popBackStack() },
                navController = navController
            )
        }
    }

    createScreenGraph(navController)
}

