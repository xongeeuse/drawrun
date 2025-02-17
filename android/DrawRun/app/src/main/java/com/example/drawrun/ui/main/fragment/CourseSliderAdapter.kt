package com.example.drawrun.ui.main.fragment

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.drawrun.data.dto.response.search.CourseData

class CourseSliderAdapter(fragment: Fragment, private val courseList: List<CourseData>) : FragmentStateAdapter(fragment) {
    override fun getItemCount(): Int = courseList.size
    override fun createFragment(position: Int): Fragment = CourseSliderFragment.newInstance(courseList[position])
}