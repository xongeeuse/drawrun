package com.example.drawrun.presentation

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import com.example.drawrun.presentation.ui.NavigationScreen

class NavigationActivity : ComponentActivity() {

    private val dataViewModel: DataViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Log.d("NavigationActivity", "registerReceiver 호출 직전")
        registerReceiver(
            navigationUpdateReceiver,
            IntentFilter("com.example.drawrun.presentation.NAVIGATION_UPDATE"),
            Context.RECEIVER_EXPORTED
        )
        Log.d("NavigationActivity", "registerReceiver 호출 완료")
        setContent {
            NavigationScreen(dataViewModel)
        }
    }

    override fun onResume() {
        super.onResume()
        Log.d("NavigationActivity", "onResume 호출됨 - Activity 활성 상태")
        dataViewModel.forceRefresh()
    }

    private val navigationUpdateReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val action = intent.action
            Log.d("NavigationReceiver", "onReceive 호출됨 - 인텐트 액션: $action")
            val distanceToNextTurn = intent.getDoubleExtra("distanceToNextTurn", 0.0)
            val voiceInstruction = intent.getStringExtra("voiceInstruction") ?: "안내 없음"
            val totalDistance = intent.getDoubleExtra("totalDistance", 0.0)
            val distanceRemaining = intent.getDoubleExtra("distanceRemaining", 0.0)

            Log.d(
                "NavigationReceiver",
                "onReceive 호출됨 - distanceToNextTurn=$distanceToNextTurn, voiceInstruction=$voiceInstruction, " +
                        "totalDistance=$totalDistance, distanceRemaining=$distanceRemaining"
            )

            dataViewModel.updateData(distanceToNextTurn, voiceInstruction, totalDistance, distanceRemaining)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(navigationUpdateReceiver)
    }
}
