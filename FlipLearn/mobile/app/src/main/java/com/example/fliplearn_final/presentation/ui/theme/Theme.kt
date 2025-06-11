package com.example.fliplearn_final.presentation.ui.theme



import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.graphics.Color

val LocalAppColors = compositionLocalOf<Colors> {
    error("No LocalAppColors provided")
}

val LocalAppTypography = compositionLocalOf<Typography> {
    error("No LocalAppTypography provided")
}

@Composable
fun AppTheme(
    useDarkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val appColors = if (useDarkTheme) darkPalette else lightPalette

    val materialColorScheme = if (useDarkTheme) {
        darkColorScheme(
            primary = appColors.primaryTintColor,
            secondary = appColors.secondaryTintColor,
            background = appColors.primaryBackground,
            surface = appColors.secondaryBackground,
            error = appColors.notificationColor,
            onPrimary = appColors.primaryTextInvertColor,
            onSecondary = appColors.primaryTextColor,
            onBackground = appColors.primaryTextColor,
            onSurface = appColors.headerTextColor,
            onError = Color.White,
        )
    } else {
        lightColorScheme(
            primary = appColors.primaryTintColor,
            secondary = appColors.secondaryTintColor,
            background = appColors.primaryBackground,
            surface = appColors.secondaryBackground,
            error = appColors.notificationColor,
            onPrimary = appColors.primaryTextInvertColor,
            onSecondary = appColors.primaryTextColor,
            onBackground = appColors.primaryTextColor,
            onSurface = appColors.headerTextColor,
            onError = Color.White,
        )
    }

    val appTypography = CustomTypography



    CompositionLocalProvider(
        LocalAppColors provides appColors,
        LocalAppTypography provides appTypography
        ) {
        MaterialTheme(
            colorScheme = materialColorScheme,
            typography = appTypography,
            shapes = Shapes(),
            content = content
        )
    }
}

