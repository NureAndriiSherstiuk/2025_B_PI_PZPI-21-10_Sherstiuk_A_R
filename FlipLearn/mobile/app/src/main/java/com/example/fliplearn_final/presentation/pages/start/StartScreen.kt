package com.example.fliplearn_final.presentation.pages.start

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.fliplearn_final.R
import com.example.fliplearn_final.presentation.ui.components.CustomText
import com.example.fliplearn_final.presentation.ui.theme.LocalAppColors
import com.example.fliplearn_final.presentation.ui.theme.LocalAppTypography


@Composable
fun StartScreen(
    viewModel: StartViewModel = hiltViewModel(),
    onNavigateToSignIn: () -> Unit,
    onNavigateToSignUp: () -> Unit,
) {

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = LocalAppColors.current.primaryBackground
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 50.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(5.dp)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_logo),
                        contentDescription = "Logo",
                        modifier = Modifier.size(25.dp),
                        contentScale = ContentScale.Fit,
                        colorFilter = ColorFilter.tint(LocalAppColors.current.headerTextColor)
                    )
                    CustomText(
                        text = "FlipLearn",
                        style = LocalAppTypography.current.titleLarge,
                        color = LocalAppColors.current.headerTextColor
                    )
                }
                Icon(
                    painter = painterResource(id = R.drawable.ic_search),
                    contentDescription = "Search",
                    modifier = Modifier.size(21.dp),
                    tint = LocalAppColors.current.headerTextColor
                )
            }
            LazyRow {
                items(3) {
                    InitialCard()
                }
            }

            CustomText(
                text = "Шукайте та створюйте словники з новими словами.",
                modifier = Modifier
                    .padding(horizontal = 46.dp, vertical = 38.dp)
                    .fillMaxWidth(),
                style = LocalAppTypography.current.displayLarge,
                textAlign = TextAlign.Center,
                color = LocalAppColors.current.headerTextColor
            )


            Spacer(modifier = Modifier.weight(1f))


            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .padding(bottom = 85.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Button(
                    onClick = onNavigateToSignUp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF94A0FE)),
                    shape = RoundedCornerShape(7.dp)
                ) {
                    CustomText(
                        text = "Зареєструватися",
                        style = LocalAppTypography.current.bodySmall,
                        color = LocalAppColors.current.headerTextColor
                    )
                }
                TextButton(
                    onClick = onNavigateToSignIn
                ) {
                    CustomText(
                        text = "Увійти",
                        style = LocalAppTypography.current.bodySmall,
                        color = LocalAppColors.current.primaryTextColor
                    )
                }
            }
        }
    }
}

@Composable
private fun InitialCard() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(334.dp)
    ) {
        Image(
            painter = painterResource(id = R.drawable.background),
            contentDescription = "Background",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.FillBounds
        )
        Card(
            modifier = Modifier
                .width(320.dp)
                .height(220.dp)
                .align(Alignment.TopCenter)
                .padding(top = 59.dp),
            shape = RoundedCornerShape(10.dp),
            colors = CardDefaults.cardColors(containerColor = LocalAppColors.current.primaryBackground),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                LocalAppColors.current.primaryButtonColor,
                                LocalAppColors.current.primaryTextInvertColor
                            ),
                            radius = 300f
                        )
                    )
            )
        }
        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(15.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(7.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFF3F3F3))
            )
            Box(
                modifier = Modifier
                    .size(10.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFF3F3F3))
            )
            Box(
                modifier = Modifier
                    .size(7.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFF3F3F3))
            )
        }
    }
}