package com.example.drawrun.presentation.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import com.example.drawrun.R
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(onFinish: () -> Unit) {
    // 스플래시 화면 UI
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        val splashIcon: Painter = painterResource(R.drawable.my_splash_icon2)
        Image(
            painter = splashIcon,
            contentDescription = "Splash Icon",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop  // 화면에 꽉 차게 표시
        )
    }

    // 3초 후 화면 전환
    LaunchedEffect(Unit) {
        delay(3000L)  // 3초 대기
        onFinish()    // 대기 후 호출
    }
}
