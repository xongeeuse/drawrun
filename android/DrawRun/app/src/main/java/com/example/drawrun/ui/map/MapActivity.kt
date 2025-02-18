package com.example.drawrun.ui.map

import android.Manifest
import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.util.Log
import android.view.Window
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.drawrun.R
import com.example.drawrun.data.model.ParcelablePoint
import com.example.drawrun.services.NavigationForegroundService
import com.example.drawrun.ui.map.fragment.CourseCompleteBottomSheet
import com.example.drawrun.ui.runrecord.RunRecordActivity
import com.example.drawrun.utils.RetrofitInstance
import com.google.android.gms.location.LocationServices
import com.google.android.gms.wearable.PutDataMapRequest
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.wearable.Wearable
import com.mapbox.api.directions.v5.DirectionsCriteria
import com.mapbox.api.directions.v5.models.RouteOptions
import com.mapbox.bindgen.Expected
import com.mapbox.common.location.Location
import com.mapbox.geojson.Point
import com.mapbox.maps.CameraOptions
import com.mapbox.maps.EdgeInsets
import com.mapbox.maps.ImageHolder
import com.mapbox.maps.MapView
import com.mapbox.maps.Style
import com.mapbox.maps.extension.localization.localizeLabels
import com.mapbox.maps.extension.style.layers.addLayerAbove
import com.mapbox.maps.extension.style.layers.getLayer
import com.mapbox.maps.plugin.LocationPuck2D
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
import com.mapbox.navigation.core.trip.session.LocationObserver
import com.mapbox.navigation.core.trip.session.LocationMatcherResult
import com.mapbox.navigation.core.trip.session.RouteProgressObserver
import com.mapbox.navigation.ui.base.util.MapboxNavigationConsumer
import com.mapbox.navigation.voice.api.MapboxSpeechApi
import com.mapbox.navigation.voice.api.MapboxVoiceInstructionsPlayer
import com.mapbox.navigation.voice.model.SpeechAnnouncement
import com.mapbox.navigation.voice.model.SpeechError
import com.mapbox.navigation.voice.model.SpeechValue
import com.mapbox.turf.TurfMeasurement
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.io.FileOutputStream

class MapActivity : AppCompatActivity() {

    // MapBox 관련 변수 초기화
    private lateinit var mapView: MapView
    private lateinit var polylineAnnotationManager: PolylineAnnotationManager  // 지도에 경로를 표시할 관리 객체
    private lateinit var trackingAnnotationManager: PolylineAnnotationManager // ✅ 트래킹 선 (초록색)
    private lateinit var mapboxNavigation: MapboxNavigation
    private lateinit var routeLineApi: MapboxRouteLineApi
    private lateinit var routeLineView: MapboxRouteLineView
    private lateinit var speechApi: MapboxSpeechApi
    private lateinit var voiceInstructionsPlayer: MapboxVoiceInstructionsPlayer
    private lateinit var fusedLocationClient: FusedLocationProviderClient


    private val points = mutableListOf<Point>() // 사용자가 선택한 지점들을 저장하는 리스트
    private val trackingPoints = mutableListOf<Point>() // 사용자의 이동 경로를 저장하는 리스트
    private var lastAnnouncement: String? = null // 마지막 안내 메시지 저장 변수

    // 백그라운드에서 음성안내 실시
    private lateinit var tts: TextToSpeech
    private var isNavigating = false

    private var aiPath: List<ParcelablePoint>?=null
    private var isAiRoute = false

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

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        // ✅ AI 추천 경로 데이터를 Intent에서 가져오기 (안전한 변환 처리)
        aiPath = intent.getParcelableArrayListExtra("aiPath")

        if (!aiPath.isNullOrEmpty()) {
            val pathPoints = aiPath!!.map { it.point } // ParcelablePoint → Point 변환
            points.clear()
            points.addAll(pathPoints)
            drawAiPath(pathPoints) // ✅ 폴리라인(빨간색) 그리기
            isAiRoute = true
            Log.d("MapActivity", "📌 받은 AI 경로 데이터: $aiPath")
        } else {
            isAiRoute = false
        }

        Log.d("MapActivity", "📌 받은 AI 경로 데이터: $aiPath") // ✅ 로그 추가

        // 백그라운드 tts
        tts = TextToSpeech(this){ status ->
            if(status == TextToSpeech.SUCCESS) {
                tts.language = Locale.KOREAN
            }
        }

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

//    Ai 먀먀먀
    // ✅ AI 경로를 지도에 폴리라인(빨간색)으로 표시
    private fun drawAiPath(points: List<Point>) {
        if (!::polylineAnnotationManager.isInitialized) {
            Log.e("MapActivity", "❌ polylineAnnotationManager가 아직 초기화되지 않음!")
            return
        }

        if (points.isEmpty()) {
            Log.e("MapActivity", "❌ AI 경로 포인트가 비어 있음!")
            return
        }

        Log.d("MapActivity", "📌 AI 경로 표시 시작 - 포인트 개수: ${points.size}")

        polylineAnnotationManager.deleteAll() // 기존 경로 초기화

        val polylineOptions = PolylineAnnotationOptions()
            .withPoints(points)
            .withLineColor("#FF0000") // AI 경로 색상 (빨간색)
            .withLineWidth(5.0)

        polylineAnnotationManager.create(polylineOptions)
        Log.d("MapActivity", "✅ AI 경로 생성 완료")

        // ✅ AI 경로의 첫 번째 지점으로 카메라 이동 (AI 경로가 있을 때만)
        val firstPoint = points.firstOrNull()
        if (firstPoint != null) {
            Log.d("MapActivity", "📌 카메라 이동: ${firstPoint.latitude()}, ${firstPoint.longitude()}")

            mapView.getMapboxMap().setCamera(CameraOptions.Builder()
                .center(firstPoint) // 첫 번째 포인트로 이동
                .zoom(14.0) // 적절한 줌 레벨 설정
                .build()
            )
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

    private fun sendStartNavigationCommandToWatch() {
        Wearable.getNodeClient(this).connectedNodes.addOnSuccessListener { nodes ->
            if (nodes.isNotEmpty()) {
                val nodeId = nodes.first().id  // 첫 번째 연결된 노드 ID 가져오기
                Log.d("PhoneData", "전송 대상 노드 ID: $nodeId")

                Wearable.getMessageClient(this).sendMessage(
                    nodeId,
                    "/start_navigation",
                    "start".toByteArray()
                ).addOnSuccessListener {
                    Log.d("PhoneData", "워치로 내비게이션 시작 명령 전송 성공")
                }.addOnFailureListener { e ->
                    Log.e("PhoneData", "워치로 내비게이션 시작 명령 전송 실패", e)
                }
            } else {
                Log.e("PhoneData", "연결된 노드가 없습니다.")
            }
        }.addOnFailureListener { e ->
            Log.e("PhoneData", "노드 탐색 실패", e)
        }
    }


    // 지도 초기화
    @SuppressLint("MissingPermission")
    private fun initializeMap() {
        mapView = findViewById(R.id.mapView)

        val startButton = findViewById<Button>(R.id.startNavigationButton)
        val stopButton = findViewById<Button>(R.id.stopNavigationButton)

        // 경로 생성 버튼 추가
        val generateRouteButton = findViewById<Button>(R.id.generateRouteButton)

        mapView.getMapboxMap().loadStyleUri(Style.DARK) { style ->
            style.localizeLabels(Locale("ko")) // 지도 라벨 한글화

            mapView.location.updateSettings {
                enabled = true // 현재 위치 표시 활성화
                pulsingEnabled = true // 현재 위치에 펄싱 효과 추가
            }
            // ✅ 경로(빨간색)와 트래킹 선(초록색)을 따로 관리하여 트래킹 선이 위로 보이게 함
            polylineAnnotationManager = mapView.annotations.createPolylineAnnotationManager()
            trackingAnnotationManager = mapView.annotations.createPolylineAnnotationManager() // ✅ 트래킹 선 추가

            Log.d("MapActivity", "✅ polylineAnnotationManager & trackingAnnotationManager 초기화 완료")

            moveTrackingLineAboveRoute()

            mapView.gestures.addOnMapClickListener { point ->
                handleMapClick(point)
                true
            }

            // 경로 생성 버튼 클릭 시 동작 추가
            generateRouteButton.setOnClickListener {
                if (points.size >= 1) {
                    requestRoute(points, manualRequest = true)  // 사용자가 찍은 빨간색 좌표를 기반으로 도보 경로 요청
                    Log.d("NAVINAVI", "사용자 지정 경로 요청: ${points}")
                    Toast.makeText(this, "경로를 생성하고 스냅샷을 찍습니다.", Toast.LENGTH_SHORT).show()

                    // ✅ 생성된 경로를 스냅샷으로 저장
                } else {
                    Toast.makeText(this, "최소 출발지와 도착지를 선택해주세요", Toast.LENGTH_SHORT).show()
                }
            }

            // 경로 라인 표시 옵션 설정
            val routeLineOptions = MapboxRouteLineViewOptions.Builder(this)
                .routeLineBelowLayerId("waterway-label")
                .build()
            routeLineApi = MapboxRouteLineApi(MapboxRouteLineApiOptions.Builder().build())
            routeLineView = MapboxRouteLineView(routeLineOptions)

            startButton.setOnClickListener {
                if (points.isEmpty()) {
                    Toast.makeText(this, "최소 출발지와 도착지를 선택해주세요.", Toast.LENGTH_SHORT).show()
                    isNavigating = true
                    startNavigationForegroundService()
                    return@setOnClickListener
                }
                // ✅ AI 경로가 있는 경우 처리
                if (isAiRoute && !aiPath.isNullOrEmpty()) {
                    val pathPoints = aiPath!!.map { it.point } // ParcelablePoint → Point 변환
                    points.clear()  // 기존 사용자 입력 포인트 초기화
                    points.addAll(pathPoints)  // AI 경로 추가
                    drawAiPath(pathPoints) // ✅ AI 경로를 지도에 표시
                }
                // ✅ 현재 사용자 위치 가져오기
                fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                    if (location != null) {
                        val userPoint = Point.fromLngLat(location.longitude, location.latitude)
                        val startPoint = points.first()

                        val distance = TurfMeasurement.distance(userPoint, startPoint, "meters")

                        if (distance > 50) {
                            Toast.makeText(this@MapActivity, "출발지로 이동 후 시작해주세요.", Toast.LENGTH_SHORT).show()
                        } else {
                            // ✅ 출발지와 가까운 경우 기존 내비게이션 시작 로직 실행
                            mapboxNavigation.requestRoutes(
                                RouteOptions.builder()
                                    .applyDefaultNavigationOptions()
                                    .profile(DirectionsCriteria.PROFILE_WALKING)
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
                                    override fun onRoutesReady(routes: List<NavigationRoute>, routerOrigin: String) {
                                        val route = routes.firstOrNull()

                                        if (route != null) {
                                            mapboxNavigation.startTripSession()
                                            mapboxNavigation.setNavigationRoutes(listOf(route))
                                            mapboxNavigation.registerRouteProgressObserver(routeProgressObserver)
                                            sendStartNavigationCommandToWatch()

                                            // 🚶‍♂️ 도보 모드에 적절한 줌 설정 (내비게이션 시작 시만)
                                            mapView.getMapboxMap().setCamera(
                                                CameraOptions.Builder()
                                                    .center(points.first()) // 출발지를 중심으로 설정
                                                    .zoom(17.0) // 도보 모드에 적절한 줌 레벨
                                                    .build()
                                            )

                                            Toast.makeText(this@MapActivity, "내비게이션 시작!", Toast.LENGTH_SHORT).show()
                                        } else {
                                            Toast.makeText(this@MapActivity, "경로 생성 실패!", Toast.LENGTH_SHORT).show()
                                        }
                                    }

                                    override fun onFailure(reasons: List<RouterFailure>, routeOptions: RouteOptions) {
                                        Toast.makeText(this@MapActivity, "경로 요청 실패!", Toast.LENGTH_SHORT).show()
                                    }

                                    override fun onCanceled(routeOptions: RouteOptions, routerOrigin: String) {}
                                }
                            )
                        }
                    } else {
                        Toast.makeText(this@MapActivity, "현재 위치를 가져올 수 없습니다.", Toast.LENGTH_SHORT).show()
                    }
                }.addOnFailureListener {
                    Toast.makeText(this@MapActivity, "위치 정보 오류 발생!", Toast.LENGTH_SHORT).show()
                }
            }



            stopButton.setOnClickListener {
                stopNavigationForegroundService()
                stopNavigation()
            }

//            polylineAnnotationManager = mapView.annotations.createPolylineAnnotationManager()


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

//            // 지도 클릭 시 위치 추가
//            polylineAnnotationManager = mapView.annotations.createPolylineAnnotationManager()
//            mapView.gestures.addOnMapClickListener { point ->
//                handleMapClick(point)
//                true
//            }
        }
    }

    // 지도 클릭 시 위치 처리
    private fun handleMapClick(point: Point) {
        points.add(point)
        when (points.size) {
            1 -> Toast.makeText(this, "출발지 설정", Toast.LENGTH_SHORT).show()
            2 -> Toast.makeText(this, "경유지 또는 도착지를 선택하세요", Toast.LENGTH_SHORT).show()
            else -> Toast.makeText(this, "경유지 ${points.size - 1} 추가", Toast.LENGTH_SHORT).show()
        }
        if (points.size >= 1) {
            drawLine(points)  // 경로 그리기
        }
    }

    // 경로에 지도에 그리기
    private fun drawLine(points: List<Point>) {
        polylineAnnotationManager.deleteAll()

        val polylineOptions = PolylineAnnotationOptions()
            .withPoints(points)
            .withLineColor("#FF0000") // 경로 색상(빨간색)
            .withLineWidth(5.0)  // 경로 두께

//        polylineAnnotationManager.deleteAll()  // 기존 경로 삭제
        polylineAnnotationManager.create(polylineOptions)  // 새로운 경로 추가
    }

    // 경로 요청
    private fun requestRoute(points: List<Point>, manualRequest: Boolean = false) {
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
                            }
                        }

                        // ✅ 내비게이션 자동 시작 X
                        // ✅ 줌 조정 X (내비 시작 시 적용)

                        if (manualRequest) {
                            val distanceInKm = route.directionsRoute.distance() / 1000
                            captureMapSnapshotAndShow(distanceInKm)
                            Toast.makeText(
                                this@MapActivity,
                                "경로가 생성되었습니다.",
                                Toast.LENGTH_LONG
                            ).show()
                        }

                    }
                }

                override fun onFailure(reasons: List<RouterFailure>, routeOptions: RouteOptions) {}
                override fun onCanceled(routeOptions: RouteOptions, routerOrigin: String) {}
            }
        )
    }

    private fun captureMapSnapshotAndShow(distanceInKm: Double) {
        if (points.size < 2) return

        // 현재 위치 마커 숨기기
        mapView.location.updateSettings {
            enabled = false  // 위치 마커 비활성화
        }

        // 1️⃣ 바운딩 박스(경로 영역) 계산
        val routeBounds = points.fold(null as Pair<Point, Point>?) { bounds, point ->
            when (bounds) {
                null -> Pair(point, point)
                else -> Pair(
                    Point.fromLngLat(
                        minOf(bounds.first.longitude(), point.longitude()),
                        minOf(bounds.first.latitude(), point.latitude())
                    ),
                    Point.fromLngLat(
                        maxOf(bounds.second.longitude(), point.longitude()),
                        maxOf(bounds.second.latitude(), point.latitude())
                    )
                )
            }
        }

        routeBounds?.let { (southWest, northEast) ->
            // 2️⃣ 지도 카메라를 바운딩 박스에 맞게 자동 조정 (Mapbox 제공 기능)
            val cameraOptions = mapView.getMapboxMap().cameraForCoordinates(
                points, // 경로에 포함된 모든 좌표 사용
                EdgeInsets(300.0, 300.0, 300.0, 300.0) // 경로 크기에 따라 여백 추가 (200~300 추천)
            )

            mapView.getMapboxMap().setCamera(cameraOptions) // 카메라 설정 적용

            // 3️⃣ 사용자가 그린 빨간색 경로(Polyline) 삭제
            polylineAnnotationManager.deleteAll()

            // 4️⃣ 약간의 딜레이 후 스냅샷 촬영 (카메라 조정 후 안정적 촬영)
            mapView.postDelayed({
                mapView.snapshot { bitmap ->

                    if (bitmap != null) {
                        uploadSnapshotAndShowBottomSheet(bitmap, distanceInKm)
                    } else {
                        Log.e("MAP_SNAPSHOT", "스냅샷 생성 실패")
                    }
                }
            }, 1400) // 카메라 이동 후 1.4초 딜레이 (줌 조정 안정화)
        }
    }

    private fun uploadSnapshotAndShowBottomSheet(bitmap: Bitmap, distanceInKm: Double) {
        lifecycleScope.launch {
            val imageUrl = uploadImage(bitmap)
            if (imageUrl != null) {
                val parcelablePoints = points.map { ParcelablePoint(it) }
                showCourseCompleteBottomSheet(distanceInKm, imageUrl, parcelablePoints)
            } else {
                Toast.makeText(this@MapActivity, "이미지 업로드 실패", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private suspend fun uploadImage(bitmap: Bitmap): String? {
        return withContext(Dispatchers.IO) {
            val file = File(cacheDir, "temp_image.jpg")
            FileOutputStream(file).use { out ->
                bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out)
            }

            val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
            val body = MultipartBody.Part.createFormData("file", file.name, requestFile)

            try {
                val imageUploadApi = RetrofitInstance.ImageUploadApi(this@MapActivity)
                val response = imageUploadApi.uploadImage(body)
                if (response.isSuccess) {
                    response.data?.url
                } else {
                    null
                }
            } catch (e: Exception) {
                null
            } finally {
                file.delete()
            }
        }
    }

    private fun showCourseCompleteBottomSheet(distance: Double, imageUrl: String, parcelablePoints: List<ParcelablePoint>) {
        CourseCompleteBottomSheet.newInstance(distance, imageUrl, parcelablePoints)
            .show(supportFragmentManager, "CourseCompleteBottomSheet")
    }

    // ✅ 정사각형 크롭 함수 (중앙 기준)
    private fun cropBitmapToSquare(bitmap: Bitmap): Bitmap {
        val width = bitmap.width
        val height = bitmap.height
        val size = minOf(width, height) // 정사각형 크기 설정 (가장 작은 변을 기준)

        val xOffset = (width - size) / 2
        val yOffset = (height - size) / 2

        return Bitmap.createBitmap(bitmap, xOffset, yOffset, size, size)
    }




    private fun showSnapshotDialog(bitmap: Bitmap) {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.dialog_snapshot)

        val imageView = dialog.findViewById<ImageView>(R.id.snapshotImageView)
        val closeButton = dialog.findViewById<Button>(R.id.closeButton)

        imageView.setImageBitmap(bitmap)

        closeButton.setOnClickListener {
            dialog.dismiss() // 다이얼로그 닫기

        // ✅ 닫기 버튼 누르면 위치 마커 다시 활성
        mapView.location.updateSettings {
            enabled = true // 위치 마커 다시 활성화
            }
        }

        dialog.show()
    }




    // 위치 변경 시 호출되는 콜백
    private val realTimeLocationObserver = object : LocationObserver {
        override fun onNewLocationMatcherResult(result: LocationMatcherResult) {
            val currentLocation = result.enhancedLocation.toPoint()
            val bearing = result.enhancedLocation.bearing ?: 0.0 // null일 경우 0.0으로 기본값 설정

            mapView.getMapboxMap().setCamera(
                CameraOptions.Builder()
                    .center(currentLocation)
                    .zoom(17.0)
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
//                requestRoute(updatedPoints, manualRequest = false)
            }
        }
    }

    // ✅ 이동한 경로를 초록색으로 그리는 함수
    private fun drawTrackingLine(points: List<Point>) {
        trackingAnnotationManager.deleteAll()
        val polylineOptions = PolylineAnnotationOptions()
            .withPoints(points)
            .withLineColor("#00FF00") // 초록색
            .withLineWidth(8.0)

//        polylineAnnotationManager.deleteAll()  // 기존 트래킹 경로 삭제
        polylineAnnotationManager.create(polylineOptions)  // 새로운 경로 추가
        trackingAnnotationManager.create(polylineOptions)   // 위로 올리기 방금 추가
        Log.d("Tracking", "✅ 트래킹 선 업데이트 완료")
    }


    // 경로 진행 상황을 감시하는 옵저버 + 목적지 도착 시 트래킹 중지 기능 추가
    private val routeProgressObserver = RouteProgressObserver { routeProgress ->
        val distanceRemaining = routeProgress.distanceRemaining // 남은 시간
        val durationRemaining = routeProgress.durationRemaining // 남은 거리
        val totalDistance = routeProgress.route.distance()
        val currentLegIndex = routeProgress.currentLegProgress?.legIndex    // 현재 구간 인덱스
        val stepProgress = routeProgress.currentLegProgress?.currentStepProgress
        val distanceToNextTurn = stepProgress?.distanceRemaining?.toDouble() ?: 0.0
        val voiceInstrunction = routeProgress.voiceInstructions?.announcement() ?: "안내 없음"
//        speakNavigationInstructions(voiceInstrunction)
        // 데이터 로그 출력
        Log.d("NAVINAVI", "다음 지시까지 남은 거리: ${distanceToNextTurn}m, 방향 안내: ${voiceInstrunction}")

        Log.d(
            "NAVINAVI",
            "현재 경로 구간 인덱스 :${currentLegIndex}, 남은 거리: ${distanceRemaining}, 시간: ${durationRemaining}"
        )
        // 데이터 전송 함수 호출
        sendNavigationInstructionToWatch(
            distanceToNextTurn,
            voiceInstrunction,
            totalDistance,
            distanceRemaining,
            durationRemaining
        )
        // 목적지 도착 시 안내 종료
        if (distanceRemaining < 5) { // 남은 거리가 1m 미만일 경우 종료
            routeProgress.route.legs()?.let { legs ->
                if (routeProgress.currentLegProgress?.legIndex == legs.size - 1) {
                    val totalDistance = routeProgress.route.distance() // 총 이동 거리 (미터)
                    val totalDuration = routeProgress.route.duration().toInt()  // 총 소요 시간 (초)

                    val totalDistanceInKm = totalDistance / 1000 // 미터 -> 킬로미터 변환
//                    val totalTimeInMinutes = (totalDuration / 60) // 분 단위 변환
                    stopNavigation()
                    // 트래킹 스냅샷 캡처 후 모달 띄우기
                    captureTrackingSnapshot()
                    // 이동 거리 및 소요 시간 안내 모달 표시
                    showArrivalDialog(totalDistanceInKm, totalDuration, totalDistance, totalDuration)
                }
            }
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

    // 도착 시 모달 다이얼로그 표시 함수 추가
//    private fun showArrivalDialog(distanceInKm: Double, time: Int, totalDistance: Double, totalDuration: Int) {
//        val formattedDistance = String.format("%.3f", distanceInKm) // 소수점 둘째 자리까지 나타냄
//
//        // X분 Y초 형식 변환
//        val minutes = time / 60
//        val seconds = time % 60
//        val formattedTime = "${minutes}분 ${seconds}초"
//
//        AlertDialog.Builder(this)
//            .setTitle("📍 목적지 도착!")
//            .setMessage("러닝이 완료되었습니다.\n'러닝 완료하기'를 눌러 기록을 확인하세요.")
//            .setPositiveButton("러닝 완료하기") { dialog, _ ->
//            dialog.dismiss()
//            navigateToRunRecordActivity(totalDistance, totalDuration, distanceInKm, time)  // ✅ RunRecordActivity로 이동
//        }
//            .setCancelable(false) // 사용자가 다이얼로그 외부를 눌러도 닫히지 않게 설정
//            .show()
//    }

    private fun showArrivalDialog(distanceInKm: Double, time: Int, totalDistance: Double, totalDuration: Int) {
        val dialogView = layoutInflater.inflate(R.layout.dialog_arrival, null)
        val dialog = AlertDialog.Builder(this).setView(dialogView).create()

        val imageView = dialogView.findViewById<ImageView>(R.id.trackingSnapshotImageView)
        val finishButton = dialogView.findViewById<Button>(R.id.finishRunButton)
        Log.d("showArrivalDialog", "📌 스냅샷 로드 시도 - trackingSnapshotUrl: $trackingSnapshotUrl")

        if (trackingSnapshotUrl != null) {
            Log.d("showArrivalDialog", "📌 로드할 스냅샷 경로: $trackingSnapshotUrl")
            Glide.with(this)
                .load(trackingSnapshotUrl)
                .into(imageView)
        } else {
            Log.e("showArrivalDialog", "❌ trackingSnapshotUrl이 null이거나 비어 있음")
            // ✅ 기본 GIF (`gps_art_run_done.gif`) 적용
            Glide.with(this)
                .asGif() // GIF로 로드
                .load(R.drawable.gps_art_run_done) // ✅ drawable에 있는 GIF
                .diskCacheStrategy(DiskCacheStrategy.ALL) // 캐싱 전략
                .into(imageView)
        }

        finishButton.setOnClickListener {
            dialog.dismiss()
            navigateToRunRecordActivity(totalDistance, totalDuration, distanceInKm, time, trackingSnapshotUrl)
        }

        dialog.show()
    }



    // stopButton 클릭 시 경로 초기화 기능 유지
    private fun stopNavigation() {
        mapboxNavigation.apply {
            stopTripSession()
            unregisterRouteProgressObserver(routeProgressObserver)
            unregisterLocationObserver(realTimeLocationObserver)
            setNavigationRoutes(emptyList()) // 경로 초기화
        }

        // ✅ 경로 삭제 후 빈 경로 강제 설정 (강제 리로드)
        mapboxNavigation.setNavigationRoutes(emptyList()) // 두 번 실행하여 강제 적용

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
        if (isNavigating) { // ✅ 내비게이션이 실행 중일 때만 정지
            stopNavigation()
            isNavigating = false
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        voiceInstructionsPlayer.shutdown()
        mapView.onDestroy()
    }

    // 워치로 전송하는 코드
    private fun sendNavigationInstructionToWatch(
        distanceToNextTurn: Double,
        voiceInstruction: String,
        totalDistance: Double,
        distanceRemaining: Float,
        durationRemaining: Double
    ) {
        val dataClient = Wearable.getDataClient(this)
        val path = "/navigation/instructions"

        // 전송할 데이터 추가
        val dataMap = PutDataMapRequest.create(path).apply {
            dataMap.putDouble("distanceToNextTurn", distanceToNextTurn)
            dataMap.putString("voiceInstruction", voiceInstruction)
            dataMap.putDouble("totalDistance", totalDistance)
            dataMap.putFloat("distanceRemaining", distanceRemaining)
            dataMap.putDouble("durationRemaining", durationRemaining)
        }

        // 전송 상태 로그 출력
        dataClient.putDataItem(dataMap.asPutDataRequest()).addOnSuccessListener {
            Log.d("PhoneData", "내비게이션 지시 데이터 전송 성공")
        }.addOnFailureListener { e ->
            Log.e("PhoneData", "내비게이션 지시 데이터 전송 실패", e)
        }
    }

    // ✅ RunRecordActivity로 이동하는 함수
    private fun navigateToRunRecordActivity(
        totalDistance: Double,
        totalDuration: Int,
        distanceInKm: Double,
        time: Int,
        snapshotUrl: String?
    ) {
        val intent = Intent(this, RunRecordActivity::class.java).apply {
            putExtra("totalDistance", totalDistance)
            putExtra("distanceInKm", distanceInKm)
            putExtra("totalDuration", totalDuration)
            putExtra("time", time)
            putExtra("trackingSnapshotUrl", snapshotUrl)
            putExtra("pathId", 1)
        }
        startActivity(intent)
        finish() // 현재 액티비티 종료
    }

    // ✅ 전역 변수 선언 (트래킹 스냅샷 URL 저장)
    private var trackingSnapshotUrl: String? = null


    private fun captureTrackingSnapshot() {
        Log.d("MapActivity", "🟢 captureTrackingSnapshot() 호출됨")

        if (trackingPoints.size < 2) {
            Log.e("TrackingSnapshot", "❌ 트래킹 포인트가 부족하여 스냅샷을 캡처할 수 없음")
            return
        }

        // 1️⃣ 경로 바운딩 박스 계산 (최소/최대 좌표 찾기)
        val routeBounds = trackingPoints.fold(null as Pair<Point, Point>?) { bounds, point ->
            when (bounds) {
                null -> Pair(point, point)
                else -> Pair(
                    Point.fromLngLat(
                        minOf(bounds.first.longitude(), point.longitude()),
                        minOf(bounds.first.latitude(), point.latitude())
                    ),
                    Point.fromLngLat(
                        maxOf(bounds.second.longitude(), point.longitude()),
                        maxOf(bounds.second.latitude(), point.latitude())
                    )
                )
            }
        }

        routeBounds?.let { (southWest, northEast) ->
            val width = TurfMeasurement.distance(
                Point.fromLngLat(southWest.longitude(), southWest.latitude()),
                Point.fromLngLat(northEast.longitude(), southWest.latitude()), "meters"
            )

            val height = TurfMeasurement.distance(
                Point.fromLngLat(southWest.longitude(), southWest.latitude()),
                Point.fromLngLat(southWest.longitude(), northEast.latitude()), "meters"
            )

            Log.d("TrackingSnapshot", "📐 경로 크기 계산 완료 - Width: ${width}m, Height: ${height}m")

            // 2️⃣ 정사각형 크기 결정 (더 긴 쪽 기준)
            val squareSize = maxOf(width, height) * 1.3  // ✅ 30% 추가해서 여백 확보

            // 3️⃣ 자동 줌 설정 (적절한 여백을 추가한 상태에서 캡처)
            val zoomLevel = when {
                squareSize > 2000 -> 13.0
                squareSize > 1000 -> 14.0
                squareSize > 500 -> 15.0
                squareSize > 200 -> 16.0
                squareSize > 100 -> 17.0
                squareSize > 50 -> 18.0
                else -> 19.0
            }

            Log.d("TrackingSnapshot", "🔍 자동 줌 설정 - Zoom Level: $zoomLevel")

            // 4️⃣ 캡처할 카메라 중앙 위치 계산
            val centerPoint = Point.fromLngLat(
                (southWest.longitude() + northEast.longitude()) / 2,
                (southWest.latitude() + northEast.latitude()) / 2
            )

            val cameraOptions = CameraOptions.Builder()
                .center(centerPoint)
                .zoom(zoomLevel)
                .build()

            mapView.mapboxMap.setCamera(cameraOptions)

            // 5️⃣ 스냅샷 캡처 실행
            mapView.postDelayed({
                mapView.snapshot { bitmap ->
                    if (bitmap != null) {
                        Log.d("TrackingSnapshot", "✅ 스냅샷 캡처 성공: 비트맵 크기 ${bitmap.width}x${bitmap.height}")

                        // ✅ 정사각형 크롭 적용
                        val squareBitmap = cropBitmapToSquare(bitmap)

                        lifecycleScope.launch {
                            val imageUrl = uploadImage(squareBitmap)  // ✅ 기존 uploadImage() 재사용
                            if (imageUrl != null) {
                                trackingSnapshotUrl = imageUrl
                                Log.d("TrackingSnapshot", "📌 스냅샷 업로드 성공 - URL: $imageUrl")

                            } else {
                                Log.e("TrackingSnapshot", "❌ 스냅샷 업로드 실패")
                            }
                        }

                        runOnUiThread {
                            val trackingImageView = findViewById<ImageView>(R.id.trackingImageView)
                            trackingImageView?.setImageBitmap(squareBitmap)
                                ?: Log.e("TrackingSnapshot", "❌ trackingImageView가 존재하지 않음!")
                        }
                    } else {
                        Log.e("TrackingSnapshot", "❌ 스냅샷 캡처 실패")
                    }
                }
            }, 1400) // ✅ 카메라 이동 후 1.4초 대기 (안정적 캡처)
        }
    }


    private fun saveBitmapToFile(bitmap: Bitmap): String? {
        return try {
            val file = File(getExternalFilesDir(null), "tracking_snapshot.png")
            val outputStream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
            outputStream.flush()
            outputStream.close()
            Log.d("TrackingSnapshot", "✅ 비트맵 파일 저장 완료: ${file.absolutePath}")
            file.absolutePath // ✅ 저장된 파일의 경로 반환
        } catch (e: Exception) {
            Log.e("TrackingSnapshot", "🚨 비트맵 파일 저장 실패", e)
            null
        }
    }

    private fun startNavigationForegroundService() {
        val intent = Intent(this, NavigationForegroundService::class.java)
        startService(intent)
    }

    private fun stopNavigationForegroundService() {
        val intent = Intent(this, NavigationForegroundService::class.java)
        stopService(intent)
    }

    // 🚀 네비게이션 안내 음성 출력
    private fun speakNavigationInstructions(text: String) {
        tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, null)
    }

    private fun moveTrackingLineAboveRoute() {
        val trackingLineLayerId = "tracking-line-layer"
        val routeLayerId = "mapbox-navigation-route-layer" // 네비 경로 Layer ID

        mapView.getMapboxMap().getStyle { style ->
            val trackingLineLayer = style.getLayer(trackingLineLayerId)
            val routeLayer = style.getLayer(routeLayerId)

            if (trackingLineLayer == null) {
                Log.e("Tracking", "❌ 트래킹 선 Layer ($trackingLineLayerId) 를 찾을 수 없음!")
                return@getStyle
            }
            if (routeLayer == null) {
                Log.e("Tracking", "❌ 네비 경로 Layer ($routeLayerId) 를 찾을 수 없음!")
                return@getStyle
            }

            // 🟢 기존 트래킹 선 레이어를 제거한 후, 다시 추가하여 경로 위로 배치
            style.removeStyleLayer(trackingLineLayerId)
            style.addLayerAbove(trackingLineLayer, routeLayerId)

            Log.d("Tracking", "✅ 트래킹 선을 네비 경로 위로 이동 완료")
        }
    }


}


