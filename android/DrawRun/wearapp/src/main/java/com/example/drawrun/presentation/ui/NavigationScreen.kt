package com.example.drawrun.presentation.ui

import android.util.Log
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
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
import com.example.drawrun.R
import com.example.drawrun.presentation.DataViewModel
import com.example.drawrun.presentation.sensors.SensorViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun NavigationScreen(
    dataViewModel: DataViewModel,
    sensorViewModel: SensorViewModel,
    onNavigateToMain: () -> Unit
) {
    // ✅ UI 상태 업데이트 감지
    val updateTrigger by dataViewModel.updateTrigger.collectAsState(initial = false)

    // ✅ collectAsState()를 사용하여 UI 업데이트 감지
    val distanceToNextTurn by dataViewModel.distanceToNextTurn.collectAsState()
    val voiceInstruction by dataViewModel.voiceInstruction.collectAsState()
    val totalDistance by dataViewModel.totalDistance.collectAsState()
    val distanceRemaining by dataViewModel.distanceRemaining.collectAsState()
    val isDestinationReached by dataViewModel.isDestinationReached.collectAsState(initial = false)

    // ✅ 심박수 데이터 업데이트 감지
    val heartRate by sensorViewModel.heartRate.collectAsState()
    val averageHeartRate = sensorViewModel.getAverageHeartRate()

    Log.d("NavigationScreen", "🔥 UI 업데이트: distanceToNextTurn=$distanceToNextTurn, voiceInstruction=$voiceInstruction, heartRate=$heartRate")

    // ✅ UI 강제 리렌더링 (데이터 변경 감지)
    LaunchedEffect(updateTrigger) {
        Log.d("NavigationScreen", "🟢 데이터 변경 감지됨: UI 리렌더링 중...")
    }

    // ✅ 진행 상태 계산 (거리 기반)
    val progressPercentage = if (totalDistance > 0) {
        ((totalDistance - distanceRemaining) / totalDistance).toFloat()
    } else {
        0f
    }

    // ✅ 회전 방향 아이콘 설정
    val turnDirection = when {
        voiceInstruction.contains("왼쪽회전") -> "left"
        voiceInstruction.contains("오른쪽회전") -> "right"
        else -> "straight"
    }
    val iconRes = when (turnDirection) {
        "left" -> R.drawable.go_left_icon
        "right" -> R.drawable.go_right_icon
        else -> R.drawable.go_straight_icon
    }

    // ✅ 애니메이션 진행 상태
    val animatedProgress = remember { Animatable(0f) }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(progressPercentage) {
        coroutineScope.launch {
            if (!progressPercentage.isNaN()) {
                animatedProgress.animateTo(progressPercentage, animationSpec = tween(durationMillis = 1000))
            } else {
                Log.e("NavigationScreen", "❌ Invalid progress percentage (NaN detected)")
            }
        }
    }

    // ✅ UI 레이아웃
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        if (isDestinationReached) {
            // ✅ 목적지 도착 UI
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(text = "🎉 목적지에 도착했습니다!", fontSize = 20.sp, textAlign = TextAlign.Center)
                Spacer(modifier = Modifier.height(10.dp))
                Text(text = "평균 심박수: ${averageHeartRate ?: "N/A"} BPM", fontSize = 18.sp)
            }

            LaunchedEffect(Unit) {
                delay(4000)
                onNavigateToMain()
            }
        } else {
            // ✅ 원형 진행 바 UI
            Canvas(modifier = Modifier.fillMaxSize()) {
                val ringThickness = 12.dp.toPx()
                val paddingOffset = 0f

                drawArc(
                    color = Color.White,
                    startAngle = -90f,
                    sweepAngle = 360f,
                    useCenter = false,
                    style = Stroke(width = ringThickness, cap = StrokeCap.Round),
                    topLeft = androidx.compose.ui.geometry.Offset(paddingOffset, paddingOffset),
                    size = size
                )

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

            // ✅ 네비게이션 안내 UI
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    painter = painterResource(id = iconRes),
                    contentDescription = null,
                    modifier = Modifier.size(90.dp)
                )
                Spacer(modifier = Modifier.height(5.dp))
                Text(text = "${distanceToNextTurn.toInt()}m", fontSize = 20.sp)
//                Text(text = voiceInstruction, fontSize = 18.sp, textAlign = TextAlign.Center)  // ✅ 안내문구 표시
                Text(text = "현재 심박수: ${heartRate ?: "0"} BPM", fontSize = 12.sp)
            }
        }
    }
}

