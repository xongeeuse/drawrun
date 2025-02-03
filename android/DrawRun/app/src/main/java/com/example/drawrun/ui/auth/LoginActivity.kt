package com.example.drawrun.ui.auth

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.drawrun.MainActivity
import com.example.drawrun.R
import com.example.drawrun.data.repository.AuthRepository
import com.example.drawrun.utils.RetrofitInstance
import com.example.drawrun.utils.SecureStorage
import com.example.drawrun.viewmodel.AuthViewModelFactory
import com.example.drawrun.viewmodel.LoginState
import com.example.drawrun.viewmodel.LoginViewModel
import kotlinx.coroutines.flow.collect

class LoginActivity : AppCompatActivity() {

    private lateinit var userIdEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var loginButton: Button
    private lateinit var progressBar: ProgressBar

    // ViewModel 초기화
    private val viewModel: LoginViewModel by viewModels {
        AuthViewModelFactory(AuthRepository(RetrofitInstance.api))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // UI 요소 초기화
        userIdEditText = findViewById(R.id.etUserId)
        passwordEditText = findViewById(R.id.etPassword)
        loginButton = findViewById(R.id.btnLogin)
        progressBar = findViewById(R.id.progressBar)

        // 로그인 버튼 클릭 이벤트 처리
        loginButton.setOnClickListener {
            val userId = userIdEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()

            if (userId.isNotEmpty() && password.isNotEmpty()) {
                viewModel.login(userId, password)  // ViewModel의 login() 호출
            } else {
                Toast.makeText(this, "아이디와 비밀번호를 입력하세요.", Toast.LENGTH_SHORT).show()
            }
        }

        // 로그인 상태 관찰
        observeLoginState()
    }

    private fun observeLoginState() {
        lifecycleScope.launchWhenStarted {
            viewModel.loginState.collect { state ->
                when (state) {
                    is LoginState.Idle -> {
                        progressBar.visibility = ProgressBar.GONE
                    }
                    is LoginState.Loading -> {
                        progressBar.visibility = ProgressBar.VISIBLE
                    }
                    is LoginState.Success -> {
                        progressBar.visibility = ProgressBar.GONE
                        Toast.makeText(this@LoginActivity, "로그인 성공!", Toast.LENGTH_SHORT).show()

                        // ✅ Access Token 저장 (보안 저장소)
                        SecureStorage.saveAccessToken(this@LoginActivity, state.accessToken)

                        // 메인 화면으로 이동 (MainActivity 예시)
                        val intent = Intent(this@LoginActivity, MainActivity::class.java)
                        startActivity(intent)
                        finish()  // 로그인 화면 종료
                    }
                    is LoginState.Error -> {
                        progressBar.visibility = ProgressBar.GONE
                        // 🚀 상세 로그 출력
                        android.util.Log.e("LoginError", "로그인 실패 메시지: ${state.message}")

                        Toast.makeText(this@LoginActivity, "로그인 실패: ${state.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }
}
