package com.example.fliplearn_final.presentation.ui.components



import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.fliplearn_final.presentation.ui.theme.LocalAppColors

@Composable
fun CustomLinearProgressIndicator(
    progress: Float,
    modifier: Modifier = Modifier,
    backgroundColor: Color = LocalAppColors.current.elevatedSurface,
    progressColor: Color = LocalAppColors.current.primaryTintColor,
    circleColor: Color = LocalAppColors.current.progressColor,
    height: Dp = 20.dp,
    circleRadius: Dp = 10.dp
) {
    val animatedProgress by animateFloatAsState(
        targetValue = progress.coerceIn(0f, 1f),
        animationSpec = tween(durationMillis = 500),
        label = "progressAnimation"
    )

    Canvas(modifier = modifier.height(height)) {
        val width = size.width
        val barHeight = size.height
        val progressWidth = animatedProgress * width


        drawRoundRect(
            color = backgroundColor,
            size = Size(width, barHeight),
            cornerRadius = CornerRadius(x = barHeight / 2, y = barHeight / 2)
        )


        drawRoundRect(
            color = progressColor,
            size = Size(progressWidth, barHeight),
            cornerRadius = CornerRadius(x = barHeight / 2, y = barHeight / 2)
        )


        val circleOffsetX = (progressWidth - 10.dp.toPx()).coerceAtLeast(0f)
        drawCircle(
            color = circleColor,
            radius = circleRadius.toPx(),
            center = Offset(x = circleOffsetX, y = barHeight / 2)
        )
    }
}
