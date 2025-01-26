package com.example.drawrun.ui.auth.signup

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.drawrun.databinding.ActivitySignupBinding

class SignupActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignupBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupSignupButton()
    }

    private fun setupSignupButton() {
        binding.signupButton.setOnClickListener {
            val userId = binding.userIdEditText.text.toString()
            val email = binding.emailEditText.text.toString()
            val password = binding.passwordEditText.text.toString()
            val passwordConfirm = binding.passwordConfirmEditText.text.toString()
            val userName = binding.userNameEditText.text.toString()
            val nickname = binding.nicknameEditText.text.toString()

            // 입력값 검증
            if (validateInput(userId, email, password, passwordConfirm, userName, nickname)) {
                // TODO: 회원가입 로직 구현
            }
        }
    }

    private fun validateInput(
        userId: String,
        email: String,
        password: String,
        passwordConfirm: String,
        userName: String,
        nickname: String
    ): Boolean {
        // 모든 필드가 비어있지 않은지 확인
        if (userId.isEmpty() || email.isEmpty() || password.isEmpty() ||
            passwordConfirm.isEmpty() || userName.isEmpty() || nickname.isEmpty()) {
            // TODO: 사용자에게 모든 필드를 입력하라는 메시지 표시
            return false
        }

        // 비밀번호 일치 여부 확인
        if (password != passwordConfirm) {
            // TODO: 비밀번호 불일치 메시지 표시
            return false
        }

        // 이메일 형식 검증
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            // TODO: 올바른 이메일 형식을 입력하라는 메시지 표시
            return false
        }

        return true
    }
}
