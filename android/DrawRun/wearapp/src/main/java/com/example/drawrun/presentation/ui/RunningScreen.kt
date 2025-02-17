package com.example.drawrun.presentation.ui

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Scaffold
import androidx.wear.compose.material.Text
import androidx.wear.compose.material.TimeText
import com.example.drawrun.presentation.theme.pretendard
import kotlinx.coroutines.delay

@Composable
fun RunningScreen(onCountdownFinished: () -> Unit) {
    var countdownValue by remember { mutableStateOf(3) }

    Scaffold(
        timeText = { TimeText() },
        modifier = Modifier.fillMaxSize()
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "$countdownValue",
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.body1.copy(
                    fontFamily = pretendard,
                    fontSize = 80.sp,
                    color = Color(0xFF00FFAE)
                )
            )
        }

    }
    LaunchedEffect(Unit) {
        while (countdownValue > 0) {
            delay(1000L)  // 1초 대기
            countdownValue -= 1
        }
        onCountdownFinished()  // 카운트다운 종료 후 이동 콜백 호출
    }

}