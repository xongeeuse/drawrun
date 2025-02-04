package com.example.drawrun.presentation.ui

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.wear.compose.material.Button
import androidx.wear.compose.material.Text
import com.example.drawrun.presentation.navigation.Screen
import com.example.drawrun.presentation.sensors.SensorViewModel

@Composable
fun HomeScreen(navController: NavController, viewModel: SensorViewModel) {
    var isRunning by remember { mutableStateOf(false) }
    val elapsedTime by viewModel.elapsedTime.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "경과 시간: $elapsedTime 초")

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                isRunning = true
                viewModel.startMeasurement()
            },
            enabled = !isRunning,
            modifier = Modifier
                .width(100.dp)   // 버튼 가로 크기 축소
                .height(36.dp)                      // 버튼 높이 설정
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
                .width(100.dp)   // 버튼 가로 크기 축소
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

