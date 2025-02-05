//package com.example.drawrun.ui.map
//
//import android.Manifest
//import android.annotation.SuppressLint
//import android.content.pm.PackageManager
//import android.os.Bundle
//import android.util.Log
//import android.widget.Button
//import android.widget.Toast
//import androidx.activity.ComponentActivity
//import androidx.activity.result.contract.ActivityResultContracts
//import androidx.core.app.ActivityCompat
//import com.example.drawrun.R
//import com.mapbox.api.directions.v5.DirectionsCriteria
//import com.mapbox.api.directions.v5.models.RouteOptions
//import com.mapbox.bindgen.Expected
//import com.mapbox.common.location.Location
////import com.mapbox.common.location.LocationObserver
//import com.mapbox.geojson.Point
//import com.mapbox.maps.CameraOptions
//import com.mapbox.maps.ImageHolder
//import com.mapbox.maps.MapView
//import com.mapbox.maps.Style
//import com.mapbox.maps.extension.localization.localizeLabels
//import com.mapbox.maps.plugin.LocationPuck2D
//import com.mapbox.maps.plugin.animation.camera
//import com.mapbox.maps.plugin.annotation.annotations
//import com.mapbox.maps.plugin.annotation.generated.PolylineAnnotationManager
//import com.mapbox.maps.plugin.annotation.generated.PolylineAnnotationOptions
//import com.mapbox.maps.plugin.annotation.generated.createPolylineAnnotationManager
//import com.mapbox.maps.plugin.gestures.gestures
//import com.mapbox.maps.plugin.locationcomponent.OnIndicatorPositionChangedListener
//import com.mapbox.maps.plugin.locationcomponent.location
//import com.mapbox.navigation.base.extensions.applyDefaultNavigationOptions
//import com.mapbox.navigation.base.options.NavigationOptions
//import com.mapbox.navigation.base.route.NavigationRoute
//import com.mapbox.navigation.base.route.NavigationRouterCallback
//import com.mapbox.navigation.base.route.RouterFailure
//import com.mapbox.navigation.core.MapboxNavigation
//import com.mapbox.navigation.core.MapboxNavigationProvider
//import com.mapbox.navigation.ui.maps.route.line.api.MapboxRouteLineApi
//import com.mapbox.navigation.ui.maps.route.line.api.MapboxRouteLineView
//import com.mapbox.navigation.ui.maps.route.line.model.MapboxRouteLineApiOptions
//import com.mapbox.navigation.ui.maps.route.line.model.MapboxRouteLineViewOptions
//import com.mapbox.navigation.utils.internal.toPoint
//import java.util.Locale
//// import 문 수정
//import com.mapbox.navigation.core.trip.session.LocationObserver
//import com.mapbox.navigation.core.trip.session.LocationMatcherResult
//import com.mapbox.navigation.core.trip.session.RouteProgressObserver
//import com.mapbox.navigation.ui.base.util.MapboxNavigationConsumer
//import com.mapbox.navigation.voice.api.MapboxSpeechApi
//import com.mapbox.navigation.voice.api.MapboxVoiceInstructionsPlayer
//import com.mapbox.navigation.voice.model.SpeechAnnouncement
//import com.mapbox.navigation.voice.model.SpeechError
//import com.mapbox.navigation.voice.model.SpeechValue
//
//class MapActivity : ComponentActivity() {
//
//    private lateinit var mapView: MapView // MapView 객체 선언
//    private lateinit var polylineAnnotationManager: PolylineAnnotationManager // Polyline 관리 객체
//    private lateinit var mapboxNavigation: MapboxNavigation
//    private lateinit var routeLineApi: MapboxRouteLineApi
//    private lateinit var routeLineView: MapboxRouteLineView
//    private lateinit var speechApi: MapboxSpeechApi
//    private lateinit var voiceInstructionsPlayer: MapboxVoiceInstructionsPlayer
//
//
//    private val points = mutableListOf<Point>() // 사용자가 클릭한 좌표를 저장할 리스트
//
//    // 위치 권한 요청 런처 설정
//    private val locationPermissionRequest = registerForActivityResult(
//        ActivityResultContracts.RequestMultiplePermissions()
//    ) { permissions ->
//        when {
//            permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true -> {
//                initializeMap() // 권한이 승인되면 지도 초기화 실행
//            }
//            else -> {
//                Toast.makeText(this, "위치 권한이 필요합니다.", Toast.LENGTH_SHORT).show()
//            }
//        }
//    }
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_map) // 레이아웃 설정
//
//        // 음성 안내 컴포넌트 초기화
//        speechApi = MapboxSpeechApi(
//            context = this,
//            language = "ko-KR", // 한국어 로케일 정확히  지정
//        )
//
//        voiceInstructionsPlayer = MapboxVoiceInstructionsPlayer(
//            context = this,
//            language = "ko-KR" // 한국어 로케일 정확히 지정
//        )
//
//        checkAndRequestPermissions() // 권한 확인 및 요청 처리 시작
//
//        // Mapbox 내비게이션 인스턴스 생성
//        mapboxNavigation = MapboxNavigationProvider.create(
//            NavigationOptions.Builder(this.applicationContext).build()
//        )
//    }
//
//    // 위치 권한 확인 및 요청 처리 함수 -------------------------------------
//    private fun checkAndRequestPermissions() {
//        if (checkLocationPermission()) {
//            initializeMap()
//        } else {
//            requestLocationPermission()
//        }
//    }
//
//    private fun checkLocationPermission(): Boolean {
//        return ActivityCompat.checkSelfPermission(
//            this,
//            Manifest.permission.ACCESS_FINE_LOCATION
//        ) == PackageManager.PERMISSION_GRANTED
//    }
//
//    private fun requestLocationPermission() {
//        locationPermissionRequest.launch(
//            arrayOf(
//                Manifest.permission.ACCESS_FINE_LOCATION,
//                Manifest.permission.ACCESS_COARSE_LOCATION
//            )
//        )
//    }
//
//    // 지도 초기화 및 설정 -------------------------------------------------
//    @SuppressLint("MissingPermission")
//    private fun initializeMap() {
////        mapView = MapView(this)
////        setContentView(mapView)
//        mapView = findViewById(R.id.mapView)
//
//        // 버튼 참조 가져오기
//        val startButton = findViewById<Button>(R.id.startNavigationButton)
//        val stopButton = findViewById<Button>(R.id.stopNavigationButton)
//
//        // loadStyleUri(Style.MAPBOX_STREETS)
//        // loadStyleUri(Style.DARK)
//        // loadStyleUri("mapbox://styles/mapbox/navigation-night-v1")
//
//        mapView.getMapboxMap().loadStyleUri(Style.MAPBOX_STREETS) { style ->
//            style.localizeLabels(Locale("ko"))
//
//            mapView.location.updateSettings {
//                enabled = true
//                pulsingEnabled = true
//            }
//
//            // initializeMap() 함수 내부 스타일 로드 후 추가
//            val routeLineOptions = MapboxRouteLineViewOptions.Builder(this)
//                .routeLineBelowLayerId("road-label")
//                .build()
//            routeLineApi = MapboxRouteLineApi(MapboxRouteLineApiOptions.Builder().build())
//            routeLineView = MapboxRouteLineView(routeLineOptions)
//
//            // 버튼 클릭 이벤트 설정
//            startButton.setOnClickListener {
//                if (points.size >= 2) {
//                    // destination 파라미터 제거 (불필요)
//                    requestRoute(points) // points 리스트 전체 전달
//                    Log.d("NAVINAVI", "${points}")
//
//                    Toast.makeText(this, "경유지 포함 내비게이션 시작", Toast.LENGTH_SHORT).show()
//                } else {
//                    Toast.makeText(this, "최소 출발지와 도착지를 선택해주세요", Toast.LENGTH_SHORT).show()
//                }
//            }
//
//            stopButton.setOnClickListener {
//                mapboxNavigation.apply {
//                    stopTripSession()
//                    unregisterRouteProgressObserver(routeProgressObserver)
//                    unregisterLocationObserver(locationObserver)
//                }
//                points.clear()
//                polylineAnnotationManager.deleteAll()
//                voiceInstructionsPlayer.clear()
//                Toast.makeText(this, "내비게이션 종료", Toast.LENGTH_SHORT).show()
//            }
//
//            // 1. 리스너 객체를 변수에 저장
//            val listener = object : OnIndicatorPositionChangedListener {
//                override fun onIndicatorPositionChanged(point: Point) {
//                    mapView.getMapboxMap().setCamera(
//                        CameraOptions.Builder()
//                            .center(point)
//                            .zoom(15.0)
//                            .build()
//                    )
//                    // 2. 저장된 리스너 객체를 사용하여 제거
//                    mapView.location.removeOnIndicatorPositionChangedListener(this)
//                }
//            }
//
//            // 3. 리스너 등록
//            mapView.location.addOnIndicatorPositionChangedListener(listener)
//
//            polylineAnnotationManager = mapView.annotations.createPolylineAnnotationManager()
//            mapView.gestures.addOnMapClickListener { point ->
//                handleMapClick(point)
//                true
//            }
//        }
//    }
//
//    // 지도 클릭 이벤트 처리 -----------------------------------------------
//    private fun handleMapClick(point: Point) {
//        points.add(point)
//        when (points.size) {
//            1 -> Toast.makeText(this, "출발지 설정", Toast.LENGTH_SHORT).show()
//            2 -> Toast.makeText(this, "경유지 또는 도착지를 선택하세요", Toast.LENGTH_SHORT).show()
//            else -> Toast.makeText(this, "경유지 ${points.size-1} 추가", Toast.LENGTH_SHORT).show()
//        }
//        // 클릭할 때마다 라인 그리기 업데이트
//        if (points.size > 1) {
//            drawLine(points)
//        }
//    }
//
//
//
//    // 라인 그리기 ---------------------------------------------------------
//    private fun drawLine(points: List<Point>) {
//        val polylineOptions = PolylineAnnotationOptions()
//            .withPoints(points) // LineString 대신 List<Point> 사용
//            .withLineColor("#FF0000") // 라인 색상 (빨간색)
//            .withLineWidth(4.0) // 라인 두께
//
//        polylineAnnotationManager.deleteAll() // 기존 라인 삭제 (중복 방지)
//        polylineAnnotationManager.create(polylineOptions) // 새로운 라인 생성 및 지도에 추가
//    }
//
//    // Navigation 도전합니다.
//    private fun requestRoute(points: List<Point>) {
//        mapboxNavigation.requestRoutes(
//            RouteOptions.builder()
//                .applyDefaultNavigationOptions()
//                .profile("mapbox/walking")  // 보행자 프로필로 변경
//                .language("ko") // 한국어 명시적 지정
//                .steps(true)    // 반드시 true로 설정
//                .voiceUnits(DirectionsCriteria.METRIC)  // 미터법으로 설정
//                .coordinatesList(points)  // 전체 웨이포인트 리스트 전달
//                .waypointIndicesList((0 until points.size).toList())  // 웨이포인트 인덱스 설정
//                .waypointNamesList(List(points.size) { index ->
//                    when (index) {
//                        0 -> "출발지"
//                        points.size - 1 -> "도착지"
//                        else -> "경유지 $index"
//                    }
//                })
//                .build(),
//            object : NavigationRouterCallback {
//                @SuppressLint("MissingPermission")
//                override fun onRoutesReady(routes: List<NavigationRoute>, routerOrigin: String) {
//                    routes.firstOrNull()?.let { route ->
//                        // 경로 렌더링
//                        routeLineApi.setNavigationRoutes(listOf(route)) { value ->
//                            mapView.getMapboxMap().getStyle()?.apply {
//                                routeLineView.renderRouteDrawData(this, value)
//                            }
//                        }
//                        // 내비게이션 시작
//                        mapboxNavigation.startTripSession()
//
//                        // 경로 설정 및 관찰자 등록
//                        mapboxNavigation.setNavigationRoutes(listOf(route))
//                        mapboxNavigation.registerRouteProgressObserver(routeProgressObserver)
//                    }
//                }
//
//                // 필수 구현 메서드들 추가
//                override fun onFailure(reasons: List<RouterFailure>, routeOptions: RouteOptions) {
//                    // 실패 처리
//                }
//
//                override fun onCanceled(routeOptions: RouteOptions, routerOrigin: String) {
//                    // 취소 처리
//                }
//            }
//        )
//    }
//
//    // 위치 업데이트 관찰자 추가
//    private val locationObserver = object : LocationObserver {
//        override fun onNewLocationMatcherResult(result: LocationMatcherResult) {
//            // 실시간 위치 업데이트 처리
//            mapView.location.updateSettings {
//                locationPuck = LocationPuck2D(
//                    bearingImage = ImageHolder.from(R.drawable.run_with_icon)
//                )
//            }
//
//            // 카메라 추적
//            mapView.camera.easeTo(
//                CameraOptions.Builder()
//                    .center(result.enhancedLocation.toPoint())
//                    .zoom(15.0)
//                    .build()
//            )
//        }
//
//        // 필수 구현 메서드 추가
////        override fun onLocationUpdateReceived(locations: List<Location>) {
////            // 빈 구현도 가능
////        }
//
//        // 새로 추가해야 할 메서드
//        override fun onNewRawLocation(rawLocation: Location) {
//            // 원시 위치 데이터 처리 (필요한 경우)
//        }
//    }
//    // 여기 봐라
//    // 음성 안내 시스템
//
//    private val routeProgressObserver = RouteProgressObserver { routeProgress ->
//        // 현재 경로 구간 인덱스 가져오기
//        val currentLegIndex = routeProgress.currentLegProgress?.legIndex
//
//        // 남은 거리와 시간
//        val distanceRemaining = routeProgress.distanceRemaining
//        val durationRemaining = routeProgress.durationRemaining
//        Log.d("NAVINAVI", "현재 경로 구간 인덱스 :${currentLegIndex}, 남은 거리: ${distanceRemaining}, 시간: ${durationRemaining}")
//
//        routeProgress.voiceInstructions?.let { voiceInstructions ->
//            speechApi.generate(
//                voiceInstructions,
//                object : MapboxNavigationConsumer<Expected<SpeechError, SpeechValue>> {
//                    override fun accept(expected: Expected<SpeechError, SpeechValue>) {
//                        expected.fold({ error ->
//                            // 오류 발생 시 기본 TTS로 실행
//                            voiceInstructionsPlayer.play(
//                                error.fallback,
//                                object : MapboxNavigationConsumer<SpeechAnnouncement> {
//                                    override fun accept(value: SpeechAnnouncement) {
//                                        speechApi.clean(value)
//                                    }
//                                }
//                            )
//                        }, { value ->
//                            // 정상적인 음성 파일 재생
//                            voiceInstructionsPlayer.play(
//                                value.announcement,
//                                object : MapboxNavigationConsumer<SpeechAnnouncement> {
//                                    override fun accept(value: SpeechAnnouncement) {
//                                        speechApi.clean(value)
//                                    }
//                                }
//                            )
//                        })
//                    }
//                }
//            )
//        }
//    }
//
//
//
//
//
//
//
//    override fun onStart() {
//        super.onStart()
//        mapboxNavigation.apply {
//            registerLocationObserver(locationObserver)
//            registerRouteProgressObserver(routeProgressObserver)
//        }
//    }
//
//    override fun onStop() {
//        super.onStop()
//        mapboxNavigation.apply {
//            unregisterLocationObserver(locationObserver)
//            unregisterRouteProgressObserver(routeProgressObserver)
//        }
//    }
//
//    override fun onDestroy() {
//        super.onDestroy()
//        voiceInstructionsPlayer.shutdown()
//        mapView.onDestroy()
//    }
//
//
//
//}


package com.example.drawrun.ui.map

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import com.example.drawrun.R
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

class MapActivity : ComponentActivity() {

    // MapBox 관련 변수 초기화
    private lateinit var mapView: MapView
    private lateinit var polylineAnnotationManager: PolylineAnnotationManager
    private lateinit var mapboxNavigation: MapboxNavigation
    private lateinit var routeLineApi: MapboxRouteLineApi
    private lateinit var routeLineView: MapboxRouteLineView
    private lateinit var speechApi: MapboxSpeechApi
    private lateinit var voiceInstructionsPlayer: MapboxVoiceInstructionsPlayer

    private val points = mutableListOf<Point>() // 사용자가 선택한 지점들을 저장하는 리스트
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
            drawLine(points)
        }
    }

    // 경로에 지도에 그리기
    private fun drawLine(points: List<Point>) {
        val polylineOptions = PolylineAnnotationOptions()
            .withPoints(points)
            .withLineColor("#FF0000") // 경로 색상(빨간색)
            .withLineWidth(4.0)  // 경로 두께

        polylineAnnotationManager.deleteAll()
        polylineAnnotationManager.create(polylineOptions)
    }

    // 경로 요청
    private fun requestRoute(points: List<Point>) {
        mapboxNavigation.requestRoutes(
            RouteOptions.builder()
                .applyDefaultNavigationOptions()
                .profile(DirectionsCriteria.PROFILE_WALKING) // 도보 경로 설정
                .language("ko")
                .steps(true)
                .voiceUnits(DirectionsCriteria.METRIC)  // 거리 단위(미터)
                .coordinatesList(points)
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
                    .bearing(bearing) // 사용자의 휴대폰 방향으로 카메라 회전
                    .build()
            )

            mapView.location.updateSettings {
                locationPuck = LocationPuck2D(
                    bearingImage = ImageHolder.from(R.drawable.run_with_icon),
                    shadowImage = null,
                    scaleExpression = null
                )
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

    // 경로 진행 상황을 감시하는 옵저버
    // 경로 진행 상황을 감시하는 옵저버
    private val routeProgressObserver = RouteProgressObserver { routeProgress ->
        val currentLegIndex = routeProgress.currentLegProgress?.legIndex
        val distanceRemaining = routeProgress.distanceRemaining
        val durationRemaining = routeProgress.durationRemaining

        Log.d("NAVINAVI", "현재 경로 구간 인덱스 :${currentLegIndex}, 남은 거리: ${distanceRemaining}, 시간: ${durationRemaining}")

        // 목적지 도착 시 안내 종료
        if (distanceRemaining < 5) { // 남은 거리가 5m 미만일 경우 종료
            stopNavigation()
            Toast.makeText(this, "목적지에 도착했습니다. 내비게이션을 종료합니다.", Toast.LENGTH_SHORT).show()
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
            setNavigationRoutes(emptyList())
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


//class MapActivity : ComponentActivity() {
//
//    // MapBox 관련 변수 초기화
//    private lateinit var mapView: MapView
//    private lateinit var polylineAnnotationManager: PolylineAnnotationManager
//    private lateinit var mapboxNavigation: MapboxNavigation
//    private lateinit var routeLineApi: MapboxRouteLineApi
//    private lateinit var routeLineView: MapboxRouteLineView
//    private lateinit var speechApi: MapboxSpeechApi
//    private lateinit var voiceInstructionsPlayer: MapboxVoiceInstructionsPlayer
//
//    private val points = mutableListOf<Point>() // 사용자가 선택한 지점들을 저장하는 리스트
//    private var lastAnnouncement: String? = null // 마지막 안내 메시지 저장 변수
//    private var hasAnnouncedArrival = false // 도착 여부 체크
//
//    // 위치 권한 요청을 처리하기 위하 ActivityResultLanuncher
//    private val locationPermissionRequest = registerForActivityResult(
//        ActivityResultContracts.RequestMultiplePermissions()
//    ) { permissions ->
//        when {
//            permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true -> {
//                initializeMap() // 위치 권한이 허용된 경우 지도 초기화
//            }
//            else -> {
//                Toast.makeText(this, "위치 권한이 필요합니다.", Toast.LENGTH_SHORT).show()
//            }
//        }
//    }
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_map)
//
//        // 음성 안내 API 초기화
//        speechApi = MapboxSpeechApi(
//            context = this,
//            language = "ko-KR",
//        )
//
//        voiceInstructionsPlayer = MapboxVoiceInstructionsPlayer(
//            context = this,
//            language = "ko-KR"
//        )
//
//        checkAndRequestPermissions() // 위치 권한 확인 및 요청
//
//        // Mapbox 내비게이션 초기화
//        mapboxNavigation = MapboxNavigationProvider.create(
//            NavigationOptions.Builder(this.applicationContext).build()
//        )
//    }
//
//    private fun checkAndRequestPermissions() {
//        if (ActivityCompat.checkSelfPermission(
//                this,
//                Manifest.permission.ACCESS_FINE_LOCATION
//            ) != PackageManager.PERMISSION_GRANTED
//        ) {
//            locationPermissionRequest.launch(
//                arrayOf(
//                    Manifest.permission.ACCESS_FINE_LOCATION,
//                    Manifest.permission.ACCESS_COARSE_LOCATION
//                )
//            )
//        } else {
//            initializeMap()
//        }
//    }
//
//    private fun initializeMap() {
//        mapView = findViewById(R.id.mapView)
//
//        val startButton = findViewById<Button>(R.id.startNavigationButton)
//        val stopButton = findViewById<Button>(R.id.stopNavigationButton)
//
//        mapView.getMapboxMap().loadStyleUri(Style.MAPBOX_STREETS) { style ->
//            style.localizeLabels(Locale("ko")) // 지도 라벨 한글화
//
//            mapView.location.updateSettings {
//                enabled = true // 현재 위치 표시 활성화
//                pulsingEnabled = true // 현재 위치에 펄싱 효과 추가
//            }
//
//            polylineAnnotationManager = mapView.annotations.createPolylineAnnotationManager()
//
//            startButton.setOnClickListener {
//                if (points.size >= 2) {
//                    requestRoute(points)
//                    Toast.makeText(this, "경유지 포함 내비게이션 시작", Toast.LENGTH_SHORT).show()
//                } else {
//                    Toast.makeText(this, "최소 출발지와 도착지를 선택해주세요", Toast.LENGTH_SHORT).show()
//                }
//            }
//
//            stopButton.setOnClickListener {
//                stopNavigation()
//            }
//
//            mapView.gestures.addOnMapClickListener { point ->
//                handleMapClick(point)
//                true
//            }
//        }
//    }
//
//    private fun handleMapClick(point: Point) {
//        points.add(point)
//        when (points.size) {
//            1 -> Toast.makeText(this, "출발지 설정", Toast.LENGTH_SHORT).show()
//            2 -> Toast.makeText(this, "경유지 또는 도착지를 선택하세요", Toast.LENGTH_SHORT).show()
//            else -> Toast.makeText(this, "경유지 ${points.size - 1} 추가", Toast.LENGTH_SHORT).show()
//        }
//        if (points.size >= 1) {
//            drawLine(points)
//        }
//    }
//
//    private fun drawLine(points: List<Point>) {
//        val polylineOptions = PolylineAnnotationOptions()
//            .withPoints(points)
//            .withLineColor("#FF0000")
//            .withLineWidth(4.0)
//
//        polylineAnnotationManager.deleteAll()
//        polylineAnnotationManager.create(polylineOptions)
//    }
//
//    private fun requestRoute(points: List<Point>) {
//        mapboxNavigation.requestRoutes(
//            RouteOptions.builder()
//                .applyDefaultNavigationOptions()
//                .profile(DirectionsCriteria.PROFILE_WALKING)
//                .language("ko")
//                .steps(true)
//                .coordinatesList(points)
//                .build(),
//            object : NavigationRouterCallback {
//                override fun onRoutesReady(routes: List<NavigationRoute>, routerOrigin: String) {
//                    routes.firstOrNull()?.let { route ->
//                        mapboxNavigation.setNavigationRoutes(listOf(route))
//
//                        try {
//                            if (ActivityCompat.checkSelfPermission(
//                                    this@MapActivity,
//                                    Manifest.permission.ACCESS_FINE_LOCATION
//                                ) == PackageManager.PERMISSION_GRANTED
//                            ) {
//                                mapboxNavigation.startTripSession()
//                            } else {
//                                Toast.makeText(this@MapActivity, "위치 권한이 필요합니다.", Toast.LENGTH_SHORT).show()
//                            }
//                        } catch (e: SecurityException) {
//                            e.printStackTrace()
//                            Toast.makeText(this@MapActivity, "위치 권한 오류가 발생했습니다.", Toast.LENGTH_SHORT).show()
//                        }
//                    }
//                }
//
//                override fun onFailure(reasons: List<RouterFailure>, routeOptions: RouteOptions) {}
//                override fun onCanceled(routeOptions: RouteOptions, routerOrigin: String) {}
//            }
//        )
//    }
//
//
//    private val routeProgressObserver = RouteProgressObserver { routeProgress ->
//        try {
//            if (ActivityCompat.checkSelfPermission(
//                    this,
//                    Manifest.permission.ACCESS_FINE_LOCATION
//                ) == PackageManager.PERMISSION_GRANTED
//            ) {
//                val distanceRemaining = routeProgress.distanceRemaining
//
//                if (distanceRemaining < 20 && !hasAnnouncedArrival) {
//                    hasAnnouncedArrival = true
//                    announceArrival(routeProgress)
//                }
//            } else {
//                Toast.makeText(this, "위치 권한이 필요합니다.", Toast.LENGTH_SHORT).show()
//            }
//        } catch (e: SecurityException) {
//            e.printStackTrace()
//            Toast.makeText(this, "위치 권한 오류가 발생했습니다.", Toast.LENGTH_SHORT).show()
//        }
//    }
//
//
//
//
//    private fun announceArrival(routeProgress: RouteProgress) {
//        val totalDistance = routeProgress.route?.distance()?.div(1000)?.let { String.format("%.2f", it) } ?: "0"
//        val totalDuration = routeProgress.route?.duration()?.div(60)?.let { String.format("%.0f", it) } ?: "0"
//
//        val arrivalMessage = "목적지에 도착했습니다. 총 이동 거리 ${totalDistance}킬로미터, 소요 시간은 ${totalDuration}분입니다. 내비게이션을 종료합니다."
//
//        val announcement = SpeechAnnouncement.Builder(arrivalMessage).build()
//        voiceInstructionsPlayer.play(announcement, object : MapboxNavigationConsumer<SpeechAnnouncement> {
//            override fun accept(value: SpeechAnnouncement) {
//                speechApi.clean(value)
//                stopNavigation()
//            }
//        })
//    }
//
//    private fun stopNavigation() {
//        mapboxNavigation.apply {
//            stopTripSession()
//            unregisterRouteProgressObserver(routeProgressObserver)
//        }
//        polylineAnnotationManager.deleteAll()
//        points.clear()
//        Toast.makeText(this, "내비게이션 종료", Toast.LENGTH_SHORT).show()
//    }
//
//    override fun onStart() {
//        super.onStart()
//        mapboxNavigation.registerRouteProgressObserver(routeProgressObserver)
//    }
//
//    override fun onStop() {
//        super.onStop()
//        mapboxNavigation.unregisterRouteProgressObserver(routeProgressObserver)
//    }
//
//    override fun onDestroy() {
//        super.onDestroy()
//        voiceInstructionsPlayer.shutdown()
//        mapView.onDestroy()
//    }
//}
//
