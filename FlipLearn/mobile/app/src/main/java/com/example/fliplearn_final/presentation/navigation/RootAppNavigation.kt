package com.example.fliplearn_final.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.compose.runtime.getValue
import androidx.navigation.compose.rememberNavController
import com.example.fliplearn_final.presentation.pages.main.MainScreen
import com.example.fliplearn_final.presentation.pages.sign_in.SignInScreen
import com.example.fliplearn_final.presentation.pages.sign_in.SignInViewModel
import com.example.fliplearn_final.presentation.pages.sign_up.SignUpScreen
import com.example.fliplearn_final.presentation.pages.sign_up.SignUpViewModel
import com.example.fliplearn_final.presentation.pages.start.StartScreen
import com.example.fliplearn_final.presentation.pages.start.StartViewModel
import com.example.fliplearn_final.presentation.pages.splash.SplashScreen


@Composable
fun RootAppNavigation(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    startDestination: String = Routes.Start.route,
) {
    NavHost(
        modifier = modifier,
        navController = navController,
        startDestination = startDestination,
    ) {

        composable(Routes.Start.route) {
            val viewModel = hiltViewModel<StartViewModel>()
            val isSignedIn by viewModel.isSignedIn.collectAsState()

            LaunchedEffect(Unit) {
                viewModel.checkIfAlreadySignedIn()
            }

            LaunchedEffect(isSignedIn) {
                if (isSignedIn == true) {
                    navController.navigate(Routes.Main.route) {
                        popUpTo(Routes.Start.route) { inclusive = true }
                    }
                }
            }

            when (isSignedIn) {
                null -> SplashScreen()
                false -> StartScreen(
                    viewModel = viewModel,
                    onNavigateToSignIn = { navController.navigate(Routes.SignIn.route) },
                    onNavigateToSignUp = { navController.navigate(Routes.SignUp.route) },
                )
                true -> SplashScreen()
            }
        }




        composable(Routes.SignIn.route) {
            val viewModel = hiltViewModel<SignInViewModel>()
            SignInScreen(
                viewModel = viewModel,
                onNavigateToStart = {
                    navController.popBackStack()
                },
                onNavigateToSignUp = {
                    navController.navigate(Routes.SignUp.route)
                },
                onNavigateToMain = {
                    navController.navigate(Routes.Main.route) {
                        popUpTo(Routes.Start.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Routes.SignUp.route) {
            val viewModel = hiltViewModel<SignUpViewModel>()
            SignUpScreen(
                viewModel = viewModel,
                onNavigateToStart = {
                    navController.popBackStack()
                }, onNavigateToSignIn =
                    {
                        navController.navigate(Routes.SignIn.route)
                    },
                onNavigateToMain = {
                    navController.navigate(Routes.Main.route) {
                        popUpTo(Routes.Start.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Routes.Main.route) {
            MainScreen(rootNavController = navController)
        }

    }
}