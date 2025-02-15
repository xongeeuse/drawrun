package com.example.drawrun.ui.navi

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.drawrun.R
import com.example.drawrun.databinding.ActivityNaviBinding
import com.example.drawrun.dto.course.PathPoint
import com.mapbox.api.directions.v5.DirectionsCriteria
import com.mapbox.api.directions.v5.models.RouteOptions
import com.mapbox.geojson.LineString
import com.mapbox.geojson.Point
import com.mapbox.maps.CameraOptions
import com.mapbox.maps.MapboxMap
import com.mapbox.maps.Style
import com.mapbox.maps.extension.localization.localizeLabels
import com.mapbox.maps.plugin.annotation.annotations
import com.mapbox.maps.plugin.annotation.generated.PolylineAnnotationManager
import com.mapbox.maps.plugin.annotation.generated.PolylineAnnotationOptions
import com.mapbox.maps.plugin.annotation.generated.createPolylineAnnotationManager
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
import java.util.Locale

class NaviActivity : AppCompatActivity() {
    private lateinit var binding: ActivityNaviBinding
    private lateinit var mapboxMap: MapboxMap

    private var polylineAnnotationManager: PolylineAnnotationManager? = null // 🔹 Polyline 관리 객체
    private lateinit var mapboxNavigation: MapboxNavigation
    private lateinit var routeLineApi: MapboxRouteLineApi
    private lateinit var routeLineView: MapboxRouteLineView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNaviBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Mapbox 내비게이션 초기화
        mapboxNavigation = MapboxNavigationProvider.create(
            NavigationOptions.Builder(this.applicationContext).build()
        )

        // ✅ 전달된 데이터 받기
        val path = intent.getParcelableArrayListExtra<PathPoint>("path") ?: emptyList()
        val startLocation = intent.getStringExtra("startLocation") ?: "정보 없음"
        val distance = intent.getDoubleExtra("distance", 0.0)

        Log.d("pathpath", "${path}")

        // ✅ Mapbox 초기화 (스타일 로드 후 경로 그림 추가)
        binding.mapView?.let { mapView ->
            mapboxMap = mapView.getMapboxMap()
            mapboxMap.loadStyleUri(Style.DARK) { style ->
                style.localizeLabels(Locale("ko")) // 지도 라벨 한글화
                routeLineApi = MapboxRouteLineApi(MapboxRouteLineApiOptions.Builder().build())
                routeLineView = MapboxRouteLineView(MapboxRouteLineViewOptions.Builder(this).build())

                // ✅ 스타일이 로드된 후에 경로를 그림
//                drawRouteOnMap(path)
                moveToPathStart(path) // 경로 이동을 여기서 실행

                // ✅ 도보 경로 요청
                // 🔄 ✅ longitude와 latitude 순서를 바꿔서 전달
                // ✅ 도보 경로 요청 (타입 변경)
                requestWalkingRoute(path.map { Point.fromLngLat(it.latitude, it.longitude) })

            }
        }

        // ✅ UI 업데이트
        binding.startLocation.text = "📍 $startLocation"
        binding.distance.text = "📏 거리: ${distance} km"
    }

    // ✅ 도보 경로 요청 및 지도에 표시
    private fun requestWalkingRoute(path: List<Point>) {
        if (path.size < 2) {
            Log.e("NaviActivity", "경로 요청 실패: 최소 2개 이상의 좌표가 필요합니다.")
            return
        }

        // ✅ 경도(longitude) -> 위도(latitude) 순서로 변환 (순서 변경!)
        val points = path.map { Point.fromLngLat(it.longitude(), it.latitude()) } // 🔄 순서 변경!

        mapboxNavigation.requestRoutes(
            RouteOptions.builder()
                .applyDefaultNavigationOptions()
                .profile(DirectionsCriteria.PROFILE_WALKING) // 도보 경로
                .language("ko")
                .steps(true)
                .voiceUnits(DirectionsCriteria.METRIC)
                .coordinatesList(points)
                .waypointIndicesList((0 until points.size).toList())
                .waypointNamesList(
                    List(points.size) { index ->
                        when (index) {
                            0 -> "출발지"
                            points.size - 1 -> "도착지"
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

                override fun onCanceled(routeOptions: RouteOptions, routerOrigin: String) {
                    Log.e("NaviActivity", "경로 요청이 취소되었습니다.")
                }
            }
        )
    }


    // ✅ 지도 중심을 `path`의 중앙으로 이동
    // ✅ 출발지 (첫 번째 좌표)로 지도 중심 이동
    private fun moveToPathStart(path: List<PathPoint>) {
        if (path.isNotEmpty()) {
            val startPoint = Point.fromLngLat(path.first().latitude, path.first().longitude) // 🔄 순서 변경

            mapboxMap.setCamera(
                CameraOptions.Builder()
                    .center(startPoint) // 출발지를 중심으로 지도 이동
                    .zoom(15.0) // 줌 레벨 조정
                    .build()
            )

            // ✅ 로그 추가 (디버깅용)
            Log.d("NaviActivity", "Moving to start point: ${path.first().longitude}, ${path.first().latitude}")
        }
    }
}
