package com.example.drawrun.ui.navi

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import android.Manifest
import androidx.activity.result.contract.ActivityResultContracts
import com.example.drawrun.R
import com.example.drawrun.databinding.ActivityNaviBinding
import com.example.drawrun.dto.course.PathPoint
import com.google.android.gms.location.LocationServices
import com.mapbox.api.directions.v5.DirectionsCriteria
import com.mapbox.api.directions.v5.models.RouteOptions
import com.mapbox.geojson.LineString
import com.mapbox.geojson.Point
import com.mapbox.maps.CameraOptions
import com.mapbox.maps.EdgeInsets
import com.mapbox.maps.MapboxMap
import com.mapbox.maps.Style
import com.mapbox.maps.extension.localization.localizeLabels
import com.mapbox.maps.plugin.annotation.annotations
import com.mapbox.maps.plugin.annotation.generated.PolylineAnnotationManager
import com.mapbox.maps.plugin.annotation.generated.PolylineAnnotationOptions
import com.mapbox.maps.plugin.annotation.generated.createPolylineAnnotationManager
import com.mapbox.maps.plugin.locationcomponent.location
import com.mapbox.navigation.base.extensions.applyDefaultNavigationOptions
import com.mapbox.navigation.base.options.NavigationOptions
import com.mapbox.navigation.base.route.NavigationRoute
import com.mapbox.navigation.base.route.NavigationRouterCallback
import com.mapbox.navigation.base.route.RouterFailure
import com.mapbox.navigation.core.MapboxNavigation
import com.mapbox.navigation.core.MapboxNavigationProvider
import com.mapbox.navigation.core.trip.session.RouteProgressObserver
import com.mapbox.navigation.ui.maps.route.line.api.MapboxRouteLineApi
import com.mapbox.navigation.ui.maps.route.line.api.MapboxRouteLineView
import com.mapbox.navigation.ui.maps.route.line.model.MapboxRouteLineApiOptions
import com.mapbox.navigation.ui.maps.route.line.model.MapboxRouteLineViewOptions
import com.mapbox.navigation.voice.api.MapboxSpeechApi
import com.mapbox.navigation.voice.api.MapboxVoiceInstructionsPlayer
import com.mapbox.turf.TurfMeasurement
import java.util.Locale

class NaviActivity : AppCompatActivity() {
    private lateinit var binding: ActivityNaviBinding
    private lateinit var mapboxMap: MapboxMap
    private lateinit var mapboxNavigation: MapboxNavigation

    private var polylineAnnotationManager: PolylineAnnotationManager? = null
    private lateinit var routeLineApi: MapboxRouteLineApi
    private lateinit var routeLineView: MapboxRouteLineView

    private lateinit var path: List<PathPoint> // ✅ path를 클래스 변수로 이동
    private lateinit var locationPermissionRequest: androidx.activity.result.ActivityResultLauncher<Array<String>> // ✅ 위치 권한 요청 변수

    private lateinit var speechApi: MapboxSpeechApi
    private lateinit var voiceInstructionsPlayer: MapboxVoiceInstructionsPlayer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNaviBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.progressBar.visibility = View.VISIBLE

        // Mapbox 내비게이션 초기화
        mapboxNavigation = MapboxNavigationProvider.create(
            NavigationOptions.Builder(this.applicationContext).build()
        )

        // 음성 안내 API 초기화
        speechApi = MapboxSpeechApi(
            context = this,
            language = "ko-KR",
        )

        voiceInstructionsPlayer = MapboxVoiceInstructionsPlayer(
            context = this,
            language = "ko-KR"
        )


        // ✅ 전달된 데이터 받기
        path = intent.getParcelableArrayListExtra<PathPoint>("path") ?: emptyList()
        val startLocation = intent.getStringExtra("startLocation") ?: "정보 없음"
        val distance = intent.getDoubleExtra("distance", 0.0)

        val copyAddress = findViewById<TextView>(R.id.copyAddress)
        copyAddress.setOnClickListener {
            val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText("주소", startLocation)
            clipboard.setPrimaryClip(clip)
            Toast.makeText(this, "주소가 복사되었습니다.", Toast.LENGTH_SHORT).show()
        }


        Log.d("pathpath", "$path")

        // ✅ 위치 권한 요청 (클래스 변수로 변경)
        locationPermissionRequest = registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            when {
                permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true -> {
                    moveToPathStart(path)
                }
                else -> {
                    Toast.makeText(this, "위치 권한이 필요합니다.", Toast.LENGTH_SHORT).show()
                }
            }
        }



        // ✅ Mapbox 초기화
        binding.mapView?.let { mapView ->
            mapboxMap = mapView.getMapboxMap()
            mapboxMap.loadStyleUri(Style.DARK) { style ->
                style.localizeLabels(Locale("ko"))
                routeLineApi = MapboxRouteLineApi(MapboxRouteLineApiOptions.Builder().build())
                routeLineView = MapboxRouteLineView(MapboxRouteLineViewOptions.Builder(this).build())

                moveToPathStart(path) // 지도 중심 이동
                requestWalkingRoute(path.map { Point.fromLngLat(it.longitude, it.latitude) }) // 도보 경로 요청

                binding.progressBar.visibility = View.GONE
            }
        }
        checkAndRequestPermissions() // ✅ 위치 권한 요청 실행
        binding.startLocation.text = "$startLocation"
        binding.distance.text = "${distance} km"

        // ✅ startButton 클릭 시 내비게이션 시작
        binding.startButton.setOnClickListener {
            startNavigation(path)
        }

    }

    // ✅ startButton 클릭 시 내비게이션 시작하는 함수
    private fun startNavigation(path: List<PathPoint>) {
        if (path.isEmpty()) {
            Toast.makeText(this, "최소 출발지와 도착지를 선택해주세요.", Toast.LENGTH_SHORT).show()
            return
        }


        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        try {
            if (ActivityCompat.checkSelfPermission(
                    this, Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(
                    this, Manifest.permission.ACCESS_COARSE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
            ) {


                fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                    if (location != null) {
                        // 위치 정보를 가져와 사용
                    } else {
                        Toast.makeText(this, "현재 위치를 가져올 수 없습니다.", Toast.LENGTH_SHORT).show()
                    }
                }.addOnFailureListener {
                    Toast.makeText(this, "위치 정보 오류 발생!", Toast.LENGTH_SHORT).show()
                }
            } else {
                // ❌ 위치 권한이 없을 경우 요청
                requestLocationPermission()
            }
        } catch (e: SecurityException) {
            // 🚨 권한 문제가 발생한 경우 예외 처리
            Log.e("NaviActivity", "위치 권한이 거부됨: ${e.message}")
            Toast.makeText(this, "위치 권한이 필요합니다.", Toast.LENGTH_SHORT).show()
            requestLocationPermission()
        }

        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            if (location != null) {
                val userPoint = Point.fromLngLat(location.longitude,location.latitude)
                val startPoint = Point.fromLngLat(path.first().longitude, path.first().latitude) // 출발지

                // ✅ 로그 추가: 현재 위치 & 출발지 좌표 확인
                Log.d("NAVINAVI", "🟢 현재 위치: ${userPoint.longitude()}, ${userPoint.latitude()}")
                Log.d("NAVINAVI", "🔴 출발지 위치: ${startPoint.longitude()}, ${startPoint.latitude()}")

                val distance = TurfMeasurement.distance(userPoint, startPoint, "meters")
                Log.d("NAVINAVI", "📏 현재 위치와 출발지 거리: $distance meters")




                if (distance > 20) {
                    Toast.makeText(this, "출발지로 이동 후 시작해주세요.", Toast.LENGTH_SHORT).show()
                } else {
                    // ✅ 내비게이션 시작
                    val points = path.map { Point.fromLngLat(it.longitude, it.latitude) }

                    // ✅ 디버깅용 로그 추가 (요청될 경로 좌표 확인)
                    points.forEachIndexed { index, point ->
                        Log.d("NAVINAVI", "📍 요청 좌표 #$index -> longitude: ${point.longitude()}, latitude: ${point.latitude()}")
                    }

                    mapboxNavigation.requestRoutes(
                        RouteOptions.builder()
                            .applyDefaultNavigationOptions()
                            .profile(DirectionsCriteria.PROFILE_WALKING)
                            .language("ko")
                            .steps(true)
                            .voiceUnits(DirectionsCriteria.METRIC)
                            .coordinatesList(points) // 경로 설정
                            .voiceInstructions(true)  // ✅ 음성 안내 활성화!
                            .waypointIndicesList((0 until path.size).toList())
                            .waypointNamesList(
                                List(path.size) { index ->
                                    when (index) {
                                        0 -> "출발지"
                                        path.size - 1 -> "도착지"
                                        else -> "경유지 $index"
                                    }
                                }
                            )
                            .build(),

                        object : NavigationRouterCallback {
                            override fun onRoutesReady(routes: List<NavigationRoute>, routerOrigin: String) {
                                val route = routes.firstOrNull()

                                if (route != null) {
                                    mapboxNavigation.startTripSession()
                                    mapboxNavigation.setNavigationRoutes(listOf(route))
                                    mapboxNavigation.registerRouteProgressObserver(routeProgressObserver)

                                    // 🚶‍♂️ 내비게이션 시작 시 지도 줌 설정
                                    binding.mapView.getMapboxMap().setCamera(
                                        CameraOptions.Builder()
                                            .center(startPoint)
                                            .zoom(17.0) // 도보 모드에 적절한 줌 레벨
                                            .build()
                                    )

                                    Toast.makeText(this@NaviActivity, "내비게이션 시작!", Toast.LENGTH_SHORT).show()
                                } else {
                                    Toast.makeText(this@NaviActivity, "경로 생성 실패!", Toast.LENGTH_SHORT).show()
                                }
                            }

                            override fun onFailure(reasons: List<RouterFailure>, routeOptions: RouteOptions) {
                                Toast.makeText(this@NaviActivity, "경로 요청 실패!", Toast.LENGTH_SHORT).show()
                            }

                            override fun onCanceled(routeOptions: RouteOptions, routerOrigin: String) {}
                        }
                    )
                }
            } else {
                Toast.makeText(this, "현재 위치를 가져올 수 없습니다.", Toast.LENGTH_SHORT).show()
            }
        }.addOnFailureListener {
            Toast.makeText(this, "위치 정보 오류 발생!", Toast.LENGTH_SHORT).show()
        }
    }



    /// 위치 권한 확인 및 요청
    private fun checkAndRequestPermissions() {
        if (checkLocationPermission()) {
            moveToPathStart(path)
        } else {
            requestLocationPermission()
        }
    }

    // 위치 권한 확인
    private fun checkLocationPermission(): Boolean {
        return ActivityCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    // 위치 권한 요청
    private fun requestLocationPermission() {
        locationPermissionRequest.launch(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        )
    }

    // ✅ 출발지로 지도 중심 이동
    private fun moveToPathStart(path: List<PathPoint>) {
        if (path.isNotEmpty()) {
            val startPoint = Point.fromLngLat(path.first().longitude, path.first().latitude)

            mapboxMap.setCamera(
                CameraOptions.Builder()
                    .center(startPoint)
                    .zoom(15.0)
                    .build()
            )

            Log.d("NaviActivity", "Moving to start point: ${path.first().longitude}, ${path.first().latitude}")
        }
    }

    private val routeProgressObserver = RouteProgressObserver { routeProgress ->
        val distanceRemaining = routeProgress.distanceRemaining
        val durationRemaining = routeProgress.durationRemaining
        Log.d("NAVINAVI", "남은 거리: $distanceRemaining, 남은 시간: $durationRemaining")
    }

    // ✅ 도보 경로 요청
    private fun requestWalkingRoute(path: List<Point>) {
        if (path.size < 2) {
            Log.e("NaviActivity", "경로 요청 실패: 최소 2개 이상의 좌표가 필요합니다.")
            return
        }

        mapboxNavigation.requestRoutes(
            RouteOptions.builder()
                .applyDefaultNavigationOptions()
                .profile(DirectionsCriteria.PROFILE_WALKING)
                .language("ko")
                .steps(true)
                .voiceUnits(DirectionsCriteria.METRIC)
                .coordinatesList(path)
                .waypointIndicesList((0 until path.size).toList())
                .waypointNamesList(
                    List(path.size) { index ->
                        when (index) {
                            0 -> "출발지"
                            path.size - 1 -> "도착지"
                            else -> "경유지 $index"
                        }
                    }
                )
                .build(),
            object : NavigationRouterCallback {
                override fun onRoutesReady(routes: List<NavigationRoute>, routerOrigin: String) {
                    routes.firstOrNull()?.let { route ->
                        routeLineApi.setNavigationRoutes(listOf(route)) { value ->
                            binding.mapView.getMapboxMap().getStyle()?.apply {
                                routeLineView.renderRouteDrawData(this, value)
                            }
                        }
                        Log.d("NaviActivity", "도보 경로가 성공적으로 생성되었습니다.")
                    }
                }

                override fun onFailure(reasons: List<RouterFailure>, routeOptions: RouteOptions) {
                    Log.e("NaviActivity", "경로 요청 실패: $reasons")
                }

                override fun onCanceled(routeOptions: RouteOptions, routerOrigin: String) {}
            }
        )
    }
}
