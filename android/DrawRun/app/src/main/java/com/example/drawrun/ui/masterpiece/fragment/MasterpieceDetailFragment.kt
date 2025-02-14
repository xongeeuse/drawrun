package com.example.drawrun.ui.masterpiece.fragment

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.drawrun.data.dto.response.masterpiece.Masterpiece
import com.example.drawrun.databinding.FragmentMasterpieceDetailBinding
import com.mapbox.api.directions.v5.DirectionsCriteria
import com.mapbox.api.directions.v5.models.RouteOptions
import com.mapbox.geojson.LineString
import com.mapbox.geojson.Point
import com.mapbox.maps.CoordinateBounds
import com.mapbox.maps.EdgeInsets
import com.mapbox.maps.Style
import com.mapbox.maps.extension.localization.localizeLabels
import com.mapbox.maps.plugin.annotation.annotations
import com.mapbox.maps.plugin.annotation.generated.PolylineAnnotationManager
import com.mapbox.maps.plugin.annotation.generated.PolylineAnnotationOptions
import com.mapbox.maps.plugin.annotation.generated.createPolylineAnnotationManager
import com.mapbox.maps.plugin.scalebar.scalebar
import com.mapbox.navigation.base.extensions.applyDefaultNavigationOptions
import com.mapbox.navigation.base.options.NavigationOptions
import com.mapbox.navigation.base.route.NavigationRoute
import com.mapbox.navigation.base.route.NavigationRouterCallback
import com.mapbox.navigation.base.route.RouterFailure
import com.mapbox.navigation.core.MapboxNavigation
import com.mapbox.navigation.core.MapboxNavigationProvider
import java.util.Locale

class MasterpieceDetailFragment : Fragment() {

    private var _binding: FragmentMasterpieceDetailBinding? = null
    private val binding get() = _binding!!

    private lateinit var polylineAnnotationManager: PolylineAnnotationManager
    private lateinit var mapboxNavigation: MapboxNavigation // MapboxNavigation 객체 선언

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMasterpieceDetailBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 전달받은 데이터 가져오기
        val masterpiece = arguments?.getSerializable("masterpiece") as? Masterpiece

        // MapboxNavigation 초기화
        mapboxNavigation = MapboxNavigationProvider.create(
            NavigationOptions.Builder(requireContext()).build()
        )

        // 지도 초기화 및 스타일 로드
        binding.mapView.getMapboxMap().loadStyleUri(Style.DARK) { style ->
            style.localizeLabels(Locale("ko")) // 지도 라벨 한글화
            binding.mapView.scalebar.enabled = false

            polylineAnnotationManager = binding.mapView.annotations.createPolylineAnnotationManager()

            val coordinates = extractCoordinatesFromJson() // JSON에서 좌표 추출
            requestWalkingRoute(coordinates) // 경로 요청

            // 카메라 조정 (좌표 범위 설정)
            val bounds = CoordinateBounds(
                Point.fromLngLat(128.8787948694096, 35.08560045113647),
                Point.fromLngLat(128.8819939469559, 35.08785261167259)
            )
            val cameraOptions = binding.mapView.getMapboxMap().cameraForCoordinateBounds(
                bounds,
                EdgeInsets(200.0, 200.0, 200.0, 200.0),
                null,
                null
            )
            binding.mapView.getMapboxMap().setCamera(cameraOptions)
        }

        // RecyclerView 초기화 (구간 정보 표시)
        binding.sectionInfoList.layoutManager = LinearLayoutManager(requireContext())
    }

    private fun requestWalkingRoute(coordinates: List<Point>) {
        mapboxNavigation.requestRoutes(
            RouteOptions.builder()
                .applyDefaultNavigationOptions()
                .profile(DirectionsCriteria.PROFILE_WALKING) // 도보 프로필 설정
                .language("ko") // 한국어 설정
                .steps(true) // 상세 단계 요청
                .voiceUnits(DirectionsCriteria.METRIC) // 거리 단위 설정 (미터)
                .coordinatesList(coordinates) // 좌표 리스트 설정
                .waypointIndicesList((0 until coordinates.size).toList()) // 모든 인덱스 설정 (출발지, 도착지 및 경유지)
                .waypointNamesList(List(coordinates.size) { index ->
                    when (index) {
                        0 -> "출발지"
                        coordinates.size - 1 -> "도착지"
                        else -> "경유지 $index"
                    }
                })
                .build(),
            object : NavigationRouterCallback {
                override fun onRoutesReady(routes: List<NavigationRoute>, routerOrigin: String) {
                    val route = routes.firstOrNull()
                    if (route != null) {
                        val routeCoordinates = route.directionsRoute.geometry()?.let {
                            LineString.fromPolyline(it, 6).coordinates()
                        } ?: emptyList()

                        // 각 구간별 거리 정보 출력
                        route.directionsRoute.legs()?.forEachIndexed { index, leg ->
                            val legDistance = leg.distance() // 구간 전체 거리 (미터)
                            Toast.makeText(
                                context,
                                "구간 $index 거리: ${legDistance} m",
                                Toast.LENGTH_SHORT
                            ).show()

//                            // 각 단계별 거리 정보 출력
//                            leg.steps()?.forEachIndexed { stepIndex, step ->
//                                val stepDistance = step.distance() // 단계별 거리 (미터)
//                                println("구간 $index, 단계 $stepIndex 거리: ${stepDistance} m")
//                            }
                        }

                        // 4구간으로 나누어 그리기
                        drawRouteInSections(routeCoordinates, 4)

                        // 카메라 조정 (경로에 맞춰 조정)
                        adjustCamera(routeCoordinates) // 경로에 맞춰 카메라 조정
                    }
                }

                override fun onFailure(reasons: List<RouterFailure>, routeOptions: RouteOptions) {
                    // 경로 요청 실패 처리
                    Toast.makeText(context, "경로 요청 실패", Toast.LENGTH_SHORT).show()
                }

                override fun onCanceled(routeOptions: RouteOptions, routerOrigin: String) {
                    // 경로 요청 취소 처리 (필요 시 구현)
                }
            }
        )
    }

    private fun generateColors(count: Int): List<String> {
        return (0 until count).map { i ->
            val hue = (i * 360f / count) % 360f
            val hsv = floatArrayOf(hue, 1f, 1f)
            val color = Color.HSVToColor(hsv)
            String.format("#%06X", 0xFFFFFF and color)
        }
    }


    private fun drawRouteInSections(routeCoordinates: List<Point>, sections: Int) {
        polylineAnnotationManager.deleteAll() // 기존 경로 삭제

        val sectionSize = routeCoordinates.size / sections
        val colors = generateColors(sections) // 랜덤 색상 선택 사용

        for (i in 0 until sections) {
            val start = i * sectionSize
            val end = if (i == sections - 1) routeCoordinates.size else (i + 1) * sectionSize
            val sectionCoordinates = routeCoordinates.subList(start, end)

            val polylineOptions = PolylineAnnotationOptions()
                .withPoints(sectionCoordinates)
                .withLineColor(colors[i])
                .withLineWidth(5.0)

            polylineAnnotationManager.create(polylineOptions)
        }
    }

    private fun adjustCamera(routeCoordinates: List<Point>) {
        val cameraOptions = binding.mapView.getMapboxMap().cameraForCoordinates(
            routeCoordinates,
            EdgeInsets(100.0, 100.0, 100.0, 100.0),
            null,
            null
        )
        binding.mapView.getMapboxMap().setCamera(cameraOptions)
    }

    private fun extractCoordinatesFromJson(): List<Point> {
        return listOf(
            Point.fromLngLat(128.88189078375206, 35.08677284699564),
            Point.fromLngLat(128.8819939469559, 35.08621769582258),
            Point.fromLngLat(128.88189078375206, 35.08561506086515),
            Point.fromLngLat(128.88118090232263, 35.08567181529165),
            Point.fromLngLat(128.8799691627463, 35.08560045113647),
            Point.fromLngLat(128.87998553227607, 35.08619415246808),
            Point.fromLngLat(128.88117245103422, 35.08619414722901),
            Point.fromLngLat(128.881173951037, 35.086657644114354),
            Point.fromLngLat(128.87995654687, 35.086703796420196),
            Point.fromLngLat(128.87994786146623, 35.08730019496652),
            Point.fromLngLat(128.8788491628615, 35.08724513336804),
            Point.fromLngLat(128.87906830382883, 35.08779816664506),
            Point.fromLngLat(128.8787948694096, 35.08785261167259)
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        polylineAnnotationManager.deleteAll() // 뷰가 파괴될 때 모든 주석 삭제
//        mapboxNavigation.onDestroy() // MapboxNavigation 정리
        _binding = null
    }
}
