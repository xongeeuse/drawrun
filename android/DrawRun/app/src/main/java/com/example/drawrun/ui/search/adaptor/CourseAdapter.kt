package com.example.drawrun.ui.search.adaptor

import android.util.Log
import android.view.LayoutInflater
import android.view.View
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
    private val onBookmarkClick: (CourseData) -> Unit,
    private val showRanking: Boolean = false
) : ListAdapter<CourseData, CourseViewHolder>(CourseDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CourseViewHolder {
        Log.d("SearchSearch", "onCreateViewHolder called")
        return CourseViewHolder(
            ItemCourseBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            ),
            onBookmarkClick,  // ViewHolder 생성 시 콜백 전달
            showRanking

        )
    }

    override fun onBindViewHolder(holder: CourseViewHolder, position: Int) {
        val course = getItem(position)
        Log.d("SearchSearch", "onBindViewHolder called for position $position, course: $course")
        holder.bind(course, position)
    }
}

class CourseViewHolder(
    private val binding: ItemCourseBinding,
    private val onBookmarkClick: (CourseData) -> Unit,  // 북마크 클릭 콜백 추가
    private val showRanking: Boolean
) : RecyclerView.ViewHolder(binding.root) {
    fun bind(course: CourseData, position: Int) {
        binding.apply {
            // 기존 데이터 바인딩
            courseName.text = course.courseName
            location.text = course.location
            distance.text = "${course.distance}km"
            nickname.text = course.userNickname  // 작성자 닉네임
            bookmarkCount.text = course.bookmarkCount.toString()

            // 랭킹 정보 추가
            // showRanking이 true일 때만 랭킹 번호를 표시
            // position + 1을 사용하여 1부터 시작하는 랭킹 번호 표시
            if (showRanking) {
                rankingNumber.visibility = View.VISIBLE
                rankingNumber.text = "${position + 1}"
            } else {
                rankingNumber.visibility = View.GONE
            }

            // 북마크 버튼 클릭 리스너
            btnBookmark.setOnClickListener {
                onBookmarkClick(course)  // 콜백 호출
            }

            // 북마크 상태 처리
            btnBookmark.setImageResource(
                if (course.isBookmark) R.drawable.bookmark_filled_icon
                else R.drawable.bookmark_outlined_icon
            )

            // 이미지 로딩 (Glide 사용)
            Glide.with(itemView.context)
                .load(course.courseImgUrl)
                .into(courseImage)

            // 프로필 이미지 로딩 (null이 아닐 때만)
            course.profileImgUrl?.let { url ->
                Glide.with(itemView.context)
                    .load(url)
                    .circleCrop()
                    .into(profileImage)
            }
        }
    }
}


class CourseDiffCallback : DiffUtil.ItemCallback<CourseData>() {
    override fun areItemsTheSame(oldItem: CourseData, newItem: CourseData): Boolean {
        return oldItem.courseId == newItem.courseId
    }

    override fun areContentsTheSame(oldItem: CourseData, newItem: CourseData): Boolean {
        return oldItem == newItem // 데이터 클래스의 equals() 사용
    }
}

