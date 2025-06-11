package com.example.fliplearn_final.presentation.ui.widgets

import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.fliplearn_final.presentation.ui.theme.LocalAppColors
import com.example.fliplearn_final.presentation.ui.theme.montserratAlternatesRegular


@OptIn(ExperimentalMaterial3Api::class, ExperimentalAnimationApi::class)
@Composable
fun Dropdown(
    items: List<String>,
    textName: String,
    selectedItem: String,
    onItemSelected: (String) -> Unit,
    modifier: Modifier = Modifier,
    fieldWidth: Dp = 120.dp,
    height: Dp = 45.dp,
    isRounded: Boolean = false
) {
    var expanded by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
    ) {
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = it },
            modifier = Modifier
                .width(fieldWidth)
                .height(height)
        ) {
            OutlinedTextField(
                value = selectedItem,
                onValueChange = { },
                textStyle = TextStyle(
                    fontFamily = montserratAlternatesRegular,
                    fontWeight = FontWeight.Normal,
                    fontSize = 10.sp,
                    lineHeight = 14.sp,
                    letterSpacing = 0.5.sp
                ),
                readOnly = true,
                shape = if (isRounded) RoundedCornerShape(7.dp) else OutlinedTextFieldDefaults.shape,
                trailingIcon = {
                    AnimatedContent(
                        targetState = expanded,
                        transitionSpec = {
                            (fadeIn() + slideInVertically()).togetherWith(fadeOut() + slideOutVertically())
                        },
                        label = "IconAnimation"
                    ) { isExpanded ->
                        ExpandableChevron(
                            expanded = isExpanded,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                },
                placeholder = if (selectedItem.isEmpty()) {
                    {
                        Text(
                            text = textName,
                            fontFamily = montserratAlternatesRegular,
                            fontWeight = FontWeight.Normal,
                            fontSize = 10.sp,
                            lineHeight = 14.sp,
                            letterSpacing = 0.5.sp,
                            color = LocalAppColors.current.primaryTextColor
                        )
                    }
                } else null,
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedBorderColor = LocalAppColors.current.surfaceVariant,
                    unfocusedTextColor = LocalAppColors.current.headerTextColor,
                    unfocusedLabelColor = LocalAppColors.current.hintTextColor,
                    unfocusedPlaceholderColor = LocalAppColors.current.hintTextColor,
                    focusedBorderColor = LocalAppColors.current.actionTextColor,
                    focusedTextColor = LocalAppColors.current.headerTextColor
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(height)
                    .menuAnchor(),
                singleLine = true
            )

            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier
                    .width(fieldWidth)
                    .background(LocalAppColors.current.primaryBackground, shape = if (isRounded) RoundedCornerShape(7.dp) else RoundedCornerShape(4.dp))
            ) {
                items.forEach { level ->
                    DropdownMenuItem(
                        text = {
                            Text(
                                text = level,
                                fontFamily = montserratAlternatesRegular,
                                fontWeight = FontWeight.Normal,
                                fontSize = 10.sp,
                                lineHeight = 14.sp,
                                letterSpacing = 0.5.sp,
                                color = LocalAppColors.current.primaryTextColor,
                                maxLines = 1
                            )
                        },
                        onClick = {
                            onItemSelected(level)
                            expanded = false
                        },
                    )
                }
            }
        }
    }
}