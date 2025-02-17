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
import android.graphics.Bitmap
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.widget.Button
import android.widget.ImageView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.drawrun.R
import com.example.drawrun.data.repository.MasterpieceRepository
import com.example.drawrun.databinding.ActivityNaviBinding
import com.example.drawrun.dto.course.PathPoint
import com.example.drawrun.ui.runrecord.RunRecordActivity
import com.example.drawrun.utils.RetrofitInstance
import com.example.drawrun.viewmodel.MasterpieceViewModel
import com.example.drawrun.viewmodel.MasterpieceViewModelFactory
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.wearable.PutDataMapRequest
import com.google.android.gms.wearable.Wearable
import com.mapbox.api.directions.v5.DirectionsCriteria
import com.mapbox.api.directions.v5.models.RouteOptions
import com.mapbox.bindgen.Expected
import com.mapbox.geojson.LineString
import com.mapbox.geojson.Point
import com.mapbox.maps.CameraOptions
import com.mapbox.maps.EdgeInsets
import com.mapbox.maps.MapView
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
import com.mapbox.navigation.ui.base.util.MapboxNavigationConsumer
import com.mapbox.navigation.ui.maps.route.line.api.MapboxRouteLineApi
import com.mapbox.navigation.ui.maps.route.line.api.MapboxRouteLineView
import com.mapbox.navigation.ui.maps.route.line.model.MapboxRouteLineApiOptions
import com.mapbox.navigation.ui.maps.route.line.model.MapboxRouteLineViewOptions
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
import java.util.Locale

class NaviActivity : AppCompatActivity() {
    private lateinit var binding: ActivityNaviBinding
    private lateinit var mapboxMap: MapboxMap
    private lateinit var mapboxNavigation: MapboxNavigation
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback

    private var trackingLineManager: PolylineAnnotationManager? = null
    private lateinit var routeLineApi: MapboxRouteLineApi
    private lateinit var routeLineView: MapboxRouteLineView

    private lateinit var path: List<PathPoint> // ✅ path를 클래스 변수로 이동
    private lateinit var locationPermissionRequest: androidx.activity.result.ActivityResultLauncher<Array<String>> // ✅ 위치 권한 요청 변수

    private lateinit var speechApi: MapboxSpeechApi
    private lateinit var voiceInstructionsPlayer: MapboxVoiceInstructionsPlayer

    private val trackedPath = mutableListOf<Point>() // 사용자가 지나간 경로 저장
    private var isTrackingStarted = false

    private var navigationStartTime: Long = 0L // 내비게이션 시작 시간
    private lateinit var sensorManager: SensorManager
    private var currentBearing: Float = 0f // ✅ 사용자의 현재 방향

    private var trackingSnapshotUrl : String? = null
    private lateinit var mapView: MapView

    // Masterpiece 출신 요청인지 확인할 플래그 설정
    private var isMasterpieceRequest = false
    private var masterpieceSegId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNaviBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.progressBar.visibility = View.VISIBLE

        // Masterpiece 요청인지 체크
        isMasterpieceRequest = intent.getBooleanExtra("isMasterpieceRequest", false)
        masterpieceSegId = intent.getIntExtra("masterpieceSegId", -1)

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
            this.mapView = mapView
            mapboxMap = mapView.getMapboxMap()
            mapboxMap.loadStyleUri(Style.DARK) { style ->
                style.localizeLabels(Locale("ko"))

                enableUserLocation() // 현위치 마커 활성화
                routeLineApi = MapboxRouteLineApi(MapboxRouteLineApiOptions.Builder().vanishingRouteLineEnabled(true).build())
                routeLineView = MapboxRouteLineView(MapboxRouteLineViewOptions.Builder(this).build())

                moveToPathStart(path) // 지도 중심 이동
                requestWalkingRoute(path.map { Point.fromLngLat(it.longitude, it.latitude) }) // 도보 경로 요청

                binding.progressBar.visibility = View.GONE
            }
        } ?: Log.e("NaviActivity", "❌ `mapView`가 초기화되지 않았음!")

        // ✅ 내비게이션 음성 안내 등록
        mapboxNavigation.registerVoiceInstructionsObserver { voiceInstructions ->
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
        checkAndRequestPermissions() // ✅ 위치 권한 요청 실행
        binding.startLocation.text = "$startLocation"
        binding.distance.text = "${distance} km"

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        val rotationVectorSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR)

        rotationVectorSensor?.let {
            sensorManager.registerListener(sensorEventListener, it, SensorManager.SENSOR_DELAY_UI)
        }


        // ✅ startButton 클릭 시 내비게이션 시작
        binding.startButton.setOnClickListener {
            navigationStartTime = System.currentTimeMillis() // 시작 시간 기록

            if (isMasterpieceRequest && masterpieceSegId != -1) {
                // Masterpiece 요청일 경우 조인 API 호출
                val repository = MasterpieceRepository(RetrofitInstance.MasterpieceApi(this))
                val masterpieceViewModelFactory = MasterpieceViewModelFactory(repository)
                val masterpieceViewModel: MasterpieceViewModel = ViewModelProvider(this, masterpieceViewModelFactory)[MasterpieceViewModel::class.java]

                masterpieceViewModel.joinMasterpiece(masterpieceSegId, 0, 0) // 임시로 masterpieceBoardId 와 position 0 으로 설정
                masterpieceViewModel.joinMasterpieceResult.observe(this) { isSuccess ->
                    if (isSuccess) {
                        Log.d("NaviActivity", "Masterpiece 조인 요청 성공")
                        Toast.makeText(this, "마스터피스 조인 성공", Toast.LENGTH_SHORT).show()
                    } else {
                        Log.e("NaviActivity", "Masterpiece 조인 요청 실패")
                        Toast.makeText(this, "마스터피스 조인 실패", Toast.LENGTH_SHORT).show()
                    }
                }
            }

            startNavigation(path)

        }

    }

    // ✅ 센서 값 받아오기
    private val sensorEventListener = object : SensorEventListener {
        override fun onSensorChanged(event: SensorEvent?) {
            if (event?.sensor?.type == Sensor.TYPE_ROTATION_VECTOR) {
                val rotationMatrix = FloatArray(9)
                SensorManager.getRotationMatrixFromVector(rotationMatrix, event.values)

                val orientation = FloatArray(3)
                SensorManager.getOrientation(rotationMatrix, orientation)

                currentBearing = Math.toDegrees(orientation[0].toDouble()).toFloat()
            }
        }
        override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
    }

    // ✅ 실제 이동한 거리 계산
    private fun calculateTotalDistance(): Double {
        var totalDistance = 0.0
        for (i in 0 until trackedPath.size - 1) {
            totalDistance += TurfMeasurement.distance(trackedPath[i], trackedPath[i + 1], "meters")
        }
        return totalDistance
    }

    // ✅ 실제 이동한 시간 계산
    private fun calculateElapsedTime(): Pair<Int, Int> {
        val elapsedMillis = System.currentTimeMillis() - navigationStartTime
        val elapsedSeconds = (elapsedMillis / 1000).toInt()
        return Pair(elapsedSeconds / 60, elapsedSeconds % 60)
    }

    // ✅ 사용자의 현재 위치 마커를 활성화하는 함수 (추가된 부분)
    private fun enableUserLocation() {
        val locationComponentPlugin = binding.mapView?.location

        locationComponentPlugin?.updateSettings {
            enabled = true // 현재 위치 마커 활성화
            pulsingEnabled = true // 펄스 효과 추가 (선택)
            layerAbove = "waterway-label" // ✅ 사용자 마커를 도로 레이어 위에 배치
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
                    binding.bottomLayout.visibility = View.GONE // 내비게이션 시작 시 코스 정보 숨김

                    val startPoint = Point.fromLngLat(path.first().longitude, path.first().latitude)
                    binding.mapView.getMapboxMap().setCamera(
                        CameraOptions.Builder()
                            .center(startPoint)
                            .zoom(17.0)
                            .bearing(currentBearing.toDouble()) // ✅ 사용자의 현재 나침반 방향 반영
                            .build()
                    )

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
                                    sendStartNavigationCommandToWatch()
                                    // 🚶‍♂️ 내비게이션 시작 시 지도 줌 설정
                                    binding.mapView.getMapboxMap().setCamera(
                                        CameraOptions.Builder()
                                            .center(startPoint)
                                            .zoom(17.0) // 도보 모드에 적절한 줌 레벨
                                            .bearing(location.bearing.toDouble()) // 사용자의 바라보는 방향으로 회전
                                            .build()
                                    )

                                    startTrackingUserLocation() // 트래킹 시작


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
        val totalDistance = routeProgress.route.distance() // 전체 경로 거리
        val currentLegIndex = routeProgress.currentLegProgress?.legIndex // 현재 구간 인덱스
        val stepProgress = routeProgress.currentLegProgress?.currentStepProgress
        val distanceToNextTurn = stepProgress?.distanceRemaining?.toDouble() ?: 0.0
        val voiceInstrunction = routeProgress.voiceInstructions?.announcement() ?: "안내 없음"
        Log.d("NAVINAVI", "남은 거리: $distanceRemaining, 남은 시간: $durationRemaining")

        // 데이터 전송 함수 호출
        sendNavigationInstructionToWatch(
            distanceToNextTurn,
            voiceInstrunction,
            totalDistance,
            distanceRemaining,
            durationRemaining
        )

        // 목적지 도착 여부 확인 (남은 거리가 1m 이하일 경우 종료)
        if (distanceRemaining < 5) {
            routeProgress.route.legs()?.let { legs ->
                if (routeProgress.currentLegProgress?.legIndex == legs.size - 1) {
                    val totalDuration = routeProgress.route.duration().toInt()  // 총 소요 시간 (초)
                    val totalDistanceInKm = totalDistance / 1000 // 미터 -> 킬로미터 변환
                    val totalDistance = routeProgress.route.distance()

                    stopNavigation() // 내비게이션 종료

                    captureTrackingSnapshot()
                    showArrivalDialog(totalDistanceInKm, totalDuration, totalDistance, totalDuration)

                }
            }
        }
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

    // ✅ 사용자의 현재 위치 트래킹 (도착 시 stopNavigation() 호출)
    private fun startTrackingUserLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationCallback = object : LocationCallback() {
                override fun onLocationResult(locationResult: LocationResult) {
                    for (location in locationResult.locations) {
                        val userPoint = Point.fromLngLat(location.longitude, location.latitude)

                        // ✅ 목적지 좌표 가져오기
                        val destinationPoint = Point.fromLngLat(path.last().longitude, path.last().latitude)

                        // ✅ 남은 거리 계산
                        val remainingDistance = TurfMeasurement.distance(userPoint, destinationPoint, "meters")

                        Log.d("NAVINAVI", "현재 위치: ${userPoint.longitude()}, ${userPoint.latitude()}")
                        Log.d("NAVINAVI", "목적지 위치: ${destinationPoint.longitude()}, ${destinationPoint.latitude()}")
                        Log.d("NAVINAVI", "남은 거리: ${remainingDistance}m")

//                        // ✅ 목적지 도착 시 위치 업데이트 중지 & 내비게이션 종료
//                        if (remainingDistance < 5.0) {
//                            stopNavigation() // 내비게이션 종료 (트래킹 중지)
//                            return
//                        }

                        // ✅ 트래킹 경로 추가 (삭제 X)
                        if (trackedPath.isEmpty() || trackedPath.last() != userPoint) {
                            trackedPath.add(userPoint)
                            updateTrackingLine() // 지도에 초록색 트래킹 경로 표시
                        }
                        updateTrackingLine() // 지도에 초록색 트래킹 경로 표시
                    }
                }
            }

            val locationRequest = LocationRequest.create().apply {
                interval = 1000 // 1초마다 위치 업데이트
                fastestInterval = 500
                priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            }

            fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null)
        }
    }


    private fun showArrivalDialog(distanceInKm: Double, time: Int, totalDistance: Double, totalDuration: Int) {
        // 이미 계산된 값을 사용하므로 재계산할 필요 X
        // val totalDistance = calculateTotalDistance() / 1000.0
        // val (minutes, seconds) = calculateElapsedTime()
        // val totalDuration = minutes * 60 + seconds

        val dialogView = layoutInflater.inflate(R.layout.dialog_arrival, null)
        val dialog = AlertDialog.Builder(this).setView(dialogView).create()

        val imageView = dialogView.findViewById<ImageView>(R.id.trackingSnapshotImageView)
        val finishButton = dialogView.findViewById<Button>(R.id.finishRunButton)

        Log.d("showArrivalDialog", "📌 스냅샷 로드 시도 - trackingSnapshotUrl: $trackingSnapshotUrl")

        if (!trackingSnapshotUrl.isNullOrEmpty()) {
            Log.d("showArrivalDialog", "📌 로드할 스냅샷 경로: $trackingSnapshotUrl")
            Glide.with(this)
                .load(trackingSnapshotUrl)
                .placeholder(R.drawable.gps_art_run_done) // 로딩 중 표시할 이미지
                .error(R.drawable.gps_art_run_done) // 로드 실패 시 표시할 이미지
                .into(imageView)
        } else {
            Log.e("showArrivalDialog", "❌ trackingSnapshotUrl이 null이거나 비어 있음")
            Glide.with(this)
                .asGif()
                .load(R.drawable.gps_art_run_done)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(imageView)
        }

        finishButton.setOnClickListener {
            dialog.dismiss()
            navigateToRunRecordActivity(totalDistance, totalDuration, distanceInKm, time, trackingSnapshotUrl)
        }

        dialog.show()
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

            // isMasterpieceRequest 및 masterpieceSegId도 함께 전달
            putExtra("isMasterpieceRequest", isMasterpieceRequest)
            putExtra("masterpieceSegId", masterpieceSegId)
        }
        startActivity(intent)
        finish() // 현재 액티비티 종료
    }


    private fun updateTrackingLine() {
        val mapView = binding.mapView
        val annotations = mapView.annotations
        val trackingColor = "#00FF00" // 초록색

        // 기존의 트래킹 라인 제거 후 다시 그림 (계속 이어지게)
        trackingLineManager?.deleteAll()
        trackingLineManager = annotations.createPolylineAnnotationManager()

        val polyline = PolylineAnnotationOptions()
            .withPoints(trackedPath) // 사용자가 이동한 경로
            .withLineColor(trackingColor) // 초록색
            .withLineWidth(7.0) // 선 두께

        trackingLineManager?.create(polyline)
    }

    // 최종 목적지 도착 시 내비게이션과 트래킹 종료
    private fun stopNavigation() {
        // 위치 업데이트 중지
        fusedLocationClient.removeLocationUpdates(locationCallback)

        // 🚫 내비게이션 종료 및 경로 초기화
        mapboxNavigation.stopTripSession()
        mapboxNavigation.setNavigationRoutes(emptyList()) // ❌ Mapbox 도보 경로 제거

        isTrackingStarted = false // 트래킹 중지 (하지만 지나간 경로는 유지됨)
        Toast.makeText(this, "🎉 목적지에 도착했습니다! 내비게이션 종료.", Toast.LENGTH_LONG).show()

        if (trackedPath.size < 2) {
            Log.e("NaviActivity", "❌ 트래킹 경로 부족! 캡처 생략")
            return
        }

//        captureTrackingSnapshot() // ✅ 네비게이션 종료 후 트래킹 스냅샷 실행

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


    // 🚀 **워치에 내비게이션 지시 정보 전송**
    private fun sendNavigationInstructionToWatch(
        distanceToNextTurn: Double,
        voiceInstruction: String,
        totalDistance: Double,
        distanceRemaining: Float,
        durationRemaining: Double
    ) {
        val dataClient = Wearable.getDataClient(this)
        val path = "/navigation/instructions"

        val dataMap = PutDataMapRequest.create(path).apply {
            dataMap.putDouble("distanceToNextTurn", distanceToNextTurn)
            dataMap.putString("voiceInstruction", voiceInstruction)
            dataMap.putDouble("totalDistance", totalDistance)
            dataMap.putFloat("distanceRemaining", distanceRemaining)
            dataMap.putDouble("durationRemaining", durationRemaining)
        }

        dataClient.putDataItem(dataMap.asPutDataRequest()).addOnSuccessListener {
            Log.d("PhoneData", "내비게이션 지시 데이터 전송 성공")
        }.addOnFailureListener { e ->
            Log.e("PhoneData", "내비게이션 지시 데이터 전송 실패", e)
        }
    }


    private fun captureTrackingSnapshot() {
        Log.d("NaviActivity", "🟢 captureTrackingSnapshot() 호출됨")

        if (!this::mapView.isInitialized || mapView == null) {
            Log.e("TrackingSnapshot", "❌ mapView가 초기화되지 않음! 캡처 중단")
            return
        }

        if (trackedPath.size < 2) {
            Log.e("TrackingSnapshot", "❌ 트래킹 포인트가 부족하여 스냅샷을 캡처할 수 없음")
            return
        }

        // 1️⃣ 경로 바운딩 박스 계산 (최소/최대 좌표 찾기)
        val routeBounds = trackedPath.fold(null as Pair<Point, Point>?) { bounds, point ->
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

    // ✅ 정사각형 크롭 함수 (중앙 기준)
    private fun cropBitmapToSquare(bitmap: Bitmap): Bitmap {
        val width = bitmap.width
        val height = bitmap.height
        val size = minOf(width, height) // 정사각형 크기 설정 (가장 작은 변을 기준)

        val xOffset = (width - size) / 2
        val yOffset = (height - size) / 2

        return Bitmap.createBitmap(bitmap, xOffset, yOffset, size, size)
    }


    // 🚀 **이미지 업로드 (서버에 저장)**
    private suspend fun uploadImage(bitmap: Bitmap): String? {
        return withContext(Dispatchers.IO) {
            val timestamp = System.currentTimeMillis() // ✅ 현재 시간(밀리초) 추가
            val file = File(cacheDir, "tracking_snapshot_$timestamp.jpg") // ✅ 파일명에 타임스탬프 추가

            FileOutputStream(file).use { out ->
                bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out)
            }

            val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
            val body = MultipartBody.Part.createFormData("file", file.name, requestFile)

            try {
                val imageUploadApi = RetrofitInstance.ImageUploadApi(this@NaviActivity)
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

    override fun onDestroy() {
        super.onDestroy()
        Log.d("NaviActivity", "🛑 onDestroy() 호출됨 - mapView 유지")

        // ❌ 직접 `onDestroy()` 호출 X → MapboxNavigationProvider 사용
        if (MapboxNavigationProvider.isCreated()) {
            MapboxNavigationProvider.destroy()
            Log.d("NaviActivity", "🛑 MapboxNavigation 인스턴스가 안전하게 삭제됨")
        }
    }

//    // 🚀 **도착 시 모달 다이얼로그 표시 (캡처된 이미지 사용)**
//    private fun showArrivalDialog(context: Context, distanceInKm: Double, time: Int, onComplete: () -> Unit) {
//        val dialogView = AlertDialog.Builder(context).setView(R.layout.dialog_arrival).create()
//
//        val imageView = dialogView.findViewById<ImageView>(R.id.trackingSnapshotImageView)
//        val finishButton = dialogView.findViewById<Button>(R.id.finishRunButton)
//
//        if (trackingSnapshotUrl != null) {
//            if (imageView != null) {
//                Glide.with(context).load(trackingSnapshotUrl).into(imageView)
//            }
//        } else {
//            if (imageView != null) {
//                Glide.with(context)
//                    .asGif()
//                    .load(R.drawable.gps_art_run_done)
//                    .diskCacheStrategy(DiskCacheStrategy.ALL)
//                    .into(imageView)
//            }
//        }
//
//        finishButton?.setOnClickListener {
//            dialogView.dismiss()
//            onComplete()
//        }
//
//        dialogView.show()
//    }
}
