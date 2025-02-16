package com.example.drawrun.ui.map.fragment

import android.app.DatePickerDialog
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.drawrun.data.dto.request.masterpiece.MasterpieceSaveRequest
import com.example.drawrun.data.repository.CourseRepository
import com.example.drawrun.data.repository.MasterpieceRepository
import com.example.drawrun.databinding.BottomSheetGroupRunBinding
import com.example.drawrun.ui.masterpiece.MasterpieceActivity
import com.example.drawrun.utils.RetrofitInstance
import com.example.drawrun.viewmodel.CourseViewModel
import com.example.drawrun.viewmodel.CourseViewModelFactory
import com.example.drawrun.viewmodel.MasterpieceViewModel
import com.example.drawrun.viewmodel.MasterpieceViewModelFactory
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.mapbox.geojson.Point
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Calendar

class GroupRunBottomSheet : BottomSheetDialogFragment() {
    private var _binding: BottomSheetGroupRunBinding? = null
    private val binding get() = _binding!!
    // ... BottomSheet 구현

    // ViewModel 추가
    private lateinit var masterpieceViewModel: MasterpieceViewModel
    private lateinit var courseViewModel: CourseViewModel


    // 포인트 리스트를 저장할 변수 추가
    private var points: List<Point> = listOf()

    companion object {
        fun newInstance(distance: Double, imagePath: String, points:List<Point>): GroupRunBottomSheet {
            return GroupRunBottomSheet().apply {
                arguments = Bundle().apply {
                    putDouble("distance", distance)
                    putString("image_path", imagePath)
                    putDoubleArray("latitudes", points.map { it.latitude() }.toDoubleArray())
                    putDoubleArray("longitudes", points.map { it.longitude() }.toDoubleArray())                }
            }
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val bottomSheetDialog = BottomSheetDialog(requireContext(), theme)
        bottomSheetDialog.behavior.apply {
            state = BottomSheetBehavior.STATE_EXPANDED  // 확장된 상태로 시작
        }
        return bottomSheetDialog
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        dialog?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)
        _binding = BottomSheetGroupRunBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // ViewModel 초기화
        val masterpieceRepository = MasterpieceRepository(RetrofitInstance.MasterpieceApi(requireContext()))
        masterpieceViewModel = ViewModelProvider(this, MasterpieceViewModelFactory(masterpieceRepository))[MasterpieceViewModel::class.java]

        val courseRepository = CourseRepository(RetrofitInstance.CourseApi(requireContext()))
        courseViewModel = ViewModelProvider(this, CourseViewModelFactory(courseRepository))[CourseViewModel::class.java]

        // 포인트 리스트 복원
        val latitudes = arguments?.getDoubleArray("latitudes") ?: doubleArrayOf()
        val longitudes = arguments?.getDoubleArray("longitudes") ?: doubleArrayOf()
        points = latitudes.zip(longitudes) { lat, lng ->
            Point.fromLngLat(lng, lat)
        }

        setupViews()
        loadImage()
        setupObservers()

        // 거리 정보 설정
        arguments?.getDouble("distance")?.let { distance ->
            binding.tvDistance.text = String.format("%.2fkm", distance)
        }

        // 이미지뷰의 높이를 5:7 비율로 설정
        binding.capturedMapImage.post {
            val width = binding.capturedMapImage.width
            val params = binding.capturedMapImage.layoutParams
            params.height = (width * 1.4).toInt()  // 7/5 = 1.4
            binding.capturedMapImage.layoutParams = params
        }
    }


    private fun setupViews() {
        binding.layoutDeadline.setOnClickListener {
            val calendar = Calendar.getInstance()

            // 오늘 날짜 설정
            val today = calendar.timeInMillis

            // 한 달 후 날짜 설정
            calendar.add(Calendar.MONTH, 1)
            val oneMonthLater = calendar.timeInMillis

            // 현재 날짜로 다시 설정
            calendar.timeInMillis = today

            val datePickerDialog = DatePickerDialog(
                requireContext(),
                { _, year, month, day ->
                    // 선택된 날짜 처리
                    binding.tvDeadline.text = String.format("%d.%02d.%02d까지", year, month + 1, day)
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            )

            // 최소 날짜를 오늘로 설정
            datePickerDialog.datePicker.minDate = today
            // 최대 날짜를 한 달 후로 설정
            datePickerDialog.datePicker.maxDate = oneMonthLater

            datePickerDialog.show()
        }

        // 키보드 엔터 처리 추가
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

        // 등록 버튼 클릭 리스너 추가
        binding.btnSave.setOnClickListener {
            saveMasterpiece()
        }

        // 인원 조절 버튼 리스너 추가
        val maxMemberCount = points.size - 1
        var memberCount = 2  // 초기값

        binding.btnMinus.setOnClickListener {
            if (memberCount > 2) {  // 최소 2명
                memberCount--
                binding.tvMemberCount.text = memberCount.toString()
            }
        }

        binding.btnPlus.setOnClickListener {
            if (memberCount < maxMemberCount) {
                memberCount++
                binding.tvMemberCount.text = memberCount.toString()
            } else {
                Toast.makeText(requireContext(), "최대 인원수에 도달했습니다.", Toast.LENGTH_SHORT).show()
            }
        }
    }


    private fun loadImage() {
        arguments?.getString("image_path")?.let { imagePath ->
            if (imagePath.startsWith("http")) {
                Glide.with(this)
                    .load(imagePath)
                    .into(binding.capturedMapImage)
            } else {
                    Log.e("ImageLoading", "Error loading image")
                }
        }
    }


    private fun saveMasterpiece() {
        val courseName = binding.etCourseName.text.toString()
        val memberCount = binding.tvMemberCount.text.toString().toInt()
        val deadline = binding.tvDeadline.text.toString()

        if (courseName.isBlank() || deadline == "날짜를 선택해주세요") {
            Toast.makeText(requireContext(), "모든 필드를 입력해주세요.", Toast.LENGTH_SHORT).show()
            return
        }

        // 코스 저장 먼저 실행
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
            Log.d("GroupRunBottomSheet", "Saving course: name=$courseName, distance=$formattedDistance, imagePath=$imagePath")

            // CourseViewModel을 통해 코스 저장 요청
            courseViewModel.saveCourse(
                path = points,
                name = courseName,
                pathImgUrl = imagePath,
                distance = formattedDistance
            )
        }
        // 마스터피스 저장은 코스 저장 성공 후 setupObservers에서 처리
    }


    private fun setupObservers() {
        // 코스 저장 결과 관찰
        courseViewModel.saveCourseResult.observe(viewLifecycleOwner) { result ->
            result.onSuccess { courseId ->
                Log.d("GroupRunBottomSheet", "Course saved successfully: courseId=$courseId")
                if (courseId > 0) {
                    // 코스 저장 성공, 마스터피스 저장 진행
                    saveMasterpieceWithCourseId(courseId)
                } else {
                    Log.e("GroupRunBottomSheet", "Course save failed: Invalid courseId")
                    Toast.makeText(requireContext(), "코스 저장에 실패했습니다.", Toast.LENGTH_SHORT).show()
                }
            }.onFailure { exception ->
                Log.e("GroupRunBottomSheet", "Course save failed", exception)
                Toast.makeText(requireContext(), "코스 저장 실패: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
        }

        // 마스터피스 저장 결과 관찰 (기존 코드)
//        masterpieceViewModel.saveMasterpieceResult.observe(viewLifecycleOwner) { result ->
//            result.onSuccess { masterpieceId ->
//                Toast.makeText(requireContext(), "걸작이 성공적으로 저장되었습니다. ID: $masterpieceId", Toast.LENGTH_SHORT).show()
//                dismiss()
//            }.onFailure { exception ->
//                Toast.makeText(requireContext(), "걸작 저장 실패: ${exception.message}", Toast.LENGTH_SHORT).show()
//            }
//        }
    }


    private fun saveMasterpieceWithCourseId(courseId: Int) {
        val memberCount = binding.tvMemberCount.text.toString().toInt()
        val deadline = binding.tvDeadline.text.toString()
        val dateFormatter = DateTimeFormatter.ofPattern("yyyy.MM.dd")
        val expireDateString = deadline.split("까지")[0].trim()
        val expireDate = LocalDate.parse(expireDateString, dateFormatter).format(dateFormatter)

        val dividedPaths = dividePath(points, memberCount)
        val request = MasterpieceSaveRequest(
            userPathId = courseId, // 저장된 코스 ID 사용
            paths = dividedPaths.map { path ->
                path.map { point ->
                    com.example.drawrun.data.dto.request.masterpiece.Point(
                        latitude = point.latitude(),
                        longitude = point.longitude()
                    )
                }
            },
            restrictCount = memberCount,
            expireDate = expireDate
        )

        // MasterpieceViewModel을 통해 마스터피스 저장 요청
        masterpieceViewModel.saveMasterpiece(request)
        masterpieceViewModel.saveMasterpieceResult.observe(viewLifecycleOwner) { result ->
            result.onSuccess { masterpieceBoardId ->
                Toast.makeText(requireContext(), "걸작이 성공적으로 저장되었습니다. ID: $masterpieceBoardId", Toast.LENGTH_SHORT).show()
                navigateToMasterpieceDetail(masterpieceBoardId)
                dismiss()
            }.onFailure { exception ->
                Toast.makeText(requireContext(), "걸작 저장 실패: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun navigateToMasterpieceDetail(masterpieceBoardId: Int) {
        val intent = Intent(requireContext(), MasterpieceActivity::class.java).apply {
            putExtra("masterpieceBoardId", masterpieceBoardId)
        }
        startActivity(intent)
    }


    // 포인트 리스트를 인원수에 맞게 나누는 함수
    private fun dividePath(points: List<Point>, memberCount: Int): List<List<Point>> {
        val result = mutableListOf<List<Point>>()
        val totalPoints = points.size
        val basePointsPerMember = totalPoints / memberCount
        val extraPoints = totalPoints % memberCount

        var startIndex = 0
        for (i in 0 until memberCount) {
            val pointsForThisMember = basePointsPerMember + if (i < extraPoints) 1 else 0
            val endIndex = (startIndex + pointsForThisMember).coerceAtMost(totalPoints)

            if (i > 0) {
                startIndex -= 1 // 이전 구간의 마지막 포인트를 포함
            }

            result.add(points.subList(startIndex, endIndex))
            startIndex = endIndex
        }

        return result
    }





    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}