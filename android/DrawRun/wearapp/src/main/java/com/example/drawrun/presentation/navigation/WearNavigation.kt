package com.example.drawrun.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.drawrun.presentation.sensors.SensorViewModel
import com.example.drawrun.presentation.ui.HomeScreen
import com.example.drawrun.presentation.ui.RunningStatsScreen
import androidx.navigation.NavHostController

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object RunningStats : Screen("running_stats")
}

@Composable
fun WearNavigation(navController: NavHostController, sensorViewModel: SensorViewModel) {
    NavHost(navController = navController, startDestination = Screen.Home.route) {
        composable(Screen.Home.route) {
            HomeScreen(navController, sensorViewModel)  // 홈 화면 (러닝 통계 버튼 포함)
        }
        composable(Screen.RunningStats.route) {
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
