package com.example.fliplearn_final.presentation.ui.widgets

import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp
import com.example.fliplearn_final.presentation.ui.theme.LocalAppColors

@Composable
fun ChevronUp(
    modifier: Modifier = Modifier,
    tint: Color = LocalAppColors.current.headerTextColor,
    strokeWidth: Float = 2f
) {
    Image(
        imageVector = chevronUpIcon,
        contentDescription = "Chevron Up",
        modifier = modifier,
        colorFilter = ColorFilter.tint(tint)
    )
}

private var _chevronUp: ImageVector? = null
private val chevronUpIcon: ImageVector
    get() {
        if (_chevronUp != null) {
            return _chevronUp!!
        }
        _chevronUp = ImageVector.Builder(
            name = "ChevronUp",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            path(
                fill = null,
                fillAlpha = 1.0f,
                stroke = SolidColor(Color.Black),
                strokeAlpha = 1.0f,
                strokeLineWidth = 2f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round,
                strokeLineMiter = 1.0f,
                pathFillType = PathFillType.NonZero
            ) {
                moveTo(18f, 15f)
                lineTo(12f, 9f)
                lineTo(6f, 15f)
            }
        }.build()
        return _chevronUp!!
    }