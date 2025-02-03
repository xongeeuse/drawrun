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
//import com.mapbox.common.location.LocationObserver
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
import com.mapbox.navigation.core.MapboxNavigation
import com.mapbox.navigation.core.MapboxNavigationProvider
import com.mapbox.navigation.ui.maps.route.line.api.MapboxRouteLineApi
import com.mapbox.navigation.ui.maps.route.line.api.MapboxRouteLineView
import com.mapbox.navigation.ui.maps.route.line.model.MapboxRouteLineApiOptions
import com.mapbox.navigation.ui.maps.route.line.model.MapboxRouteLineViewOptions
import com.mapbox.navigation.utils.internal.toPoint
import java.util.Locale
// import 문 수정
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

    private lateinit var mapView: MapView // MapView 객체 선언
    private lateinit var polylineAnnotationManager: PolylineAnnotationManager // Polyline 관리 객체
    private lateinit var mapboxNavigation: MapboxNavigation
    private lateinit var routeLineApi: MapboxRouteLineApi
    private lateinit var routeLineView: MapboxRouteLineView
    private lateinit var speechApi: MapboxSpeechApi
    private lateinit var voiceInstructionsPlayer: MapboxVoiceInstructionsPlayer


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
        setContentView(R.layout.activity_map) // 레이아웃 설정

        // 음성 안내 컴포넌트 초기화
        speechApi = MapboxSpeechApi(
            context = this,
            language = "ko-KR", // 한국어 로케일 정확히  지정
        )

        voiceInstructionsPlayer = MapboxVoiceInstructionsPlayer(
            context = this,
            language = "ko-KR" // 한국어 로케일 정확히 지정
        )

        checkAndRequestPermissions() // 권한 확인 및 요청 처리 시작

        // Mapbox 내비게이션 인스턴스 생성
        mapboxNavigation = MapboxNavigationProvider.create(
            NavigationOptions.Builder(this.applicationContext).build()
        )
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
//        mapView = MapView(this)
//        setContentView(mapView)
        mapView = findViewById(R.id.mapView)

        // 버튼 참조 가져오기
        val startButton = findViewById<Button>(R.id.startNavigationButton)
        val stopButton = findViewById<Button>(R.id.stopNavigationButton)

        // loadStyleUri(Style.MAPBOX_STREETS)
        // loadStyleUri(Style.DARK)
        // loadStyleUri("mapbox://styles/mapbox/navigation-night-v1")

        mapView.getMapboxMap().loadStyleUri(Style.MAPBOX_STREETS) { style ->
            style.localizeLabels(Locale("ko"))

            mapView.location.updateSettings {
                enabled = true
                pulsingEnabled = true
            }

            // initializeMap() 함수 내부 스타일 로드 후 추가
            val routeLineOptions = MapboxRouteLineViewOptions.Builder(this)
                .routeLineBelowLayerId("road-label")
                .build()
            routeLineApi = MapboxRouteLineApi(MapboxRouteLineApiOptions.Builder().build())
            routeLineView = MapboxRouteLineView(routeLineOptions)

            // 버튼 클릭 이벤트 설정
            startButton.setOnClickListener {
                if (points.size >= 2) {
                    // destination 파라미터 제거 (불필요)
                    requestRoute(points) // points 리스트 전체 전달
                    Log.d("NAVINAVI", "${points}")

                    Toast.makeText(this, "경유지 포함 내비게이션 시작", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "최소 출발지와 도착지를 선택해주세요", Toast.LENGTH_SHORT).show()
                }
            }

            stopButton.setOnClickListener {
                mapboxNavigation.apply {
                    stopTripSession()
                    unregisterRouteProgressObserver(routeProgressObserver)
                    unregisterLocationObserver(locationObserver)
                }
                points.clear()
                polylineAnnotationManager.deleteAll()
                voiceInstructionsPlayer.clear()
                Toast.makeText(this, "내비게이션 종료", Toast.LENGTH_SHORT).show()
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
        points.add(point)
        when (points.size) {
            1 -> Toast.makeText(this, "출발지 설정", Toast.LENGTH_SHORT).show()
            2 -> Toast.makeText(this, "경유지 또는 도착지를 선택하세요", Toast.LENGTH_SHORT).show()
            else -> Toast.makeText(this, "경유지 ${points.size-1} 추가", Toast.LENGTH_SHORT).show()
        }
        // 클릭할 때마다 라인 그리기 업데이트
        if (points.size > 1) {
            drawLine(points)
        }
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

    // Navigation 도전합니다.
    private fun requestRoute(points: List<Point>) {
        mapboxNavigation.requestRoutes(
            RouteOptions.builder()
                .applyDefaultNavigationOptions()
                .profile("mapbox/walking")  // 보행자 프로필로 변경
                .language("ko") // 한국어 명시적 지정
                .steps(true)    // 반드시 true로 설정
                .voiceUnits(DirectionsCriteria.METRIC)  // 미터법으로 설정
                .coordinatesList(points)  // 전체 웨이포인트 리스트 전달
                .waypointIndicesList((0 until points.size).toList())  // 웨이포인트 인덱스 설정
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
                        // 경로 렌더링
                        routeLineApi.setNavigationRoutes(listOf(route)) { value ->
                            mapView.getMapboxMap().getStyle()?.apply {
                                routeLineView.renderRouteDrawData(this, value)
                            }
                        }
                        // 내비게이션 시작
                        mapboxNavigation.startTripSession()

                        // 경로 설정 및 관찰자 등록
                        mapboxNavigation.setNavigationRoutes(listOf(route))
                        mapboxNavigation.registerRouteProgressObserver(routeProgressObserver)
                    }
                }

                // 필수 구현 메서드들 추가
                override fun onFailure(reasons: List<RouterFailure>, routeOptions: RouteOptions) {
                    // 실패 처리
                }

                override fun onCanceled(routeOptions: RouteOptions, routerOrigin: String) {
                    // 취소 처리
                }
            }
        )
    }

    // 위치 업데이트 관찰자 추가
    private val locationObserver = object : LocationObserver {
        override fun onNewLocationMatcherResult(result: LocationMatcherResult) {
            // 실시간 위치 업데이트 처리
            mapView.location.updateSettings {
                locationPuck = LocationPuck2D(
                    bearingImage = ImageHolder.from(R.drawable.run_with_icon)
                )
            }

            // 카메라 추적
            mapView.camera.easeTo(
                CameraOptions.Builder()
                    .center(result.enhancedLocation.toPoint())
                    .zoom(15.0)
                    .build()
            )
        }

        // 필수 구현 메서드 추가
//        override fun onLocationUpdateReceived(locations: List<Location>) {
//            // 빈 구현도 가능
//        }

        // 새로 추가해야 할 메서드
        override fun onNewRawLocation(rawLocation: Location) {
            // 원시 위치 데이터 처리 (필요한 경우)
        }
    }
    // 여기 봐라
    // 음성 안내 시스템

    private val routeProgressObserver = RouteProgressObserver { routeProgress ->
        // 현재 경로 구간 인덱스 가져오기
        val currentLegIndex = routeProgress.currentLegProgress?.legIndex

        // 남은 거리와 시간
        val distanceRemaining = routeProgress.distanceRemaining
        val durationRemaining = routeProgress.durationRemaining
        Log.d("NAVINAVI", "현재 경로 구간 인덱스 :${currentLegIndex}, 남은 거리: ${distanceRemaining}, 시간: ${durationRemaining}")

        routeProgress.voiceInstructions?.let { voiceInstructions ->
            speechApi.generate(
                voiceInstructions,
                object : MapboxNavigationConsumer<Expected<SpeechError, SpeechValue>> {
                    override fun accept(expected: Expected<SpeechError, SpeechValue>) {
                        expected.fold({ error ->
                            // 오류 발생 시 기본 TTS로 실행
                            voiceInstructionsPlayer.play(
                                error.fallback,
                                object : MapboxNavigationConsumer<SpeechAnnouncement> {
                                    override fun accept(value: SpeechAnnouncement) {
                                        speechApi.clean(value)
                                    }
                                }
                            )
                        }, { value ->
                            // 정상적인 음성 파일 재생
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







    override fun onStart() {
        super.onStart()
        mapboxNavigation.apply {
            registerLocationObserver(locationObserver)
            registerRouteProgressObserver(routeProgressObserver)
        }
    }

    override fun onStop() {
        super.onStop()
        mapboxNavigation.apply {
            unregisterLocationObserver(locationObserver)
            unregisterRouteProgressObserver(routeProgressObserver)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        voiceInstructionsPlayer.shutdown()
        mapView.onDestroy()
    }



}