package com.example.drawrun.ui.map

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import com.mapbox.geojson.LineString
import com.mapbox.geojson.Point
import com.mapbox.maps.CameraOptions
import com.mapbox.maps.MapView
import com.mapbox.maps.Style
import com.mapbox.maps.extension.localization.localizeLabels
import com.mapbox.maps.plugin.annotation.annotations
import com.mapbox.maps.plugin.annotation.generated.PolylineAnnotationManager
import com.mapbox.maps.plugin.annotation.generated.PolylineAnnotationOptions
import com.mapbox.maps.plugin.annotation.generated.createPolylineAnnotationManager
import com.mapbox.maps.plugin.gestures.gestures
import com.mapbox.maps.plugin.locationcomponent.OnIndicatorPositionChangedListener
import com.mapbox.maps.plugin.locationcomponent.location
import com.mapbox.navigation.core.MapboxNavigation
import com.mapbox.navigation.ui.maps.route.line.api.MapboxRouteLineApi
import com.mapbox.navigation.ui.maps.route.line.api.MapboxRouteLineView
import java.util.Locale

class MapActivity : ComponentActivity() {

    private lateinit var mapView: MapView // MapView 객체 선언
    private lateinit var polylineAnnotationManager: PolylineAnnotationManager // Polyline 관리 객체
    private lateinit var mapboxNavigation: MapboxNavigation
    private lateinit var routeLineApi: MapboxRouteLineApi
    private lateinit var routeLineView: MapboxRouteLineView

    private val points = mutableListOf<Point>() // 사용자가 클릭한 좌표를 저장할 리스트

    // 위치 권한 요청 런처 설정
    private val locationPermissionRequest = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        when {
            permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true -> {
                initializeMap() // 권한이 승인되면 지도 초기화 실행
            }
            else -> {
                Toast.makeText(this, "위치 권한이 필요합니다.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        checkAndRequestPermissions() // 권한 확인 및 요청 처리 시작
    }

    override fun onDestroy() {
        super.onDestroy()
        mapView.onDestroy() // MapView 리소스 해제
    }

    // 위치 권한 확인 및 요청 처리 함수 -------------------------------------
    private fun checkAndRequestPermissions() {
        if (checkLocationPermission()) {
            initializeMap()
        } else {
            requestLocationPermission()
        }
    }

    private fun checkLocationPermission(): Boolean {
        return ActivityCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestLocationPermission() {
        locationPermissionRequest.launch(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        )
    }

    // 지도 초기화 및 설정 -------------------------------------------------
    @SuppressLint("MissingPermission")
    private fun initializeMap() {
        mapView = MapView(this)
        setContentView(mapView)

        // loadStyleUri(Style.MAPBOX_STREETS)
        // loadStyleUri(Style.DARK)
        // loadStyleUri("mapbox://styles/mapbox/navigation-night-v1")

        mapView.getMapboxMap().loadStyleUri(Style.DARK) { style ->
            style.localizeLabels(Locale("ko"))

            mapView.location.updateSettings {
                enabled = true
                pulsingEnabled = true
            }

            // 1. 리스너 객체를 변수에 저장
            val listener = object : OnIndicatorPositionChangedListener {
                override fun onIndicatorPositionChanged(point: Point) {
                    mapView.getMapboxMap().setCamera(
                        CameraOptions.Builder()
                            .center(point)
                            .zoom(15.0)
                            .build()
                    )
                    // 2. 저장된 리스너 객체를 사용하여 제거
                    mapView.location.removeOnIndicatorPositionChangedListener(this)
                }
            }

            // 3. 리스너 등록
            mapView.location.addOnIndicatorPositionChangedListener(listener)

            polylineAnnotationManager = mapView.annotations.createPolylineAnnotationManager()
            mapView.gestures.addOnMapClickListener { point ->
                handleMapClick(point)
                true
            }
        }
    }



    // 지도 클릭 이벤트 처리 -----------------------------------------------
    private fun handleMapClick(point: Point) {
        points.add(point) // 클릭한 좌표를 리스트에 추가

        // 로그 출력 추가
        Log.d("MAP_CLICK", "새 좌표 추가: (${point.latitude()}, ${point.longitude()})")
        Log.d("MAP_CLICK", "현재 저장된 좌표 개수: ${points.size}, 좌표 목록: ${points}")

        if (points.size > 1) { // 두 개 이상의 좌표가 있을 때만 라인 생성 가능
            drawLine(points) // 라인 그리기 호출
        }

        Toast.makeText(this, "좌표 추가: ${point.latitude()}, ${point.longitude()}", Toast.LENGTH_SHORT).show()
    }

    // 라인 그리기 ---------------------------------------------------------
    private fun drawLine(points: List<Point>) {
        val polylineOptions = PolylineAnnotationOptions()
            .withPoints(points) // LineString 대신 List<Point> 사용
            .withLineColor("#FF0000") // 라인 색상 (빨간색)
            .withLineWidth(4.0) // 라인 두께

        polylineAnnotationManager.deleteAll() // 기존 라인 삭제 (중복 방지)
        polylineAnnotationManager.create(polylineOptions) // 새로운 라인 생성 및 지도에 추가
    }
}