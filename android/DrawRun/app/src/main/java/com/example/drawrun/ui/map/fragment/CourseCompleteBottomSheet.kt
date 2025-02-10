package com.example.drawrun.ui.map.fragment

import android.graphics.Bitmap
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.drawrun.data.model.ParcelablePoint
import com.example.drawrun.data.repository.CourseRepository
import com.example.drawrun.databinding.BottomSheetCourseCompleteBinding
import com.example.drawrun.utils.RetrofitInstance
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.mapbox.geojson.Point

class CourseCompleteBottomSheet : BottomSheetDialogFragment() {
    private var _binding: BottomSheetCourseCompleteBinding? = null
    private val binding get() = _binding!!

    // 거리와 이미지를 전달받기 위한 companion object
    // 여기에 companion object 작성
    companion object {
        fun newInstance(distance: Double, imagePath: String, points: List<ParcelablePoint>): CourseCompleteBottomSheet {
            return CourseCompleteBottomSheet().apply {
                arguments = Bundle().apply {
                    putDouble("distance", distance)
                    putString("image_path", imagePath)
                    putParcelableArrayList("points", ArrayList(points))
                }
            }
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = BottomSheetCourseCompleteBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViews()

        // 거리 정보 설정
        arguments?.getDouble("distance")?.let { distance ->
            binding.tvDistance.text = String.format("%.2fKM", distance)
            Log.d("BottomSheet", "Distance set: $distance")
        }

        // 이미지 설정 시 API 레벨 체크
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            arguments?.getParcelable("captured_image", Bitmap::class.java)?.let { bitmap ->
                Log.d("BottomSheet", "Bitmap setting (API 33+): ${bitmap.width}x${bitmap.height}")
                binding.capturedMapImage.setImageBitmap(bitmap)
            } ?: Log.e("BottomSheet", "Failed to get bitmap (API 33+)")
        } else {
            @Suppress("DEPRECATION")
            arguments?.getParcelable<Bitmap>("captured_image")?.let { bitmap ->
                Log.d("BottomSheet", "Bitmap setting (Legacy): ${bitmap.width}x${bitmap.height}")
                binding.capturedMapImage.setImageBitmap(bitmap)
            } ?: Log.e("BottomSheet", "Failed to get bitmap (Legacy)")
        }

    }


    private fun setupViews() {
        binding.btnSoloRun.setOnClickListener {
            // 현재 BottomSheet 닫기
            dismiss()

            // 이미지 경로, 거리, 포인트 리스트 가져오기
            val imagePath = arguments?.getString("image_path") ?: ""
            val distance = arguments?.getDouble("distance") ?: 0.0
            // ParcelablePoint 리스트를 Point 리스트로 변환
            val points = arguments?.getParcelableArrayList<ParcelablePoint>("points")
                ?.map { it.point } ?: emptyList()

            // CourseRepository 인스턴스 생성
            val courseApi = RetrofitInstance.CourseApi(requireContext())
            val courseRepository = CourseRepository(courseApi)

            // SoloRunBottomSheet 표시
            SoloRunBottomSheet.newInstance(distance, imagePath, points, courseRepository)
                .show(parentFragmentManager, "SoloRunBottomSheet")
        }

        binding.btnGroupRun.setOnClickListener {
            dismiss()
            // 함께 달려요 BottomSheet 표시
            val imagePath = arguments?.getString("image_path") ?: ""
            val distance = arguments?.getDouble("distance") ?: 0.0

            GroupRunBottomSheet.newInstance(distance, imagePath)
                .show(parentFragmentManager, "GroupRunBottomSheet")
            }
        }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

/*1. 그림 생성 및 경로 변환
- 사용자가 찍은 좌표점들 수집
- Mapbox Navigation API로 도보 경로 생성
- 경로가 지도에 그려짐
2. 이미지 처리
- 경로가 그려진 지도 화면 캡처
- Multipart 형식으로 이미지 서버에 업로드
- 서버로부터 이미지 URL 수신
3. 코스 정보 입력
- CourseCompleteBottomSheet에서 사용자 입력 받음
- 코스 제목
- 공개/비공개 설정
- 이미지 URL과 함께 코스 정보 저장 API 호출
4. 운동 시작
- 바로 달리기 선택 시
- 코스 저장 디폴트 깔고
- 운동 기록 시작하고
- 실시간 내비게이션 안내 시작*/

// MapActivity에 추가할 부분
//// 화면 캡쳐 함수 추가
//private fun captureMapView(): Bitmap? {
//    //        val mapView = binding.mapView
//    return try {
//        // 지도 뷰의 크기만큼 비트맵 생성
//        val bitmap = Bitmap.createBitmap(
//            mapView.width,
//            mapView.height,
//            Bitmap.Config.ARGB_8888
//        )
//        val canvas = Canvas(bitmap)
//        mapView.draw(canvas)
//        bitmap
//    } catch (e: Exception) {
//        e.printStackTrace()
//        null
//    }
//}
//
//// 이미지 저장 함수 추가
//private fun saveMapImage(bitmap: Bitmap) {
//    try {
//        // 파일로 저장
//        val file = File(cacheDir, "course_map.jpg")
//        FileOutputStream(file).use { out ->
//            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out)
//        }
//
//        Log.d("MapImage", "이미지 저장 성공: ${file.absolutePath}")
//        Log.d("MapImage", "이미지 크기: ${file.length() / 1024}KB")
//
//        //            // 또는 Base64 문자열로 변환
//        //            val byteArrayOutputStream = ByteArrayOutputStream()
//        //            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, byteArrayOutputStream)
//        //            val byteArray = byteArrayOutputStream.toByteArray()
//        //            val base64String = Base64.encodeToString(byteArray, Base64.DEFAULT)
//        //
//        //            // 이미지 업로드 API 호출
//        //            uploadMapImage(file) // 또는 base64String 사용
//    } catch (e: Exception) {
//        Log.e("MapImage", "이미지 저장 실패: ${e.message}")
//        e.printStackTrace()
//    }
//}
//
//// BottomSheet 호출
//private fun showCourseCompleteBottomSheet(distance: Double, capturedImage: Bitmap) {
//    // 캐시에서 이미지 파일 읽기
//    val file = File(cacheDir, "course_map.jpg")
//    if (file.exists()) {
//        try {
//            val bitmap = BitmapFactory.decodeFile(file.absolutePath)
//            bitmap?.let {
//                CourseCompleteBottomSheet.newInstance(distance, it, points)
//                    .show(supportFragmentManager, "CourseCompleteBottomSheet")
//            }
//        } catch (e: Exception) {
//            Log.e("MapActivity", "Failed to load image: ${e.message}")
//        }
//    }
//}
//
//
//
//// 경로 요청 시 화면 캡쳐 함수 호출
//
//private fun requestRoute(points: List<Point>) {
//    mapboxNavigation.requestRoutes(
//        RouteOptions.builder()
//            .applyDefaultNavigationOptions()
//            .profile(DirectionsCriteria.PROFILE_WALKING) // 도보 경로 설정
//            .language("ko")
//            .steps(true)
//            .voiceUnits(DirectionsCriteria.METRIC)  // 거리 단위(미터)
//            .coordinatesList(points) // 좌표 리스트 설정
//            .waypointIndicesList((0 until points.size).toList())
//            .waypointNamesList(List(points.size) { index ->
//                when (index) {
//                    0 -> "출발지"
//                    points.size - 1 -> "도착지"
//                    else -> "경유지 $index"
//                }
//            })
//            .build(),
//        object : NavigationRouterCallback {
//            @SuppressLint("MissingPermission")
//            override fun onRoutesReady(routes: List<NavigationRoute>, routerOrigin: String) {
//                routes.firstOrNull()?.let { route ->
//                    routeLineApi.setNavigationRoutes(listOf(route)) { value ->
//                        mapView.getMapboxMap().getStyle()?.apply {
//                            routeLineView.renderRouteDrawData(this, value)
//
////                                    // 경로가 그려진 후 약간의 딜레이를 두고 캡쳐
////                                    Handler(Looper.getMainLooper()).postDelayed({
////                                        captureMapView()?.let { bitmap ->
////                                            // 캡처된 비트맵 처리
////                                            saveMapImage(bitmap)
////                                            // route.distance()로 총 거리를 얻어옴 (미터 단위)
////                                            val distanceInKm = route.directionsRoute.distance() / 1000
////                                            // 거리 정보와 캡처된 이미지로 바텀시트 표시
////                                            showCourseCompleteBottomSheet(distanceInKm, bitmap)
////                                        }
////                                    }, 500) // 500ms 딜레이
//                        }
//                    }
//                    mapboxNavigation.startTripSession()
//                    mapboxNavigation.setNavigationRoutes(listOf(route))
//                    mapboxNavigation.registerRouteProgressObserver(routeProgressObserver)
//                }
//            }
//
//            override fun onFailure(reasons: List<RouterFailure>, routeOptions: RouteOptions) {}
//            override fun onCanceled(routeOptions: RouteOptions, routerOrigin: String) {}
//        }
//    )
//}