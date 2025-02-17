package com.example.drawrun.presentation.ui

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.wear.compose.material.*
import com.example.drawrun.R
import com.example.drawrun.presentation.RunningActivity
import com.example.drawrun.presentation.sensors.SensorViewModel
import com.example.drawrun.presentation.theme.praise
import com.example.drawrun.presentation.theme.pretendard
import com.google.android.gms.wearable.Wearable


@Composable
fun DrawRunMainScreen(viewModel: SensorViewModel, context: Context) {

    // 심박수 상태 수집
    val heartRate by viewModel.heartRate.collectAsState(initial = null)

    Scaffold(
        timeText = { TimeText() },
        modifier = Modifier.fillMaxSize()
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)  // 각 요소 간격 추가
            ) {
                // 타이틀 텍스트
                Text(
                    text = "Draw Run",
                    style = MaterialTheme.typography.body1.copy(
                        color = MaterialTheme.colors.primary,
                        fontFamily = praise,
                        fontSize = 30.sp
                    ),
//                    modifier = Modifier.padding(bottom = 5.dp)
                )

                // 심박수와 경로 선택 버튼을 Row로 배치
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)  // 요소 간격 추가
                ) {
                    // 심박수 아이콘
                    Image(
                        painter = painterResource(com.example.drawrun.R.drawable.heart_icon),
                        contentDescription = "Heart Rate Icon",
                        modifier = Modifier.size(24.dp)
                    )

                    // 심박수 텍스트
                    Text(
                        text = heartRate?.let { "${it.toInt()} BPM" } ?: "...",
                        style = MaterialTheme.typography.body1.copy(
                            fontFamily = pretendard,
                            fontSize = 13.sp,
                            color = Color.White
                        )
                    )
                }

                // 경로 선택 버튼 (이미지와 텍스트가 함께 위치)
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Image(
                        painter = painterResource(R.drawable.map_ai_icon),
                        contentDescription = "Select Path Icon",
                        modifier = Modifier
                            .size(24.dp)
                            .clickable {

                                // 연결된 노드 탐색 및 메시지 전송
                                Wearable.getNodeClient(context).connectedNodes.addOnSuccessListener { nodes ->
                                    nodes.forEach { node ->
                                        Log.d("DrawRun", "연결된 노드 ID: ${node.id}, 이름: ${node.displayName}")
                                        Wearable.getMessageClient(context).sendMessage(
                                            node.id,
                                            "/launch_app",
                                            "message from watch".toByteArray()
                                        ).addOnSuccessListener {
                                            Log.d("DrawRun", "모바일 앱 실행 요청 메시지 전송 성공")
                                        }.addOnFailureListener {
                                            Log.e("DrawRun", "모바일 앱 실행 요청 메시지 전송 실패")
                                        }
                                    }
                                }
                            }

                    )

                    // 경로 선택 텍스트
                    Text(
                        text = "경로 선택",
                        style = MaterialTheme.typography.body1.copy(
                            fontFamily = pretendard,
                            fontSize = 13.sp,
                            color = Color.White
                        )
                    )
                }

                // 러닝 시작 버튼
                Button(
                    onClick = {
                        val runningIntent = Intent(context, RunningActivity::class.java)
                        context.startActivity(runningIntent)
                    },
                    modifier = Modifier
                        .fillMaxWidth(0.5f)
                        .height(40.dp),
//                        .padding(top = 5.dp),
                    colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFF01280E))
                ) {
                    Text(
                        text = "러닝 시작",
                        style = MaterialTheme.typography.body1.copy(
                            fontFamily = pretendard,
                            fontSize = 12.sp,
                            color = Color.White
                        )
                    )
                }

            }
        }
    }
}
