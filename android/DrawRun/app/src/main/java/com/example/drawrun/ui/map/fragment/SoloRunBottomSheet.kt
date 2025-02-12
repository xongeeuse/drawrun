package com.example.drawrun.ui.map.fragment

import android.app.Dialog
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.LifecycleOwner
import com.example.drawrun.databinding.BottomSheetSoloRunBinding
import com.example.drawrun.viewmodel.CourseViewModel
import com.example.drawrun.data.repository.CourseRepository
import com.example.drawrun.viewmodel.CourseViewModelFactory
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.mapbox.geojson.Point
import java.io.File

class SoloRunBottomSheet(private val courseRepository: CourseRepository) : BottomSheetDialogFragment() {
    private var _binding: BottomSheetSoloRunBinding? = null
    private val binding get() = _binding!!

    // ViewModel 주입
    private lateinit var courseViewModel: CourseViewModel // ViewModel을 나중에 초기화하도록 변경

    companion object {
        fun newInstance(distance: Double, imagePath: String, points: List<Point>, courseRepository: CourseRepository): SoloRunBottomSheet {
            return SoloRunBottomSheet(courseRepository).apply {
                arguments = Bundle().apply {
                    putDouble("distance", distance)
                    putString("image_path", imagePath)
                    // Point 객체의 좌표값들을 별도의 ArrayList로 저장
                    putDoubleArray("latitudes", points.map { it.latitude() }.toDoubleArray())
                    putDoubleArray("longitudes", points.map { it.longitude() }.toDoubleArray())
                }
            }
        }
    }


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val bottomSheetDialog = BottomSheetDialog(requireContext(), theme)
        bottomSheetDialog.behavior.apply {
            state = BottomSheetBehavior.STATE_EXPANDED  // 확장된 상태로 시작
//            isDraggable = false  // 드래그 비활성화 (선택사항)
        }
        return bottomSheetDialog
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        dialog?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)
        _binding = BottomSheetSoloRunBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // ViewModel 초기화 코드를 여기에 추가합니다.
        val factory = CourseViewModelFactory(courseRepository)
        courseViewModel = ViewModelProvider(this, factory)[CourseViewModel::class.java]

        setupViews()
        loadImage()
        observeViewModel() // ViewModel 관찰 메서드 호출 추가
    }

    private fun setupViews() {
        // 거리 정보 설정
        arguments?.getDouble("distance")?.let { distance ->
            val formattedDistance = String.format("%.2f", distance)
            binding.tvDistance.text = "${formattedDistance}km"
        }

        // 키보드 엔터 처리
        binding.etCourseName.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(v.windowToken, 0)
                true
            } else {
                false
            }
        }

        // EditText 포커스 변경 리스너 추가
        binding.etCourseName.setOnFocusChangeListener { view, hasFocus ->
            if (!hasFocus) {
                val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(view.windowToken, 0)
            }
        }

        // 버튼 클릭 리스너 설정
        binding.btnSave.setOnClickListener {
            // 코스 저장 로직 구현
            val courseName = binding.etCourseName.text.toString()
            if (courseName.isBlank()) {
                Toast.makeText(requireContext(), "코스 이름을 입력해주세요", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // 공개 설정 값 가져오기
            val isPublic = binding.switchPublic.isChecked

            arguments?.let { args ->
                val distance = args.getDouble("distance")
                val formattedDistance = String.format("%.2f", distance).toDouble()
                val imagePath = args.getString("image_path") ?: return@let
                // arguments에서 좌표 배열을 가져와서 Point 리스트로 변환
                val latitudes = args.getDoubleArray("latitudes") ?: return@let
                val longitudes = args.getDoubleArray("longitudes") ?: return@let
                val points = latitudes.zip(longitudes) { lat, lng ->
                    com.example.drawrun.data.dto.request.course.Point(
                        latitude = lat,
                        longitude = lng
                    )
                }

                // ViewModel의 saveCourse 호출
                courseViewModel.saveCourse(
                    path = points,           // MapActivity에서 전달받아야 할 경로 데이터
                    name = courseName,
                    pathImgUrl = imagePath,
                    distance = formattedDistance,
//                    isPublic = isPublic
                )
            }
        }

        binding.btnStart.setOnClickListener {
            // 코스 저장부터 하고
            binding.btnSave.performClick()
            // 저장 완료 후 달리기 화면 이동 로직 추가
        }
    }

    // ViewModel 관찰 메서드 추가
    private fun observeViewModel() {
        courseViewModel.saveCourseResult.observe(viewLifecycleOwner) { result ->
            result.onSuccess { courseId ->
                Toast.makeText(requireContext(), "코스가 저장되었습니다. CourseId: $courseId", Toast.LENGTH_SHORT).show()
                dismiss()
            }.onFailure { exception ->
                Toast.makeText(
                    requireContext(),
                    "저장에 실패했습니다: ${exception.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }



    private fun loadImage() {
        arguments?.getString("image_path")?.let { imageUrl ->
            Glide.with(this)
                .load(imageUrl)
                .into(binding.capturedMapImage)

            binding.capturedMapImage.visibility = View.VISIBLE

            binding.capturedMapImage.post {
                val width = binding.capturedMapImage.width
                val params = binding.capturedMapImage.layoutParams
                params.height = (width * 1.4).toInt()
                binding.capturedMapImage.layoutParams = params
            }
        }
    }



    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
