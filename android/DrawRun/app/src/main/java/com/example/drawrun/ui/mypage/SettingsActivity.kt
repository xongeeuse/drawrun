package com.example.drawrun.ui.mypage

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.example.drawrun.R
import com.example.drawrun.ui.common.BaseActivity
import com.google.android.gms.wearable.Wearable
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SettingsActivity : BaseActivity() {

    override fun getLayoutId(): Int = R.layout.activity_settings

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val emailTextView: TextView = findViewById(R.id.emailValueTextView)
        val idTextView: TextView = findViewById(R.id.idValueTextView)

        // 실제 API 호출 (코루틴 사용)


        val btnStartRunning = findViewById<Button>(R.id.btnStartRunning)
        btnStartRunning.setOnClickListener {
            Wearable.getNodeClient(this).connectedNodes.addOnSuccessListener { nodes ->
                nodes.forEach { node ->
                    // 러닝 시작 메시지 전송
                    Wearable.getMessageClient(this).sendMessage(
                        node.id,
                        "/start_running",
                        null
                    ).addOnSuccessListener {
                        Toast.makeText(this, "러닝 시작 메시지를 보냈습니다!", Toast.LENGTH_SHORT).show()
                    }.addOnFailureListener {
                        Toast.makeText(this, "메시지 전송 실패", Toast.LENGTH_LONG).show()
                    }

                    // 워치 앱 실행 메시지 전송
                    Wearable.getMessageClient(this).sendMessage(
                        node.id,
                        "/launch_app",
                        null
                    ).addOnSuccessListener {
                        Toast.makeText(this, "워치 앱을 실행했습니다!", Toast.LENGTH_SHORT).show()
                    }.addOnFailureListener {
                        Toast.makeText(this, "워치 앱 실행 실패", Toast.LENGTH_LONG).show()
                    }
                }
            }
        }



    }
}