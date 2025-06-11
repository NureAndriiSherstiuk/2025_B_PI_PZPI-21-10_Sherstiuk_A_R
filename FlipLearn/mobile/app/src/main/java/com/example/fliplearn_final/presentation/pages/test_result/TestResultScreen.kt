package com.example.fliplearn_final.presentation.pages.test_result

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.fliplearn_final.R
import com.example.fliplearn_final.presentation.ui.components.CustomButton
import com.example.fliplearn_final.presentation.ui.components.CustomText
import com.example.fliplearn_final.presentation.ui.theme.LocalAppColors
import com.example.fliplearn_final.presentation.ui.theme.LocalAppTypography

@Composable
fun TestResultScreen(
    correct: Int,
    total: Int,
    setName: String,
    onRestart: () -> Unit,
    onReview: () -> Unit,
    onBack: () -> Unit
) {
    val percent = if (total > 0) (correct * 100f / total) else 0f

    Scaffold(
        topBar = {
            TopBar(setName = setName, onBack = onBack)
        }
    ) { padding ->
        TestResultScreenContent(
            modifier = Modifier.padding(padding),
            correct = correct,
            total = total,
            percent = percent,
            onRestart = onRestart,
            onReview = onReview
        )
    }
}

@Composable
fun TestResultScreenContent(
    modifier: Modifier,
    correct: Int,
    total: Int,
    percent: Float,
    onRestart: () -> Unit,
    onReview: () -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(15.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            Spacer(modifier = Modifier.height(80.dp))

            CustomText(
                text = "Ваш результат",
                style = LocalAppTypography.current.headlineLarge
            )

            Row(
                modifier = Modifier
                    .padding(top = 40.dp)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                PieChart(percent = percent)

                Column(
                    modifier = Modifier
                        .padding(start = 22.dp)
                        .weight(1f)
                ) {
                    ResultBadge(
                        label = "Правильні відповіді",
                        percentage = "${percent.toInt()}%",
                        color = LocalAppColors.current.accentColor
                    )
                    Spacer(modifier = Modifier.height(35.dp))
                    ResultBadge(
                        label = "Неправильні відповіді",
                        percentage = "${(100 - percent.toInt())}%",
                        color = LocalAppColors.current.notificationColor
                    )
                }
            }
        }

        Column {
            CustomButton(
                text = "Пройти знову",
                onClick = onRestart
            )

            Spacer(modifier = Modifier.height(15.dp))

            OutlinedButton(
                onClick = onReview,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(7.dp),
                border = ButtonDefaults.outlinedButtonBorder.copy(
                    brush = SolidColor(LocalAppColors.current.hintTextColor)
                )
            ) {
                CustomText(
                    text = "Повторити матеріал",
                    color = LocalAppColors.current.headerTextColor,
                    style = LocalAppTypography.current.labelLarge
                )
            }
        }

        Spacer(modifier = Modifier.height(100.dp))
    }
}

@Composable
private fun PieChart(percent: Float) {
    val correctAngle = percent * 360f / 100f
    val incorrectAngle = 360f - correctAngle

    val correctColor = LocalAppColors.current.accentColor
    val incorrectColor = LocalAppColors.current.notificationColor

    Canvas(modifier = Modifier.size(95.dp)) {
        drawArc(
            color = incorrectColor,
            startAngle = 0f,
            sweepAngle = incorrectAngle,
            useCenter = true
        )

        drawArc(
            color = correctColor,
            startAngle = incorrectAngle,
            sweepAngle = correctAngle,
            useCenter = true
        )
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TopBar(setName: String, onBack: () -> Unit) {
    TopAppBar(
        title = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(end = 24.dp),
                contentAlignment = Alignment.Center
            ) {
                CustomText(
                    text = setName,
                    style = LocalAppTypography.current.headlineMedium,
                    color = LocalAppColors.current.headerTextColor
                )
            }
        },
        navigationIcon = {
            IconButton(onClick = onBack) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_arrow_back),
                    contentDescription = "Back",
                    modifier = Modifier.size(20.dp)
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = LocalAppColors.current.primaryBackground
        )
    )
}

@Composable
fun ResultBadge(label: String, percentage: String, color: Color) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        CustomText(
            text = label,
            style = LocalAppTypography.current.labelMedium
        )
        Surface(
            modifier = Modifier
                .border(1.dp, color, RoundedCornerShape(5.dp))
                .padding(horizontal = 8.dp, vertical = 4.dp),
            color = Color.Transparent
        ) {
            CustomText(
                text = percentage,
                style = LocalAppTypography.current.labelMedium
            )
        }
    }
}
