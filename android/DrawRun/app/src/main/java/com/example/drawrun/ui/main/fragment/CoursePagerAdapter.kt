package com.example.drawrun.ui.main.fragment

import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.drawrun.data.dto.response.search.CourseData

class CoursePagerAdapter(
    fragmentActivity: FragmentActivity, // ✅ `FragmentActivity`를 직접 받음
    private val courseList: List<CourseData>
) : FragmentStateAdapter(fragmentActivity) { // ✅ `fragmentActivity`를 넘겨줌

    // ✅ 클릭 리스너 인터페이스 추가
    private var onItemClickListener: ((CourseData) -> Unit)? = null

    fun setOnItemClickListener(listener: (CourseData) -> Unit) {
        onItemClickListener = listener
    }

    override fun getItemCount(): Int = courseList.size

    override fun createFragment(position: Int) = CourseSliderFragment.newInstance(courseList[position]).apply {
        setOnImageClickListener { course ->
            onItemClickListener?.invoke(course)
        }
    }
}
