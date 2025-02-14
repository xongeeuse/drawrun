package com.example.drawrun.dto.course

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class PathPoint(
    // details.path가 LatLngData이기 때문에 Parcelable로 변환해야 Intent로 넘기기 가능
    val latitude: Double,
    val longitude: Double
) : Parcelable
