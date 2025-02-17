package com.example.drawrun.ui.main.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.drawrun.data.dto.response.search.CourseData
import com.example.drawrun.databinding.FragmentCourseSliderBinding
import com.google.gson.Gson

class CourseSliderFragment : Fragment() {

    private var _binding: FragmentCourseSliderBinding? = null
    private val binding get() = _binding!!

    private var onImageClickListener: ((CourseData) -> Unit)? = null // ✅ 클릭 리스너 추가

    companion object {
        private const val ARG_COURSE = "course"

        fun newInstance(course: CourseData): CourseSliderFragment {
            val fragment = CourseSliderFragment()
            val args = Bundle().apply {
                val courseJson = Gson().toJson(course)  // ✅ 객체를 JSON으로 변환
                putString(ARG_COURSE, courseJson)
            }
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentCourseSliderBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val courseJson = arguments?.getString(ARG_COURSE)
        val course = Gson().fromJson(courseJson, CourseData::class.java)

        course?.let {
            binding.tvCourseName.text = it.courseName
            binding.tvDistance.text = "${it.distance} km"
            Log.d("CourseSliderFragment", "📸 이미지 URL: ${course.courseImgUrl}")

            // ✅ Glide를 활용해 이미지 로드
            Glide.with(this)
                .load(it.courseImgUrl)
                .centerCrop()
                .into(binding.ivCourseImage)

            // ✅ 이미지 클릭 시 상세 화면 이동
            binding.ivCourseImage.setOnClickListener {
                onImageClickListener?.invoke(course)
            }
        }
    }

    fun setOnImageClickListener(listener: (CourseData) -> Unit) {
        onImageClickListener = listener
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
