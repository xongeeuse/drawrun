package com.example.drawrun.presentation.ui

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.drawrun.presentation.navigation.Screen
import com.example.drawrun.presentation.sensors.SensorViewModel

@Composable
fun HomeScreen(navController: NavController, viewModel: SensorViewModel) {
    var isRunning by remember { mutableStateOf(false) }
    val elapsedTime by viewModel.elapsedTime.collectAsState()
    val heartRate by viewModel.heartRate.collectAsState()
    val stepCount by viewModel.stepCount.collectAsState()
    val totalDistance by viewModel.totalDistance.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),  // 스크롤 가능하게 설정
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "경과 시간: $elapsedTime 초")
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = "현재 심박수: ${heartRate ?: "데이터 없음"} bpm")
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = "걸음 수: $stepCount 걸음").also {
            Log.d("HomeScreen", "Current step count : $stepCount")
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = "이동 거리: ${"%.2f".format(totalDistance)} m")
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = {
                isRunning = true
                viewModel.startMeasurement()
            },
            enabled = !isRunning,
            modifier = Modifier
                .width(100.dp)
                .height(36.dp)
        ) {
            Text(text = "Start")
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = {
                isRunning = false
                viewModel.stopMeasurement()
            },
            enabled = isRunning,
            modifier = Modifier
                .width(100.dp)
                .height(36.dp)
        ) {
            Text(text = "Stop")
        }

        Spacer(modifier = Modifier.height(12.dp))

        Button(
            onClick = { navController.navigate(Screen.RunningStats.route) },
            modifier = Modifier
                .width(100.dp)
                .height(36.dp)
        ) {
            Text(text = "러닝 통계")
        }
    }
}

