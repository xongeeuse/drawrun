package com.example.drawrun.presentation.theme

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Colors
import androidx.wear.compose.material.Typography

// Wear OS 전용 색상 설정
private val WearColorPalette = Colors(
    primary = Color(0xFF00FFAE),
    background = Color(0xFF171515),
    onPrimary = Color.White
)

// 폰트 전역 설정 변수
var usePretendardFont: Boolean = true

@Composable
fun DrawRunTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colors = WearColorPalette,
        typography = if (usePretendardFont) pretendardFont else praiseFont,
        content = content
    )
}
