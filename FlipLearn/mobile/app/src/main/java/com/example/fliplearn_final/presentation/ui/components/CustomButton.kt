package com.example.fliplearn_final.presentation.ui.components


import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import com.example.fliplearn_final.presentation.ui.theme.LocalAppColors
import com.example.fliplearn_final.presentation.ui.theme.LocalAppTypography


@Composable
fun  CustomButton (
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
        .fillMaxWidth()
        .height(50.dp),
    shape: Shape = RoundedCornerShape(7.dp),
    textStyle: TextStyle = LocalAppTypography.current.bodySmall,
    contentColor: Color = LocalAppColors.current.headerTextColor,
    containerColor: Color = LocalAppColors.current.primaryButtonColor
) {
    Button(
        onClick = onClick,
        shape = shape,
        modifier = modifier,
        colors = ButtonDefaults.buttonColors(
            containerColor = containerColor,
            contentColor = contentColor
        )
    ) {
        CustomText(
            text = text,
            style = textStyle
        )
    }
}