package com.example.drawrun

import android.content.Intent
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
import com.example.drawrun.ui.map.MapActivity
import com.example.drawrun.ui.mypage.UserActivity
import com.example.drawrun.ui.search.SearchActivity

class MainActivity : AppCompatActivity() {
    private lateinit var btnLoginLogout: Button
    private lateinit var tvWelcome: TextView

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

        val btnRegister = findViewById<Button>(R.id.btnRegister)
        btnLoginLogout = findViewById(R.id.btnLogin)
        tvWelcome = findViewById(R.id.tvWelcome)

        // ✅ 초기 로그인 상태 확인
        updateLoginState()

        // 회원가입 버튼 클릭 이벤트 처리
        btnRegister.setOnClickListener {
            try {
                val intent = Intent(this, RegisterActivity::class.java)
                startActivity(intent)
            } catch (e: Exception) {
                Log.e("MainActivity", "Error starting RegisterActivity", e)
            }
        }

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

        // 유저 페이지 버튼 클릭 이벤트
        findViewById<Button>(R.id.goToUserPageButton).setOnClickListener {
            val accessToken = SecureStorage.getAccessToken(this)
            if (accessToken != null) {
                // 로그인 상태일 경우 UserActivity로 이동
                val intent = Intent(this, UserActivity::class.java)
                startActivity(intent)
            } else {
                // 비로그인 상태일 경우 LoginActivity로 이동
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
            }
        }

        findViewById<Button>(R.id.btnGoToMap).setOnClickListener {
            val accessToken = SecureStorage.getAccessToken(this)
            if (accessToken != null) {
                // 로그인 상태일 경우 MapActivity로 이동
                val intent = Intent(this, MapActivity::class.java)
                startActivity(intent)
            } else {
                // 비로그인 상태일 경우 LoginActivity로 이동
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
            }
        }

        findViewById<Button>(R.id.btnGoToSearch).setOnClickListener {
            val accessToken = SecureStorage.getAccessToken(this)
            if (accessToken != null) {
                val intent = Intent(this, SearchActivity::class.java)
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

    // ✅ 로그인 상태 업데이트 함수ㅅ
    private fun updateLoginState() {
        val accessToken = SecureStorage.getAccessToken(this)
        val isLoggedIn = accessToken != null

        if (isLoggedIn) {
            btnLoginLogout.text = "로그아웃"
            val username = parseUsernameFromToken(accessToken)
            tvWelcome.text = "환영합니다, $username 님! 😊"
            tvWelcome.visibility = TextView.VISIBLE
        } else {
            btnLoginLogout.text = "로그인"
            tvWelcome.visibility = TextView.GONE
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
}


