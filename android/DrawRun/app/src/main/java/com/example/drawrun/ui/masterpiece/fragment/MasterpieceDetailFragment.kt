package com.example.drawrun.ui.masterpiece.fragment

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.drawrun.R
import com.example.drawrun.data.dto.response.masterpiece.Masterpiece
import com.example.drawrun.data.dto.response.masterpiece.SectionInfo
import com.example.drawrun.data.repository.MasterpieceRepository
import com.example.drawrun.databinding.FragmentMasterpieceDetailBinding
import com.example.drawrun.dto.course.PathPoint
import com.example.drawrun.ui.masterpiece.adapter.SectionInfoAdapter
import com.example.drawrun.ui.navi.NaviActivity
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

        // MapboxNavigation 초기화
        mapboxNavigation = MapboxNavigationProvider.create(
            NavigationOptions.Builder(requireContext()).build()
        )

        val repository = MasterpieceRepository(RetrofitInstance.MasterpieceApi(requireContext()))
        val viewModelFactory = MasterpieceViewModelFactory(repository)
        viewModel = ViewModelProvider(this, viewModelFactory)[MasterpieceViewModel::class.java]

        // 전달받은 데이터 가져오기

//        val masterpiece = arguments?.getSerializable("masterpiece") as? Masterpiece
//        Log.d("masterpiecemasterpiece  ", "받은 마스터피스 데이터: $masterpiece")

        // 전달받은 masterpieceBoardId 가져오기
        val masterpieceBoardId = arguments?.getInt("masterpieceBoardId")
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

                // 지도 위에 오버레이 정보 업데이트
                binding.courseNameOverlay.text = detail.courseName
//                binding.ddayOverlay.text = if (detail.dday == 0) "D - day" else "D - ${detail.dday}"
                binding.distanceOverlay.text = "${detail.distance} km"
                binding.nicknameOverlay.text = detail.nickname

                // 지도 카메라 위치 조정 및 경로 표시
//                updateMap(detail)

            } else {
                Log.e("masterpiecemasterpiece", "Failed to fetch detail")
            }
        }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                navigateToMasterpieceSearch()
            }
        })


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
        observeJoinResult()

        // masterpieceBoardId를 사용하여 구간 정보 요청
        viewModel.fetchMasterpieceSectionInfo(masterpieceBoardId)


    }

    private fun observeJoinResult() {
        viewModel.joinMasterpieceResult.observe(viewLifecycleOwner) { isSuccess ->
            if (isSuccess) {
                Toast.makeText(context, "마스터피스 참여 성공", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, "마스터피스 참여 실패", Toast.LENGTH_SHORT).show()
            }
        }
    }


    private fun setupRecyclerView() {
        sectionInfoAdapter = SectionInfoAdapter { sectionInfo, masterpieceBoardId, position ->
            // NaviActivity로 이동하는 로직 추가
            navigateToNaviActivity(sectionInfo, position)
            // 기존의 joinMasterpiece 호출은 유지 > 제거하고 내비액티에서 조인 요청으로 수정
//            viewModel.joinMasterpiece(sectionInfo.masterpieceSegId, masterpieceBoardId, position)
        }

        sectionInfoAdapter.setMasterpieceBoardId(viewModel.masterpieceDetail.value?.masterpieceBoardId ?: 0)

        binding.sectionInfoList.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = sectionInfoAdapter
        }
    }

    private fun navigateToNaviActivity(sectionInfo: SectionInfo, position: Int) {
        // Point 객체로 변환
        val pathPoints = sectionInfo.path.map { PathPoint(it.latitude, it.longitude) }
        val distanceInMeters = sectionInfoAdapter.getDistance(position) ?: 0.0
        val distanceInKm = String.format("%.2f", distanceInMeters / 1000.0).toDouble()

        // NaviActivity로 이동
        val intent = Intent(requireContext(), NaviActivity::class.java).apply {
            putParcelableArrayListExtra("path", ArrayList(pathPoints))
            putExtra("startLocation", sectionInfo.address)
            putExtra("distance", distanceInKm)
            putExtra("isMasterpieceRequest", true)
            putExtra("masterpieceSegId", sectionInfo.masterpieceSegId)
            // 필요한 경우 추가 데이터를 여기에 넣습니다
        }
        startActivity(intent)
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
            requestWalkingRoute(coordinates, index, colors[index], section.nickname)
        }
    }

    private fun requestWalkingRoute(coordinates: List<Point>, sectionIndex: Int, color: String, nickname: String) {
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
                        drawRouteOnMap(route, sectionIndex, color, nickname)
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


    private fun drawRouteOnMap(route: NavigationRoute, sectionIndex: Int, color: String, nickname: String) {
        val routeCoordinates = route.directionsRoute.geometry()?.let {
            LineString.fromPolyline(it, 6).coordinates()
        } ?: return

        // nickname에 따라 투명도 결정
        val alpha = when (nickname) {
            "달리기 시작", "달리는 중" -> 0.2f  // 50% 투명도
            else -> 1.0f  // 완전 불투명
        }

        // 색상에 투명도 적용
        val colorWithAlpha = Color.parseColor(color)
        val colorWithAppliedAlpha = Color.argb(
            (alpha * 255).toInt(),
            Color.red(colorWithAlpha),
            Color.green(colorWithAlpha),
            Color.blue(colorWithAlpha)
        )

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

    private fun navigateToMasterpieceSearch() {
        parentFragmentManager.beginTransaction()
            .replace(R.id.masterpiece_fragment_container, MasterpieceSearchFragment())
            .commit()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        polylineAnnotationManager.deleteAll() // 뷰가 파괴될 때 모든 주석 삭제
        MapboxNavigationProvider.destroy()
        _binding = null
    }
}
