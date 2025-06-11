package com.example.fliplearn_final.presentation.ui.widgets

import androidx.compose.runtime.Composable

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.fliplearn_final.presentation.ui.components.CustomText
import com.example.fliplearn_final.presentation.ui.theme.LocalAppColors
import com.example.fliplearn_final.presentation.ui.theme.LocalAppTypography


data class FlashCardData(
    val term: String,
    val meaning: String,
    val translation: String
)

@Composable
fun FlashCardList(cards: List<FlashCardData>) {
    LazyRow(
        modifier = Modifier.fillMaxWidth(),
        contentPadding = PaddingValues(16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(cards) { card ->
            FlashCard(cardData = card)
        }
    }
}

@Composable
fun FlashCard(cardData: FlashCardData, modifier: Modifier = Modifier) {
    var flipped by remember { mutableStateOf(false) }
    val rotation = animateFloatAsState(
        targetValue = if (flipped) 180f else 0f,
        animationSpec = tween(durationMillis = 400)
    )

    Box(
        modifier = modifier
            .width(285.dp)
            .height(225.dp)
            .clickable { flipped = !flipped }
            .shadow(4.dp, RoundedCornerShape(10.dp)),
        contentAlignment = Alignment.Center
    ) {
        Box(
            Modifier
                .fillMaxSize()
                .graphicsLayer {
                    rotationY = rotation.value
                    cameraDistance = 12f * density
                    transformOrigin = TransformOrigin.Center
                }
        ) {
            CardFace(
                modifier = Modifier
                    .graphicsLayer {
                        alpha = if (rotation.value < 90f) 1f else 0f
                    },
                content = { CustomText(cardData.term , style = LocalAppTypography.current.headlineLarge) }
            )


            CardFace(
                modifier = Modifier
                    .graphicsLayer {
                        alpha = if (rotation.value > 90f) 1f else 0f
                        rotationY = 180f
                    },
                content = {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        CustomText(cardData.meaning , style = LocalAppTypography.current.bodyLarge , textAlign = TextAlign.Center , overflow = TextOverflow.Clip)
                        Spacer(Modifier.height(10.dp))
                        CustomText(cardData.translation , style = LocalAppTypography.current.bodyLarge , textAlign = TextAlign.Center)
                    }
                }
            )
        }
    }
}

@Composable
private fun CardFace(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .clip(RoundedCornerShape(10.dp))
            .background(
                Brush.radialGradient(
                    colors = listOf(LocalAppColors.current.progressColor, LocalAppColors.current.primaryBackground),
                    center = Offset.Unspecified,
                    radius = 350f
                )
            )
            .border(
                width = 0.1.dp,
                color = LocalAppColors.current.primaryTextColor,
                shape = RoundedCornerShape(10.dp)
            ),
        contentAlignment = Alignment.Center
    ) {
        content()
    }
}