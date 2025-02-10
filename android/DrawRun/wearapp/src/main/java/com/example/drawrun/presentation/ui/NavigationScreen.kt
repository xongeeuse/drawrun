package com.example.drawrun.presentation.ui

import android.util.Log
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.wear.compose.material.Text
import com.example.drawrun.presentation.DataViewModel
import kotlinx.coroutines.launch

@Composable
fun NavigationScreen(dataViewModel: DataViewModel) {
    val distanceToNextTurn by dataViewModel.distanceToNextTurn.collectAsState()
    val voiceInstruction by dataViewModel.voiceInstruction.collectAsState()
    val totalDistance by dataViewModel.totalDistance.collectAsState()
    val distanceRemaining by dataViewModel.distanceRemaining.collectAsState()
    val isDestinationReached by dataViewModel.isDestinationReached.observeAsState(false)

    Log.d("NavigationScreen", "UI 업데이트: distanceToNextTurn=$distanceToNextTurn, voiceInstruction=$voiceInstruction")

    // 남은 거리에 따라 진행률 계산
    val progressPercentage = if (totalDistance > 0) {
        ((totalDistance - distanceRemaining) / totalDistance).toFloat()
    } else {
        0f
    }

    val animatedProgress = remember { Animatable(0f) }
    val coroutineScope = rememberCoroutineScope()

    // 진행률 애니메이션 업데이트
    LaunchedEffect(progressPercentage) {
        coroutineScope.launch {
            if (!progressPercentage.isNaN()) {
                animatedProgress.animateTo(progressPercentage, animationSpec = tween(durationMillis = 1000))
            } else {
                Log.e("WatchData", "Invalid progress percentage (NaN detected)")
            }
        }
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        if (isDestinationReached) {
            // 목적지에 도착했을 때 표시할 UI
            Text(
                text = "목적지에 도착했습니다!",
                fontSize = 24.sp,
                textAlign = TextAlign.Center
            )
        } else {
            // 진행 중인 네비게이션 UI
            Canvas(
                modifier = Modifier.fillMaxSize()
            ) {
                val ringThickness = 12.dp.toPx()
                val paddingOffset = 0f

                // 흰색 배경 링
                drawArc(
                    color = Color.White,
                    startAngle = -90f,
                    sweepAngle = 360f,
                    useCenter = false,
                    style = Stroke(width = ringThickness, cap = StrokeCap.Round),
                    topLeft = androidx.compose.ui.geometry.Offset(paddingOffset, paddingOffset),
                    size = size
                )

                // 초록색 진행 링
                drawArc(
                    color = Color.Green,
                    startAngle = -90f,
                    sweepAngle = animatedProgress.value * 360f,
                    useCenter = false,
                    style = Stroke(width = ringThickness, cap = StrokeCap.Round),
                    topLeft = androidx.compose.ui.geometry.Offset(paddingOffset, paddingOffset),
                    size = size
                )
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = voiceInstruction,
                    fontSize = 18.sp,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "${(totalDistance - distanceRemaining).toInt()}m 이동",
                    fontSize = 16.sp
                )
            }
        }
    }
}
