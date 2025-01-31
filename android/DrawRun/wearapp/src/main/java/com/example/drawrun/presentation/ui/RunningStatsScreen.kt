package com.example.drawrun.presentation.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.Button
import com.example.drawrun.presentation.sensors.SensorViewModel
import kotlinx.coroutines.delay

@Composable
fun RunningStatsScreen(viewModel: SensorViewModel, navigateToHome: () -> Unit) {
    val heartRate by viewModel.heartRate.collectAsState()
    val cadence by viewModel.cadence.collectAsState()
    val pace by viewModel.pace.collectAsState()
    val elapsedTime by viewModel.elapsedTime.collectAsState()
    val isRunning by viewModel.isRunning.collectAsState()

    // 타이머 동작을 위한 LaunchedEffect
    LaunchedEffect(isRunning) {
        while (isRunning) {
            viewModel.incrementElapsedTime()
            delay(1000L) // 1초마다 경과 시간 증가
            viewModel.updatePaceAndCadence()
        }
    }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "심박수: ${heartRate ?: "데이터 없음"} bpm")
        Spacer(modifier = Modifier.height(8.dp))

        Text(text = "경과 시간: $elapsedTime 초")
        Spacer(modifier = Modifier.height(8.dp))

        Text(text = "케이던스: ${cadence?.let { "%.2f".format(it) } ?: "데이터 없음"} SPM")
        Spacer(modifier = Modifier.height(8.dp))

        Text(text = "페이스: ${pace?.let { "%.2f".format(it) } ?: "계산 불가"} 분/km")
        Spacer(modifier = Modifier.height(8.dp))

        // 소모 칼로리 계산 및 표시
        val weightKg = 50f  // 사용자 입력 필요 (예시 체중)
        val age = 25        // 사용자 입력 필요 (예시 나이)
        val averageHeartRate = heartRate ?: 0f
        val caloriesBurned = viewModel.calculateCalories(elapsedTime, averageHeartRate, weightKg, age)

        Text(text = "소모 칼로리: ${"%.2f".format(caloriesBurned)} kcal")

        // 홈으로 돌아가는 버튼
        Button(onClick = {
            viewModel.stopMeasurement()  // 측정 중단
            viewModel.resetMeasurement() // 데이터 초기화
            navigateToHome()             // 홈 화면으로 이동
        }) {
            Text(text = "Home")
        }
    }
}
