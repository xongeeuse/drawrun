package com.example.drawrun.ui.map.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.drawrun.databinding.BottomSheetCourseCompleteBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class CourseCompleteBottomSheet : BottomSheetDialogFragment() {
    private var _binding: BottomSheetCourseCompleteBinding? = null
    private val binding get() = _binding!!

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
    }

    private fun setupViews() {
        binding.btnSoloRun.setOnClickListener {
            // 현재 BottomSheet 닫기
            dismiss()
            // 혼자 달려요 BottomSheet 표시
            SoloRunBottomSheet().show(parentFragmentManager, "SoloRunBottomSheet")
        }

        binding.btnGroupRun.setOnClickListener {
            dismiss()
            // 함께 달려요 BottomSheet 표시
            GroupRunBottomSheet().show(parentFragmentManager, "GroupRunBottomSheet")
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
