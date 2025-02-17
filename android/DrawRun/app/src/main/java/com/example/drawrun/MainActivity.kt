package com.example.drawrun

import android.content.Intent
import android.graphics.LinearGradient
import android.graphics.Shader
import android.media.Image
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.drawrun.ui.auth.LoginActivity
import com.example.drawrun.ui.auth.RegisterActivity
import com.example.drawrun.utils.SecureStorage
import org.json.JSONObject
import android.util.Base64
import android.widget.ImageView
import com.example.drawrun.ui.common.BaseActivity
import com.example.drawrun.ui.map.MapActivity
import com.example.drawrun.ui.masterpiece.MasterpieceActivity
import com.example.drawrun.ui.mypage.UserActivity
import com.example.drawrun.ui.search.SearchActivity

class MainActivity : BaseActivity() {

    private lateinit var btnLoginLogout: Button
    private lateinit var btnAICourse: ImageView
    private lateinit var btnCustomCourse: ImageView
    private lateinit var tvLocation: TextView
    private lateinit var tvRunNear: TextView
    override fun getLayoutId(): Int = R.layout.activity_main  // ✅ 레이아웃 리소스 지정

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        LaunchAppMessageReceiver(this)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        tvRunNear = findViewById(R.id.tvRunNear)
        applyGradientToText(tvRunNear)

        tvLocation = findViewById(R.id.tvLocation)

        val btnRegister = findViewById<Button>(R.id.btnRegister)
        btnLoginLogout = findViewById(R.id.btnLogin)

        // ✅ 초기 로그인 상태 확인
        updateLoginState()

// ✅ UI 요소 초기화
        tvLocation = findViewById(R.id.tvLocation)
        btnLoginLogout = findViewById(R.id.btnLogin)
        btnAICourse = findViewById(R.id.btnAICourse) // 🔹 ImageView로 수정
        btnCustomCourse = findViewById(R.id.btnCustomCourse) // 🔹 ImageView로 수정


        // ✅ 로그인/로그아웃 버튼 클릭 이벤트 처리
        btnLoginLogout.setOnClickListener {
            if (SecureStorage.getAccessToken(this) != null) {
                // 로그아웃 처리
                SecureStorage.clearAccessToken(this)
                Log.d("MainActivity", "로그아웃 완료")
            } else {
                // 로그인 화면으로 이동
                try {
                    val intent = Intent(this, LoginActivity::class.java)
                    startActivity(intent)
                } catch (e: Exception) {
                    Log.e("MainActivity", "Error starting LoginActivity", e)
                }
            }
            // ✅ 상태 업데이트
            updateLoginState()
        }


        // ✅ AI 코스 버튼 클릭 이벤트
        btnAICourse.setOnClickListener {
            val accessToken = SecureStorage.getAccessToken(this)
            if (accessToken != null) {
                val intent = Intent(this, MapActivity::class.java)
                startActivity(intent)
            } else {
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
            }
        }

        // ✅ 나만의 코스 버튼 클릭 이벤트
        btnCustomCourse.setOnClickListener {
            val accessToken = SecureStorage.getAccessToken(this)
            if (accessToken != null) {
                val intent = Intent(this, MapActivity::class.java)
                startActivity(intent)
            } else {
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
            }
        }



        // 인텐트에 포함된 메시지 경로 확인 (추가 검증)
        intent?.extras?.let {
            Log.d("DrawRun", "MainActivity 인텐트 데이터: ${it.toString()}")
        }
    }

    override fun onResume() {
        super.onResume()
        updateLoginState()  // ✅ 액티비티가 다시 보일 때 로그인 상태 업데이트
    }

    // ✅ 로그인 상태 업데이트 함수
    private fun updateLoginState() {
        val accessToken = SecureStorage.getAccessToken(this)
        val isLoggedIn = accessToken != null

        if (isLoggedIn) {
            btnLoginLogout.text = "로그아웃"
            val username = parseUsernameFromToken(accessToken)
        } else {
            btnLoginLogout.text = "로그인"
        }
    }

    // ✅ JWT 토큰에서 username 추출
    private fun parseUsernameFromToken(token: String?): String {
        return try {
            val parts = token?.split(".")
            if (parts != null && parts.size >= 2) {
                val payload = String(Base64.decode(parts[1], Base64.URL_SAFE)) // ✅ URL_SAFE로 수정
                val jsonObject = JSONObject(payload)
                jsonObject.optString("username", "사용자") // ✅ username 파싱
            } else {
                "사용자"
            }
        } catch (e: Exception) {
            e.printStackTrace()
            "사용자"
        }
    }

    private fun applyGradientToText(textView: TextView) {
        val paint = textView.paint
        val width = paint.measureText(textView.text.toString())

        val shader = LinearGradient(
            0f, 0f, width, textView.textSize,
            intArrayOf(0xFF66FF99.toInt(), 0xFF228B22.toInt()), // 연두색 → 녹색
            null,
            Shader.TileMode.CLAMP
        )
        textView.paint.shader = shader
    }
}


