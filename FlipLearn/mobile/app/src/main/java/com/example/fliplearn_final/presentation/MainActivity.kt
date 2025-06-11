package com.example.fliplearn_final.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import com.example.fliplearn_final.data.local.datastore.UserPreferences
import com.example.fliplearn_final.presentation.navigation.RootAppNavigation
import com.example.fliplearn_final.presentation.pages.profile.Theme
import com.example.fliplearn_final.presentation.ui.theme.AppTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject lateinit var userPreferences: UserPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val themeFlowState = userPreferences.selectedTheme.collectAsState(initial = Theme.Light.name)
            val theme = remember(themeFlowState.value) {
                Theme.valueOf(themeFlowState.value ?: Theme.Light.name)
            }

            val useDarkTheme = when (theme) {
                Theme.Dark -> true
                Theme.Light -> false
                Theme.Light -> isSystemInDarkTheme()
            }

            AppTheme(useDarkTheme = useDarkTheme) {
                RootAppNavigation()
            }
        }
    }
}


