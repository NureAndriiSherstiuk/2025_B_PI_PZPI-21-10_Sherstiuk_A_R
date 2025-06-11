package com.example.fliplearn_final.presentation.ui.widgets


import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun ExpandableChevron(
    expanded: Boolean,
    modifier: Modifier = Modifier,
    strokeWidth: Float = 2f
) {
    IconButton(modifier = modifier ,  onClick = { },) {
        if (expanded) {
            ChevronUp(
                strokeWidth = strokeWidth
            )
        } else {
            ChevronDown(
                strokeWidth = strokeWidth
            )
        }
    }
}
