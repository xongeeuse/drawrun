package com.example.drawrun.ui.map.fragment

import android.graphics.Bitmap
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
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

        // 이미지 URL을 사용하여 이미지 로드
        arguments?.getString("image_path")?.let { imageUrl ->
            loadImageFromUrl(imageUrl)
        }
    }



    private fun loadImageFromUrl(imageUrl: String) {
        // Glide 또는 Picasso 등의 이미지 로딩 라이브러리를 사용하여 이미지 로드
        Glide.with(this)
            .load(imageUrl)
            .into(binding.capturedMapImage)

        // 이미지뷰의 높이를 5:7 비율로 설정
        binding.capturedMapImage.post {
            val width = binding.capturedMapImage.width
            val params = binding.capturedMapImage.layoutParams
            params.height = (width * 1.4).toInt()  // 7/5 = 1.4
            binding.capturedMapImage.layoutParams = params
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