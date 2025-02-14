package com.example.drawrun.ui.navi

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.drawrun.databinding.ActivityNaviBinding
import com.example.drawrun.dto.course.PathPoint
import com.mapbox.geojson.LineString
import com.mapbox.geojson.Point
import com.mapbox.maps.CameraOptions
import com.mapbox.maps.MapView
import com.mapbox.maps.MapboxMap
import com.mapbox.maps.Style
import com.mapbox.maps.extension.localization.localizeLabels
import com.mapbox.maps.plugin.annotation.annotations
import com.mapbox.maps.plugin.annotation.generated.PolylineAnnotationManager
import com.mapbox.maps.plugin.annotation.generated.PolylineAnnotationOptions
import com.mapbox.maps.plugin.annotation.generated.createPolylineAnnotationManager
import java.util.Locale

class NaviActivity : AppCompatActivity() {
    private lateinit var binding: ActivityNaviBinding
    private lateinit var mapboxMap: MapboxMap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNaviBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // ✅ Mapbox 초기화
        binding.mapView?.let { mapView ->
            mapboxMap = mapView.getMapboxMap()
            mapboxMap.loadStyleUri(Style.DARK) {style ->
                style.localizeLabels(Locale("ko")) // 지도 라벨 한글화
            }
        }

        // ✅ 전달된 데이터 받기
        val path = intent.getParcelableArrayListExtra<PathPoint>("path") ?: emptyList()
        val startLocation = intent.getStringExtra("startLocation") ?: "정보 없음"
        val distance = intent.getDoubleExtra("distance", 0.0)

        Log.d("pathpath", "${path}")

        // ✅ 지도 중심 이동
        moveToPathStart(path)

        // ✅ 지도에 경로 그리기
        drawRouteOnMap(path)

        // ✅ UI 업데이트
        binding.startLocation.text = "📍 $startLocation"
        binding.distance.text = "📏 거리: ${distance} km"
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



    // ✅ `path` 데이터를 지도에 `Polyline`으로 표시
    private fun drawRouteOnMap(path: List<PathPoint>) {
        if (path.isNotEmpty()) {
            val lineString = LineString.fromLngLats(path.map { Point.fromLngLat(it.longitude, it.latitude) })

            val annotationApi = binding.mapView.annotations
            val polylineAnnotationManager = annotationApi.createPolylineAnnotationManager()

            polylineAnnotationManager.create(
                PolylineAnnotationOptions()
                    .withLineColor("#FF0000") // 🔴 경로 색상 (빨강)
                    .withLineWidth(5.0) // 📏 경로 두께
                    .withPoints(lineString.coordinates()) // 📌 경로 좌표 추가
            )
        }
    }
}
