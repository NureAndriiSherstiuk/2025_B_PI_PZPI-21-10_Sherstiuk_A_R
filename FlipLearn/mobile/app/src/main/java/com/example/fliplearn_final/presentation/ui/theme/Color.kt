package com.example.fliplearn_final.presentation.ui.theme

import androidx.compose.ui.graphics.Color

data class Colors(
    val primaryBackground: Color,
    val secondaryBackground: Color,
    val headerTextColor: Color,
    val primaryTextColor: Color,
    val primaryTextInvertColor: Color,
    val hintTextColor: Color,
    val primaryTintColor: Color,
    val secondaryTintColor: Color,
    val accentColor: Color,
    val notificationColor: Color,
    val actionTextColor: Color,
    val borderColor: Color,
    val hiddenColor: Color,
    val surfaceVariant: Color,
    val elevatedSurface: Color,
    val tertiaryTextColor: Color,
    val overlayBackground: Color,
    val dividerColor: Color,
    val progressColor: Color,
    val primaryButtonColor: Color
)

val lightPalette = Colors(
    primaryBackground = Color(0xFFFFFFFF),
    secondaryBackground = Color(0xFFF3D86D),
    headerTextColor = Color(0xFF000000),
    primaryTextColor = Color(0xFF4F4F4F),
    primaryTextInvertColor = Color(0xFFFFFFFF),
    hintTextColor = Color(0xFF8B8B8B),
    primaryTintColor = Color(0x7094A0FE),
    secondaryTintColor = Color(0xFFCBD1FF),
    accentColor = Color(0xFF90FF88),
    notificationColor = Color(0xFFFF6E6E),
    actionTextColor = Color(0xB22A42F9),
    borderColor = Color(0xFF979797),
    hiddenColor = Color(0x33FFFFFF),
    surfaceVariant = Color(0xFFD9D9D9),
    elevatedSurface = Color(0xFFEFEFEF),
    tertiaryTextColor = Color(0xFFADADAD),
    overlayBackground = Color(0x9EF3D86D),
    dividerColor = Color(0xFFE5E5E5),
    progressColor = Color(0xFFB3BCFF),
    primaryButtonColor = Color(0xFF94A0FE)
)

val darkPalette = Colors(
    primaryBackground = Color(0xFF121212),
    secondaryBackground = Color(0xFF1F1F1F),
    headerTextColor = Color(0xFFFFFFFF),
    primaryTextColor = Color(0xFFE0E0E0),
    primaryTextInvertColor = Color(0xFF000000),
    hintTextColor = Color(0xFF9E9E9E),
    primaryTintColor = Color(0x7094A0FE),
    secondaryTintColor = Color(0xFFB3BCFF),
    accentColor = Color(0xFF90FF88),
    notificationColor = Color(0xFFFF6E6E),
    actionTextColor = Color(0xFF6C8CFF),
    borderColor = Color(0xFF2C2C2C),
    hiddenColor = Color(0x88000000),
    surfaceVariant = Color(0xFF3A3A3A),
    elevatedSurface = Color(0xFF2B2B2B),
    tertiaryTextColor = Color(0xFF5C5C5C),
    overlayBackground = Color(0xAA2B2B2B),
    dividerColor = Color(0xFF3E3E3E),
    progressColor = Color(0xFF8C96E5),
    primaryButtonColor = Color(0xFF94A0FE)
)