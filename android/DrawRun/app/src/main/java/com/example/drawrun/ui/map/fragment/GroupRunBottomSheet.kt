package com.example.drawrun.ui.map.fragment

import android.app.DatePickerDialog
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
import com.bumptech.glide.Glide
import com.example.drawrun.databinding.BottomSheetGroupRunBinding
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import java.io.File
import java.util.Calendar

class GroupRunBottomSheet : BottomSheetDialogFragment() {
    private var _binding: BottomSheetGroupRunBinding? = null
    private val binding get() = _binding!!
    // ... BottomSheet 구현

    companion object {
        fun newInstance(distance: Double, imagePath: String): GroupRunBottomSheet {
            return GroupRunBottomSheet().apply {
                arguments = Bundle().apply {
                    putDouble("distance", distance)
                    putString("image_path", imagePath)
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
        _binding = BottomSheetGroupRunBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViews()
        loadImage()

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

            DatePickerDialog(
                requireContext(),
                { _, year, month, day ->
                    // 선택된 날짜 처리
                    binding.tvDeadline.text = String.format("%d.%02d.%02d까지", year, month + 1, day)
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            ).show()
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

        // 인원 조절 버튼 리스너 추가
        var memberCount = 2  // 초기값

        binding.btnMinus.setOnClickListener {
            if (memberCount > 2) {  // 최소 2명
                memberCount--
                binding.tvMemberCount.text = memberCount.toString()
            }
        }

        binding.btnPlus.setOnClickListener {
            memberCount++
            binding.tvMemberCount.text = memberCount.toString()
        }
    }

    private fun loadImage() {
        arguments?.getString("image_path")?.let { imagePath ->
            if (imagePath.startsWith("http")) {
                // URL인 경우 Glide를 사용하여 이미지 로드
                Glide.with(this)
                    .load(imagePath)
                    .into(binding.capturedMapImage)
            } else {
                    Log.e("ImageLoading", "Error loading image")
                    }
                }
            }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}