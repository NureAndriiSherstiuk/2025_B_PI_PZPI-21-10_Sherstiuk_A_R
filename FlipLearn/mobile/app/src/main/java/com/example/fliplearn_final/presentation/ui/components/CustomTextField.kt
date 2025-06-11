package com.example.fliplearn_final.presentation.ui.components

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.unit.dp
import com.example.fliplearn_final.presentation.ui.theme.LocalAppColors
import com.example.fliplearn_final.presentation.ui.theme.LocalAppTypography


@Composable
fun CustomTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholderText: String,
    backgroundColor: Color = Color.Transparent
) {
    val colors = LocalAppColors.current
    val typography = LocalAppTypography.current

    val modifier = Modifier
        .fillMaxWidth()
        .border(width = 0.dp, color = Color.Transparent, shape = RectangleShape)
        .drawWithContent {
            drawContent()
            drawLine(
                color = colors.hintTextColor,
                start = Offset(0f, size.height),
                end = Offset(size.width, size.height),
                strokeWidth = 1.dp.toPx()
            )
        }

    TextField(
        modifier = modifier,
        value = value,
        onValueChange = onValueChange,
        placeholder = {
            Text(
                text = placeholderText,
                style = typography.bodyMedium,
                color = colors.hintTextColor.copy(alpha = 0.7f)
            )
        },
        colors = TextFieldDefaults.colors(
            focusedContainerColor = backgroundColor,
            unfocusedContainerColor = backgroundColor,
            disabledContainerColor = backgroundColor,
            cursorColor = colors.primaryTextColor,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent,
            focusedTextColor = colors.primaryTextColor,
            unfocusedTextColor = colors.primaryTextColor,
            disabledTextColor = colors.tertiaryTextColor,
            focusedPlaceholderColor = colors.hintTextColor.copy(alpha = 0.7f),
            unfocusedPlaceholderColor = colors.hintTextColor.copy(alpha = 0.7f),
        ),
        singleLine = true
    )
}