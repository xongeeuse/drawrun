package com.example.drawrun.ui.map

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.example.drawrun.R
import com.example.drawrun.data.model.ParcelablePoint
import com.example.drawrun.ui.map.fragment.CourseCompleteBottomSheet
import com.mapbox.api.directions.v5.DirectionsCriteria
import com.mapbox.api.directions.v5.models.RouteOptions
import com.mapbox.bindgen.Expected
import com.mapbox.common.location.Location
import com.mapbox.geojson.Point
import com.mapbox.maps.CameraOptions
import com.mapbox.maps.ImageHolder
import com.mapbox.maps.MapView
import com.mapbox.maps.Style
import com.mapbox.maps.extension.localization.localizeLabels
import com.mapbox.maps.plugin.LocationPuck2D
import com.mapbox.maps.plugin.animation.camera
import com.mapbox.maps.plugin.annotation.annotations
import com.mapbox.maps.plugin.annotation.generated.PolylineAnnotationManager
import com.mapbox.maps.plugin.annotation.generated.PolylineAnnotationOptions
import com.mapbox.maps.plugin.annotation.generated.createPolylineAnnotationManager
import com.mapbox.maps.plugin.gestures.gestures
import com.mapbox.maps.plugin.locationcomponent.OnIndicatorPositionChangedListener
import com.mapbox.maps.plugin.locationcomponent.location
import com.mapbox.navigation.base.extensions.applyDefaultNavigationOptions
import com.mapbox.navigation.base.options.NavigationOptions
import com.mapbox.navigation.base.route.NavigationRoute
import com.mapbox.navigation.base.route.NavigationRouterCallback
import com.mapbox.navigation.base.route.RouterFailure
import com.mapbox.navigation.base.trip.model.RouteProgress
import com.mapbox.navigation.core.MapboxNavigation
import com.mapbox.navigation.core.MapboxNavigationProvider
import com.mapbox.navigation.ui.maps.route.line.api.MapboxRouteLineApi
import com.mapbox.navigation.ui.maps.route.line.api.MapboxRouteLineView
import com.mapbox.navigation.ui.maps.route.line.model.MapboxRouteLineApiOptions
import com.mapbox.navigation.ui.maps.route.line.model.MapboxRouteLineViewOptions
import com.mapbox.navigation.utils.internal.toPoint
import java.util.Locale
import com.mapbox.navigation.core.trip.session.LocationObserver
import com.mapbox.navigation.core.trip.session.LocationMatcherResult
import com.mapbox.navigation.core.trip.session.RouteProgressObserver
import com.mapbox.navigation.ui.base.util.MapboxNavigationConsumer
import com.mapbox.navigation.voice.api.MapboxSpeechApi
import com.mapbox.navigation.voice.api.MapboxVoiceInstructionsPlayer
import com.mapbox.navigation.voice.model.SpeechAnnouncement
import com.mapbox.navigation.voice.model.SpeechError
import com.mapbox.navigation.voice.model.SpeechValue
import java.io.File
import java.io.FileOutputStream

class MapActivity : AppCompatActivity() {

    // MapBox 관련 변수 초기화
    private lateinit var mapView: MapView
    private lateinit var polylineAnnotationManager: PolylineAnnotationManager  // 지도에 경로를 표시할 관리 객체
    private lateinit var mapboxNavigation: MapboxNavigation
    private lateinit var routeLineApi: MapboxRouteLineApi
    private lateinit var routeLineView: MapboxRouteLineView
    private lateinit var speechApi: MapboxSpeechApi
    private lateinit var voiceInstructionsPlayer: MapboxVoiceInstructionsPlayer

    private val points = mutableListOf<Point>() // 사용자가 선택한 지점들을 저장하는 리스트
    private val trackingPoints = mutableListOf<Point>() // 사용자의 이동 경로를 저장하는 리스트
    private var lastAnnouncement: String? = null // 마지막 안내 메시지 저장 변수

    // 위치 권한 요청을 처리하기 위하 ActivityResultLanuncher
    private val locationPermissionRequest = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        when {
            permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true -> {
                initializeMap() // 위치 권한이 허용된 경우 지도 초기화
            }
            else -> {
                Toast.makeText(this, "위치 권한이 필요합니다.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)

        // 음성 안내 API 초기화
        speechApi = MapboxSpeechApi(
            context = this,
            language = "ko-KR",
        )

        voiceInstructionsPlayer = MapboxVoiceInstructionsPlayer(
            context = this,
            language = "ko-KR"
        )

        checkAndRequestPermissions() // 위치 권한 확인 및 요청

        // Mapbox 내비게이션 초기화
        mapboxNavigation = MapboxNavigationProvider.create(
            NavigationOptions.Builder(this.applicationContext).build()
        )
    }

    // 위치 권한 확인 및 요청
    private fun checkAndRequestPermissions() {
        if (checkLocationPermission()) {
            initializeMap()
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

    // 지도 초기화
    @SuppressLint("MissingPermission")
    private fun initializeMap() {
        mapView = findViewById(R.id.mapView)

        val startButton = findViewById<Button>(R.id.startNavigationButton)
        val stopButton = findViewById<Button>(R.id.stopNavigationButton)

        mapView.getMapboxMap().loadStyleUri(Style.MAPBOX_STREETS) { style ->
            style.localizeLabels(Locale("ko")) // 지도 라벨 한글화

            mapView.location.updateSettings {
                enabled = true // 현재 위치 표시 활성화
                pulsingEnabled = true // 현재 위치에 펄싱 효과 추가
            }

            // 경로 라인 표시 옵션 설정
            val routeLineOptions = MapboxRouteLineViewOptions.Builder(this)
                .routeLineBelowLayerId("road-label")
                .build()
            routeLineApi = MapboxRouteLineApi(MapboxRouteLineApiOptions.Builder().build())
            routeLineView = MapboxRouteLineView(routeLineOptions)

            // 내비게이션 시작 버튼 클릭 시 경로 요청
            startButton.setOnClickListener {
                if (points.size >= 2) {
                    requestRoute(points)
                    // 경로가 그려진 후 약간의 딜레이를 두고 캡쳐
                    Handler(Looper.getMainLooper()).postDelayed({
                        captureMapView()?.let { bitmap ->
                            // 캡처된 비트맵 처리
                            saveMapImage(bitmap)
                            // route.distance()로 총 거리를 얻어옴 (미터 단위)
                            val distanceInKm = 12.34
                            // MapBox에서 제공하는 Point 객체를 감싸는 새로운 클래스 활용
                            val parcelablePoints = points.map { ParcelablePoint(it) }
                            // 거리 정보와 캡처된 이미지로 바텀시트 표시
                            showCourseCompleteBottomSheet(distanceInKm, bitmap, parcelablePoints)
                        }
                    }, 500) // 500ms 딜레이
                    Log.d("NAVINAVI", "${points}")
                    Toast.makeText(this, "경유지 포함 내비게이션 시작", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "최소 출발지와 도착지를 선택해주세요", Toast.LENGTH_SHORT).show()
                }
            }

            // 내비게이션 종료 버튼 클릭 시 경로 초기화
            stopButton.setOnClickListener {
                mapboxNavigation.apply {
                    stopTripSession()
                    unregisterRouteProgressObserver(routeProgressObserver)
                    unregisterLocationObserver(realTimeLocationObserver)
                    setNavigationRoutes(emptyList()) // 경로 초기화 추가
                }
                routeLineApi.clearRouteLine { expected ->
                    expected.fold(
                        { error -> Log.e("NAVINAVI", "경로 삭제 실패: ${error.errorMessage}") },
                        { _ -> Log.d("NAVINAVI", "경로가 성공적으로 삭제되었습니다.") }
                    )
                }
                points.clear()
                polylineAnnotationManager.deleteAll()
                voiceInstructionsPlayer.clear()
                Toast.makeText(this, "내비게이션 종료", Toast.LENGTH_SHORT).show()
            }

            val listener = object : OnIndicatorPositionChangedListener {
                override fun onIndicatorPositionChanged(point: Point) {
                    mapView.getMapboxMap().setCamera(
                        CameraOptions.Builder()
                            .center(point)
                            .zoom(15.0)
                            .build()
                    )
                    mapView.location.removeOnIndicatorPositionChangedListener(this)
                }
            }

            mapView.location.addOnIndicatorPositionChangedListener(listener)

            // 지도 클릭 시 위치 추가
            polylineAnnotationManager = mapView.annotations.createPolylineAnnotationManager()
            mapView.gestures.addOnMapClickListener { point ->
                handleMapClick(point)
                true
            }
        }
    }

    // 지도 클릭 시 위치 처리
    private fun handleMapClick(point: Point) {
        points.add(point)
        when (points.size) {
            1 -> Toast.makeText(this, "출발지 설정", Toast.LENGTH_SHORT).show()
            2 -> Toast.makeText(this, "경유지 또는 도착지를 선택하세요", Toast.LENGTH_SHORT).show()
            else -> Toast.makeText(this, "경유지 ${points.size-1} 추가", Toast.LENGTH_SHORT).show()
        }
        if (points.size >= 1) {
            drawLine(points)  // 경로 그리기
        }
    }

    // 경로에 지도에 그리기
    private fun drawLine(points: List<Point>) {
        val polylineOptions = PolylineAnnotationOptions()
            .withPoints(points)
            .withLineColor("#FF0000") // 경로 색상(빨간색)
            .withLineWidth(4.0)  // 경로 두께

        polylineAnnotationManager.deleteAll()  // 기존 경로 삭제
        polylineAnnotationManager.create(polylineOptions)  // 새로운 경로 추가
    }

    // 화면 캡쳐 함수 추가
private fun captureMapView(): Bitmap? {
    //        val mapView = binding.mapView
    return try {
        // 지도 뷰의 크기만큼 비트맵 생성
        val bitmap = Bitmap.createBitmap(
            mapView.width,
            mapView.height,
            Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bitmap)
        mapView.draw(canvas)
        bitmap
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}

// 이미지 저장 함수 수정
private fun saveMapImage(bitmap: Bitmap): String {
    val file = File(cacheDir, "course_map.jpg")
    FileOutputStream(file).use { out ->
        bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out)
    }
    Log.d("MapImage", "이미지 저장 성공: ${file.absolutePath}")
    Log.d("MapImage", "이미지 크기: ${file.length() / 1024}KB")
    return file.absolutePath // 파일 경로 반환
}


// BottomSheet 호출
private fun showCourseCompleteBottomSheet(distance: Double, capturedImage: Bitmap, parcelablePoints: List<ParcelablePoint>) {
    // 캐시에서 이미지 파일 읽기
    val imagePath = saveMapImage(capturedImage)

    val file = File(cacheDir, "course_map.jpg")
    if (file.exists()) {
        try {
            val bitmap = BitmapFactory.decodeFile(file.absolutePath)
            bitmap?.let {
                CourseCompleteBottomSheet.newInstance(distance, imagePath, parcelablePoints)
                    .show(supportFragmentManager, "CourseCompleteBottomSheet")
            }
        } catch (e: Exception) {
            Log.e("MapActivity", "Failed to load image: ${e.message}")
        }
    }
}



// 경로 요청 시 화면 캡쳐 함수 호출

private fun requestRoute(points: List<Point>) {
    mapboxNavigation.requestRoutes(
        RouteOptions.builder()
            .applyDefaultNavigationOptions()
            .profile(DirectionsCriteria.PROFILE_WALKING) // 도보 경로 설정
            .language("ko")
            .steps(true)
            .voiceUnits(DirectionsCriteria.METRIC)  // 거리 단위(미터)
            .coordinatesList(points) // 좌표 리스트 설정
            .waypointIndicesList((0 until points.size).toList())
            .waypointNamesList(List(points.size) { index ->
                when (index) {
                    0 -> "출발지"
                    points.size - 1 -> "도착지"
                    else -> "경유지 $index"
                }
            })
            .build(),
        object : NavigationRouterCallback {
            @SuppressLint("MissingPermission")
            override fun onRoutesReady(routes: List<NavigationRoute>, routerOrigin: String) {
                routes.firstOrNull()?.let { route ->
                    routeLineApi.setNavigationRoutes(listOf(route)) { value ->
                        mapView.getMapboxMap().getStyle()?.apply {
                            routeLineView.renderRouteDrawData(this, value)

//                                    // 경로가 그려진 후 약간의 딜레이를 두고 캡쳐
//                                    Handler(Looper.getMainLooper()).postDelayed({
//                                        captureMapView()?.let { bitmap ->
//                                            // 캡처된 비트맵 처리
//                                            saveMapImage(bitmap)
//                                            // route.distance()로 총 거리를 얻어옴 (미터 단위)
//                                            val distanceInKm = route.directionsRoute.distance() / 1000
//                                            // 거리 정보와 캡처된 이미지로 바텀시트 표시
//                                            showCourseCompleteBottomSheet(distanceInKm, bitmap)
//                                        }
//                                    }, 500) // 500ms 딜레이
                        }
                    }
                    mapboxNavigation.startTripSession()
                    mapboxNavigation.setNavigationRoutes(listOf(route))
                    mapboxNavigation.registerRouteProgressObserver(routeProgressObserver)
                }
            }

            override fun onFailure(reasons: List<RouterFailure>, routeOptions: RouteOptions) {}
            override fun onCanceled(routeOptions: RouteOptions, routerOrigin: String) {}
        }
    )
}



    // 위치 변경 시 호출되는 콜백
    private val realTimeLocationObserver = object : LocationObserver {
        override fun onNewLocationMatcherResult(result: LocationMatcherResult) {
            val currentLocation = result.enhancedLocation.toPoint()
            val bearing = result.enhancedLocation.bearing?: 0.0 // null일 경우 0.0으로 기본값 설정

            mapView.getMapboxMap().setCamera(
                CameraOptions.Builder()
                    .center(currentLocation)
                    .zoom(15.0)
                    .bearing(bearing) // 사용자의 방향에 맞춰 카메라 회전
                    .build()
            )

            mapView.location.updateSettings {
                locationPuck = LocationPuck2D(
                    bearingImage = ImageHolder.from(R.drawable.run_with_icon),
                    shadowImage = null,
                    scaleExpression = null
                )
            }

            // 🚀 사용자의 이동 경로를 추가하고 초록색으로 트래킹
            if (trackingPoints.isNotEmpty() && trackingPoints.last() != currentLocation) {
                trackingPoints.add(currentLocation)
                drawTrackingLine(trackingPoints) // 초록색 경로 그리기
            } else if (trackingPoints.isEmpty()) {
                trackingPoints.add(currentLocation)
            }
        }



    override fun onNewRawLocation(rawLocation: Location) {
            val userLocation = Point.fromLngLat(rawLocation.longitude, rawLocation.latitude)

            if (points.isNotEmpty() && points.last() != userLocation) {
                val updatedPoints = listOf(userLocation) + points.drop(1)
                requestRoute(updatedPoints)
            }
        }
    }

    // ✅ 이동한 경로를 초록색으로 그리는 함수
    private fun drawTrackingLine(points: List<Point>) {
        val polylineOptions = PolylineAnnotationOptions()
            .withPoints(points)
            .withLineColor("#00FF00") // 초록색
            .withLineWidth(4.0)

        polylineAnnotationManager.deleteAll()  // 기존 트래킹 경로 삭제
        polylineAnnotationManager.create(polylineOptions)  // 새로운 경로 추가
    }



    // 경로 진행 상황을 감시하는 옵저버 + 목적지 도착 시 트래킹 중지 기능 추가
    private val routeProgressObserver = RouteProgressObserver { routeProgress ->
        val currentLegIndex = routeProgress.currentLegProgress?.legIndex
        val distanceRemaining = routeProgress.distanceRemaining
        val durationRemaining = routeProgress.durationRemaining

        Log.d("NAVINAVI", "현재 경로 구간 인덱스 :${currentLegIndex}, 남은 거리: ${distanceRemaining}, 시간: ${durationRemaining}")

        // 목적지 도착 시 안내 종료
        if (distanceRemaining < 5) { // 남은 거리가 5m 미만일 경우 종료

            val totalDistance = routeProgress.route.distance() // 총 이동 거리 (미터)
            val totalDuration = routeProgress.route.duration() // 총 소요 시간 (초)

            val totalTimeInMinutes = (totalDuration / 60).toInt() // 분 단위 변환
            val toastMessage = "목적지 도착!\n총 이동 거리: ${totalDistance.toInt()}m\n총 소요 시간: ${totalTimeInMinutes}분"
            stopNavigation()
            Toast.makeText(this, toastMessage, Toast.LENGTH_LONG).show()
//            Toast.makeText(this, "목적지에 도착했습니다. 내비게이션을 종료합니다.", Toast.LENGTH_SHORT).show()
        }

        routeProgress.voiceInstructions?.let { voiceInstructions ->
            val currentAnnouncement = voiceInstructions.announcement()

            if (currentAnnouncement != lastAnnouncement) {
                lastAnnouncement = currentAnnouncement

                speechApi.generate(
                    voiceInstructions,
                    object : MapboxNavigationConsumer<Expected<SpeechError, SpeechValue>> {
                        override fun accept(expected: Expected<SpeechError, SpeechValue>) {
                            expected.fold({ error ->
                                voiceInstructionsPlayer.play(
                                    error.fallback,
                                    object : MapboxNavigationConsumer<SpeechAnnouncement> {
                                        override fun accept(value: SpeechAnnouncement) {
                                            speechApi.clean(value)
                                        }
                                    }
                                )
                            }, { value ->
                                voiceInstructionsPlayer.play(
                                    value.announcement,
                                    object : MapboxNavigationConsumer<SpeechAnnouncement> {
                                        override fun accept(value: SpeechAnnouncement) {
                                            speechApi.clean(value)
                                        }
                                    }
                                )
                            })
                        }
                    }
                )
            }
        }
    }

    // stopButton 클릭 시 경로 초기화 기능 유지
    private fun stopNavigation() {
        mapboxNavigation.apply {
            stopTripSession()
            unregisterRouteProgressObserver(routeProgressObserver)
            unregisterLocationObserver(realTimeLocationObserver)
            setNavigationRoutes(emptyList()) // 경로 초기화
        }
        routeLineApi.clearRouteLine { expected ->
            expected.fold(
                { error -> Log.e("NAVINAVI", "경로 삭제 실패: ${error.errorMessage}") },
                { _ -> Log.d("NAVINAVI", "경로가 성공적으로 삭제되었습니다.") }
            )
        }
        points.clear()  // 지점 초기화
//        polylineAnnotationManager.deleteAll() // 지도에 표시된 모든 폴라라인 주석 삭제 역할
        voiceInstructionsPlayer.clear()
        Toast.makeText(this, "내비게이션 종료", Toast.LENGTH_SHORT).show()
    }


    override fun onStart() {
        super.onStart()
        mapboxNavigation.apply {
            registerLocationObserver(realTimeLocationObserver)
            registerRouteProgressObserver(routeProgressObserver)
        }
    }

    override fun onStop() {
        super.onStop()
//        mapboxNavigation.apply {
//            unregisterLocationObserver(realTimeLocationObserver)
//            unregisterRouteProgressObserver(routeProgressObserver)
//        }
        stopNavigation()
    }

    override fun onDestroy() {
        super.onDestroy()
        voiceInstructionsPlayer.shutdown()
        mapView.onDestroy()
    }
}