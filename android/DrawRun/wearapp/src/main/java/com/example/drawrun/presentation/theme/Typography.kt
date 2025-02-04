package com.example.drawrun.presentation.theme

import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.wear.compose.material.Typography
import com.example.drawrun.R

// 커스텀 폰트 설정
val praise = FontFamily(
    Font(R.font.praise_regular, FontWeight.Normal)
)

val pretendard = FontFamily(
    Font(R.font.pretendard_regular, FontWeight.Normal)
)

// Typography 설정
val praiseFont = Typography(
    body1 = TextStyle(
        fontFamily = praise,
        fontSize = 25.sp,
        fontWeight = FontWeight.Normal
    )
)

// Typography 설정
val pretendardFont = Typography(
    body1 = TextStyle(
        fontFamily = pretendard,
        fontSize = 16.sp,
        fontWeight = FontWeight.Normal
    )
)
