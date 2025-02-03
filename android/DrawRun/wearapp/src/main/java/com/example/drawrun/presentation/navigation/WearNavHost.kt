package com.example.drawrun.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.drawrun.presentation.sensors.SensorViewModel
import com.example.drawrun.presentation.ui.HomeScreen
import com.example.drawrun.presentation.ui.RunningStatsScreen

sealed class AppScreen(val route: String) {
    object Home : AppScreen("home")
    object RunningStats : Screen("running_stats")
}

@Composable
fun WearNavHost(navController: NavHostController, sensorViewModel: SensorViewModel) {
    NavHost(navController = navController, startDestination = Screen.Home.route) {
        composable(route = Screen.Home.route) {
            HomeScreen(navController, sensorViewModel)
        }
        // 러닝 통계 화면
        composable(route = Screen.RunningStats.route) {
            RunningStatsScreen(
                viewModel = sensorViewModel,
                navigateToHome = {
                    // 네비게이션을 통해 홈 화면으로 이동
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.RunningStats.route) { inclusive = true }
                    }
                }
            )
        }
    }
}

