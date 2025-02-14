package com.example.drawrun.data.dto.response.course



import android.os.Parcelable
import com.example.drawrun.dto.course.PathPoint
import kotlinx.parcelize.Parcelize

@Parcelize
data class CourseDetailsResponse(
    val path: List<PathPoint>, // ✅ path 타입을 List<PathPoint>로 변경!
    val location: String,
    val userPathId: Int,
    val distance: Double
) : Parcelable
