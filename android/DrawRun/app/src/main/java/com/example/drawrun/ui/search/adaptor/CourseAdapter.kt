package com.example.drawrun.ui.search.adaptor

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.drawrun.R
import com.example.drawrun.data.dto.response.search.CourseData
import com.example.drawrun.databinding.ItemCourseBinding

// 생성자에 콜백 추가
class CourseAdapter(
    private val onBookmarkClick: (CourseData) -> Unit
) : ListAdapter<CourseData, CourseViewHolder>(CourseDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CourseViewHolder {
        return CourseViewHolder(
            ItemCourseBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            ),
            onBookmarkClick  // ViewHolder 생성 시 콜백 전달
        )
    }

    override fun onBindViewHolder(holder: CourseViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}

class CourseViewHolder(
    private val binding: ItemCourseBinding,
    private val onBookmarkClick: (CourseData) -> Unit  // 북마크 클릭 콜백 추가
) : RecyclerView.ViewHolder(binding.root) {
    fun bind(course: CourseData) {
        binding.apply {
            // 기존 데이터 바인딩
            courseName.text = course.courseName
            location.text = course.location
            distance.text = "${course.distance}km"
            nickname.text = course.userNickname  // 작성자 닉네임
            bookmarkCount.text = course.bookmarkCount.toString()

            // 북마크 버튼 클릭 리스너
            btnBookmark.setOnClickListener {
                onBookmarkClick(course)  // 콜백 호출
            }

            // 북마크 상태 처리
            btnBookmark.setImageResource(
                if (course.isBookmarked) R.drawable.bookmark_filled_icon
                else R.drawable.bookmark_outlined_icon
            )

            // 이미지 로딩 (Glide 사용)
            Glide.with(itemView.context)
                .load(course.courseImgUrl)
                .into(courseImage)

            // 프로필 이미지 로딩
            Glide.with(itemView.context)
                .load(course.profileImgUrl)  // 프로필 이미지 URL
                .circleCrop()  // 원형으로 표시
                .into(profileImage)
        }
    }
}


class CourseDiffCallback : DiffUtil.ItemCallback<CourseData>() {
    override fun areItemsTheSame(oldItem: CourseData, newItem: CourseData) =
        oldItem.courseId == newItem.courseId

    override fun areContentsTheSame(oldItem: CourseData, newItem: CourseData) =
        oldItem == newItem
}
