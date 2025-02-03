package com.example.drawrun.presentation
import android.net.Uri
import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.PowerManager
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
import com.example.drawrun.presentation.sensors.SensorTrackingService
import com.example.drawrun.presentation.sensors.SensorViewModel
import com.example.drawrun.presentation.sensors.SensorViewModelFactory
import com.example.drawrun.presentation.theme.DrawRunTheme

class MainActivity : ComponentActivity() {

    companion object {
        private const val REQUEST_CODE_PERMISSIONS = 1001
    }

    private lateinit var sensorManagerHelper: SensorManagerHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requestIgnoreBatteryOptimizations()
        checkAndRequestPermissions()
        // 센서 시작은 권한 승인 후 수행
        if (hasAllPermissions()) {
            startForegroundService(Intent(this, SensorTrackingService::class.java))
        }

        // Foreground Service 시작
        val serviceIntent = Intent(this, SensorTrackingService::class.java)
        startForegroundService(serviceIntent)

        // SensorManagerHelper 및 ViewModel 초기화
        sensorManagerHelper = SensorManagerHelper(this)
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

    /**
     * 필수 권한 체크 및 요청
     */
    private fun checkAndRequestPermissions() {
        val permissions = arrayOf(
            Manifest.permission.ACTIVITY_RECOGNITION,
            Manifest.permission.BODY_SENSORS,
            Manifest.permission.ACCESS_FINE_LOCATION
        )
        val missingPermissions = permissions.filter {
            ContextCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED
        }

        if (missingPermissions.isNotEmpty()) {
            ActivityCompat.requestPermissions(this, missingPermissions.toTypedArray(), REQUEST_CODE_PERMISSIONS)
        }
    }

    private fun requestPermissionsIfNeeded(sensorManagerHelper: SensorManagerHelper) {
        val permissionsToRequest = mutableListOf(
            Manifest.permission.ACTIVITY_RECOGNITION,
            Manifest.permission.BODY_SENSORS,
            Manifest.permission.ACCESS_FINE_LOCATION
        ).filter {
            ContextCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED
        }

        if (permissionsToRequest.isNotEmpty()) {
            ActivityCompat.requestPermissions(this, permissionsToRequest.toTypedArray(), REQUEST_CODE_PERMISSIONS)
        } else {
            sensorManagerHelper.startSensors()
        }
    }


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (grantResults.isNotEmpty() && grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
                Toast.makeText(this, "권한이 승인되었습니다.", Toast.LENGTH_SHORT).show()
                startForegroundService(Intent(this, SensorTrackingService::class.java))
                sensorManagerHelper.startSensors()
            } else {
                Toast.makeText(this, "필수 권한이 거부되었습니다.", Toast.LENGTH_SHORT).show()
            }
        }
    }


    /**
     * 배터리 최적화 예외 요청
     */
    private fun requestIgnoreBatteryOptimizations() {
        val packageName = packageName

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val pm = getSystemService(Context.POWER_SERVICE) as PowerManager
            if (!pm.isIgnoringBatteryOptimizations(packageName)) {
                val intent = Intent(android.provider.Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS)
                // Uri 경로에 충돌이 있으므로 명시적으로 `android.net.Uri`를 사용합니다.
                intent.data = android.net.Uri.parse("package:$packageName")
                startActivity(intent)
            }
        }
    }

    private fun hasAllPermissions(): Boolean {
        val permissions = arrayOf(
            Manifest.permission.ACTIVITY_RECOGNITION,
            Manifest.permission.BODY_SENSORS,
            Manifest.permission.ACCESS_FINE_LOCATION
        )
        return permissions.all {
            ContextCompat.checkSelfPermission(this, it) == PackageManager.PERMISSION_GRANTED
        }
    }
}
