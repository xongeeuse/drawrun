package com.example.drawrun.presentation

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.compose.rememberNavController
import com.example.drawrun.presentation.navigation.WearNavHost
import com.example.drawrun.presentation.sensors.SensorManagerHelper
import com.example.drawrun.presentation.sensors.SensorViewModel
import com.example.drawrun.presentation.sensors.SensorViewModelFactory
import com.example.drawrun.presentation.theme.DrawRunTheme

class MainActivity : ComponentActivity() {

    companion object {
        private const val REQUEST_CODE_PERMISSIONS = 1001
    }

    // 전역필드로 선언안하면 빨간줄 개락 뜸..
    private lateinit var sensorManagerHelper: SensorManagerHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // SensorManagerHelper 및 ViewModel 초기화
        val sensorManagerHelper = SensorManagerHelper(this)
        val sensorViewModelFactory = SensorViewModelFactory(sensorManagerHelper)
        val sensorViewModel = ViewModelProvider(this, sensorViewModelFactory)[SensorViewModel::class.java]

        // 권한 요청 수행
        requestPermissionsIfNeeded(sensorManagerHelper)

        // Compose UI 설정
        setContent {
            val navController = rememberNavController()
            DrawRunTheme {
                WearNavHost(navController = navController, sensorViewModel)
            }
        }
    }


    /**
     * 필수 권한 체크 및 요청 함수
     */
    private fun requestPermissionsIfNeeded(sensorManagerHelper: SensorManagerHelper) {
        val permissionsToRequest = mutableListOf<String>()

        // 권한 확인 (BODY_SENSORS, ACCESS_FINE_LOCATION)
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.BODY_SENSORS) != PackageManager.PERMISSION_GRANTED) {
            permissionsToRequest.add(Manifest.permission.BODY_SENSORS)
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            permissionsToRequest.add(Manifest.permission.ACCESS_FINE_LOCATION)
        }

        // 요청할 권한이 있을 경우 요청 수행
        if (permissionsToRequest.isNotEmpty()) {
            ActivityCompat.requestPermissions(this, permissionsToRequest.toTypedArray(), REQUEST_CODE_PERMISSIONS)
        } else {
            // 이미 권한이 허용된 경우 센서 시작
            sensorManagerHelper.startSensors()
        }
    }

    /**
     * 권한 요청 결과 처리 함수
     */
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (grantResults.isNotEmpty() && grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
                Toast.makeText(this, "권한이 승인되었습니다.", Toast.LENGTH_SHORT).show()
                // 권한 승인 후 센서 업데이트 시작
                val sensorManagerHelper = SensorManagerHelper(this)
                sensorManagerHelper.startSensors()
            } else {
                Toast.makeText(this, "필수 권한이 거부되었습니다.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    /**
     * 액티비티 상태 변화에 따라 센서 시작 및 정지
     */
    override fun onResume() {
        super.onResume()
        if (::sensorManagerHelper.isInitialized) {
            sensorManagerHelper.startSensors()
        } else {
            Log.e("MainActivity", "sensorManagerHelper가 초기화되지 않았습니다.")
        }
    }

    override fun onPause() {
        super.onPause()
        if (::sensorManagerHelper.isInitialized) {
            sensorManagerHelper.stopSensors()
        }
    }


}
