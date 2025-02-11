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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.wear.compose.material.Icon
import androidx.wear.compose.material.Text
import com.example.drawrun.presentation.DataViewModel
import com.example.drawrun.presentation.sensors.SensorViewModel
import kotlinx.coroutines.launch
import com.example.drawrun.R
import kotlinx.coroutines.delay

@Composable
fun NavigationScreen(dataViewModel: DataViewModel, sensorViewModel: SensorViewModel, onNavigateToMain: () -> Unit) {
    val distanceToNextTurn by dataViewModel.distanceToNextTurn.collectAsState()
    val voiceInstruction by dataViewModel.voiceInstruction.collectAsState()
    val totalDistance by dataViewModel.totalDistance.collectAsState()
    val distanceRemaining by dataViewModel.distanceRemaining.collectAsState()
    val isDestinationReached by dataViewModel.isDestinationReached.collectAsState(initial = false)


    Log.d("NavigationScreen", "UI 업데이트: distanceToNextTurn=$distanceToNextTurn, voiceInstruction=$voiceInstruction")

    // 남은 거리에 따라 진행률 계산
    val progressPercentage = if (totalDistance > 0) {
        ((totalDistance - distanceRemaining) / totalDistance).toFloat()
    } else {
        0f
    }

    // 회전 방향 추출
    val turnDirection = when {
        voiceInstruction.contains("왼쪽회전") -> "left"
        voiceInstruction.contains("오른쪽회전") -> "right"
        else -> "straight"
    }

    // 아이콘 리소스 설정
    val iconRes = when (turnDirection) {
        "left" -> R.drawable.go_left_icon
        "right" -> R.drawable.go_right_icon
        else -> R.drawable.go_straight_icon
    }

    val animatedProgress = remember { Animatable(0f) }
    val coroutineScope = rememberCoroutineScope()
    val averageHeartRate = sensorViewModel.getAverageHeartRate()  // 평균 심박수 가져오기
    val heartRate by sensorViewModel.heartRate.collectAsState()

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
                fontSize = 20.sp,
                textAlign = TextAlign.Center
            )
            Text(text = "평균 심박수: ${averageHeartRate ?: "N/A"} BPM", fontSize = 24.sp)
            // 4초 뒤 화면 전환
            LaunchedEffect(Unit) {
                delay(4000)
                onNavigateToMain()
            }

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

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    painter = painterResource(id = iconRes),
                    contentDescription = null,
                    modifier = Modifier.size(100.dp)
                )
                Spacer(modifier = Modifier.height(5.dp))
                Text(text = "${distanceToNextTurn.toInt()}m", fontSize = 25.sp)
//                Text(
//                    text = voiceInstruction,
//                    fontSize = 18.sp,
//                    textAlign = TextAlign.Center
//                )
                Text(text = "현재 심박수: ${heartRate ?: "N/A"} BPM", fontSize = 10.sp)
//                Spacer(modifier = Modifier.height(8.dp))
//                Text(
//                    text = "${(totalDistance - distanceRemaining).toInt()}m 이동",
//                    fontSize = 16.sp
//                )
            }
        }
    }
}

