package com.example.fliplearn_final.presentation.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.example.fliplearn_final.presentation.ui.theme.LocalAppColors
import com.example.fliplearn_final.presentation.ui.theme.LocalAppTypography


@Composable
fun CustomSearchInput(
    value: TextFieldValue,
    onValueChange: (TextFieldValue) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "Search...",
    backgroundColor: Color = LocalAppColors.current.elevatedSurface,
    textColor: Color = LocalAppColors.current.primaryTextColor,
    placeholderColor: Color = LocalAppColors.current.hintTextColor,
    iconTint: Color = LocalAppColors.current.primaryTextColor
) {
    val typography = LocalAppTypography.current

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxWidth()
            .background(backgroundColor, RoundedCornerShape(8.dp))
            .padding(horizontal = 12.dp, vertical = 8.dp)
    ) {
        Icon(
            imageVector = Icons.Default.Search,
            contentDescription = "Search Icon",
            tint = iconTint,
            modifier = Modifier.size(24.dp)
        )

        Spacer(modifier = Modifier.width(8.dp))

        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            textStyle = typography.bodyMedium.copy(color = textColor),
            singleLine = true,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            decorationBox = { innerTextField ->
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.CenterStart
                ) {
                    if (value.text.isEmpty()) {
                        CustomText(
                            text = placeholder,
                            color = placeholderColor,
                            style = typography.bodyMedium
                        )
                    }
                    innerTextField()
                }
            }
        )
    }
}
