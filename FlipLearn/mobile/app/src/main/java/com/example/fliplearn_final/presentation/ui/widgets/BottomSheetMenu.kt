package com.example.fliplearn_final.presentation.ui.widgets

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.example.fliplearn_final.presentation.ui.theme.LocalAppColors
import com.example.fliplearn_final.R
import com.example.fliplearn_final.presentation.ui.components.CustomText
import com.example.fliplearn_final.presentation.ui.theme.LocalAppTypography


@Composable
fun BottomSheetMenu(
    showBottomSheet: Boolean,
    onDismiss: () -> Unit,
    onCreateDictionary: () -> Unit,
    onCreateFolder: () -> Unit
) {
    Box(modifier = Modifier.fillMaxSize()) {
        AnimatedVisibility(
            visible = showBottomSheet,
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier
                .zIndex(1f)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(LocalAppColors.current.headerTextColor.copy(alpha = 0.2f))
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = onDismiss
                    )
            )
        }

        AnimatedVisibility(
            visible = showBottomSheet,
            enter = slideInVertically(
                initialOffsetY = { it },
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessLow
                )
            ) + fadeIn(),
            exit = slideOutVertically(
                targetOffsetY = { it },
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioNoBouncy,
                    stiffness = Spring.StiffnessMedium
                )
            ) + fadeOut(),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .zIndex(2f)
        ) {
            BottomSheetContent(
                onDismiss = onDismiss,
                onCreateDictionary = onCreateDictionary,
                onCreateFolder = onCreateFolder
            )
        }
    }
}


@Composable
fun BottomSheetContent(
    onDismiss: () -> Unit,
    onCreateDictionary: () -> Unit,
    onCreateFolder: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(240.dp),
        shape = RoundedCornerShape(topStart = 15.dp, topEnd = 15.dp),
        color = LocalAppColors.current.primaryBackground,
        shadowElevation = 8.dp,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .width(75.dp)
                    .height(5.dp)
                    .clip(RoundedCornerShape(7.dp))
                    .background(LocalAppColors.current.surfaceVariant)
            )

            Spacer(modifier = Modifier.height(24.dp))

            MenuItem(
                icon = {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_dictionary),
                        contentDescription = "Create Dictionary",
                        modifier = Modifier.size(24.dp)
                    )
                },
                title = "Створити словник",
                isSelected = true,
                backgroundColor = LocalAppColors.current.primaryButtonColor,
                onClick = onCreateDictionary
            )

            Spacer(modifier = Modifier.height(12.dp))

            MenuItem(
                icon = {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_create_folder),
                        contentDescription = "Create Folder",
                        tint = LocalAppColors.current.headerTextColor,
                        modifier = Modifier.size(24.dp)
                    )
                },
                title = "Створити папку",
                isSelected = false,
                backgroundColor = LocalAppColors.current.primaryBackground,
                onClick = onCreateFolder,
                modifier = Modifier.border(
                    width = 1.dp,
                    color = LocalAppColors.current.borderColor.copy(alpha = 0.5f),
                    shape = RoundedCornerShape(15.dp)
                )
            )

        }
    }
}


@Composable
fun MenuItem(
    icon: @Composable () -> Unit,
    title: String,
    isSelected: Boolean,
    backgroundColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = backgroundColor
        ),
        shape = RoundedCornerShape(15.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            icon()
            Spacer(modifier = Modifier.width(16.dp))
            CustomText(
                text = title,
                color = LocalAppColors.current.headerTextColor,
                style = LocalAppTypography.current.displayMedium
            )
        }
    }
}



