package com.example.drawrun.ui.masterpiece.fragment

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.drawrun.data.dto.response.masterpiece.Masterpiece
import com.example.drawrun.data.dto.response.masterpiece.SectionInfo
import com.example.drawrun.data.repository.MasterpieceRepository
import com.example.drawrun.databinding.FragmentMasterpieceDetailBinding
import com.example.drawrun.ui.masterpiece.adapter.SectionInfoAdapter
import com.example.drawrun.utils.RetrofitInstance
import com.example.drawrun.viewmodel.MasterpieceViewModel
import com.example.drawrun.viewmodel.MasterpieceViewModelFactory
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

    private lateinit var viewModel: MasterpieceViewModel

    private lateinit var sectionInfoAdapter: SectionInfoAdapter
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

        Log.d("masterpiecemasterpiece  ", "onViewCreated started")

        val repository = MasterpieceRepository(RetrofitInstance.MasterpieceApi(requireContext()))
        val viewModelFactory = MasterpieceViewModelFactory(repository)
        viewModel = ViewModelProvider(this, viewModelFactory)[MasterpieceViewModel::class.java]

        // 전달받은 데이터 가져오기

        val masterpiece = arguments?.getSerializable("masterpiece") as? Masterpiece
        Log.d("masterpiecemasterpiece  ", "받은 마스터피스 데이터: $masterpiece")

        // 전달받은 masterpieceBoardId 가져오기
        val masterpieceBoardId = masterpiece?.masterpieceBoardId
        if (masterpieceBoardId == null) {
            Log.e("masterpiecemasterpiece", "masterpieceBoardId is null")
            return
        }

        Log.d("masterpiecemasterpiece", "Fetching masterpiece detail for id: $masterpieceBoardId")

        // 데이터 요청 및 관찰
        viewModel.fetchMasterpieceDetail(masterpieceBoardId)
        viewModel.masterpieceDetail.observe(viewLifecycleOwner) { detail ->
            if (detail != null) {
                Log.d("masterpiecemasterpiece", "Received Detail: $detail")
                // UI 업데이트 추가해야 함

            } else {
                Log.e("masterpiecemasterpiece", "Failed to fetch detail")
            }
        }


            // MapboxNavigation 초기화
        mapboxNavigation = MapboxNavigationProvider.create(
            NavigationOptions.Builder(requireContext()).build()
        )

        // 지도 초기화 및 스타일 로드
        binding.mapView.getMapboxMap().loadStyleUri(Style.DARK) { style ->
            style.localizeLabels(Locale("ko")) // 지도 라벨 한글화
            binding.mapView.scalebar.enabled = false

            polylineAnnotationManager = binding.mapView.annotations.createPolylineAnnotationManager()

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

        setupRecyclerView()
        observeSectionInfo()

        // masterpieceBoardId를 사용하여 구간 정보 요청
        viewModel.fetchMasterpieceSectionInfo(masterpieceBoardId)
    }

    private fun setupRecyclerView() {
        sectionInfoAdapter = SectionInfoAdapter()
        binding.sectionInfoList.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = sectionInfoAdapter
        }
    }

    private fun observeSectionInfo() {
        viewModel.sectionInfo.observe(viewLifecycleOwner) { sectionInfoResponse ->
            sectionInfoResponse?.let {
                sectionInfoAdapter.updateSections(it)
                requestWalkingRoutes(it)
                adjustCamera(it)
            }
        }
    }

    private fun requestWalkingRoutes(sections: List<SectionInfo>) {
        val colors = generateColors(sections.size)
        sections.forEachIndexed { index, section ->
            val coordinates = section.path.map { Point.fromLngLat(it.longitude, it.latitude) }
            requestWalkingRoute(coordinates, index, colors[index])
        }
    }

    private fun requestWalkingRoute(coordinates: List<Point>, sectionIndex: Int, color: String) {
        mapboxNavigation.requestRoutes(
            RouteOptions.builder()
                .applyDefaultNavigationOptions()
                .profile(DirectionsCriteria.PROFILE_WALKING)
                .coordinatesList(coordinates)
                .build(),
            object : NavigationRouterCallback {
                override fun onRoutesReady(routes: List<NavigationRoute>, routerOrigin: String) {
                    val route = routes.firstOrNull()
                    if (route != null) {
                        val distance = route.directionsRoute.distance()
                        sectionInfoAdapter.updateDistance(sectionIndex, distance)
                        sectionInfoAdapter.updateColor(sectionIndex, color)
                        drawRouteOnMap(route, sectionIndex, color)
                    }
                }

                override fun onFailure(reasons: List<RouterFailure>, routeOptions: RouteOptions) {
                    Log.e("MasterpieceDetail", "Failed to get route for section $sectionIndex")
                }

                override fun onCanceled(routeOptions: RouteOptions, routerOrigin: String) {
                    Log.d("MasterpieceDetail", "Route request canceled for section $sectionIndex")
                }
            }
        )
    }


    private fun drawRouteOnMap(route: NavigationRoute, sectionIndex: Int, color: String) {
        val routeCoordinates = route.directionsRoute.geometry()?.let {
            LineString.fromPolyline(it, 6).coordinates()
        } ?: return

        val polylineOptions = PolylineAnnotationOptions()
            .withPoints(routeCoordinates)
            .withLineColor(color)
            .withLineWidth(5.0)

        polylineAnnotationManager.create(polylineOptions)
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

    private fun adjustCamera(sections: List<SectionInfo>) {
        val allCoordinates = sections.flatMap { it.path.map { coord -> Point.fromLngLat(coord.longitude, coord.latitude) } }
        val cameraOptions = binding.mapView.getMapboxMap().cameraForCoordinates(
            allCoordinates,
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
