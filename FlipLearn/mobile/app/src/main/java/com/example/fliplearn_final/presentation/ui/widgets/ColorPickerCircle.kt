package com.example.fliplearn_final.presentation.ui.widgets

import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.fliplearn_final.presentation.ui.theme.LocalAppColors



@Composable
fun ColorPickerCircle(
    size: Dp = 85.dp,
    defaultColor: Color = LocalAppColors.current.progressColor,
    onColorChange: (Color) -> Unit = {}
) {
    var currentColor by remember { mutableStateOf(defaultColor) }

    val hsv = remember { FloatArray(3) }.also {
        android.graphics.Color.colorToHSV(defaultColor.toArgb(), it)
    }

    Surface(
        modifier = Modifier
            .size(size)
            .clip(CircleShape)
            .pointerInput(Unit) {
                detectVerticalDragGestures { change, dragAmount ->

                    hsv[0] = (hsv[0] + dragAmount / 10f) % 360f
                    if (hsv[0] < 0) hsv[0] += 360f
                    val newColor = Color(android.graphics.Color.HSVToColor(hsv))
                    currentColor = newColor
                    onColorChange(newColor)
                }
            },
        color = currentColor,
        shape = CircleShape
    ) {}
}
