package com.example.drawrun

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.content.Intent  // Intent import 추가
import com.example.drawrun.databinding.ActivityMainBinding  // import 추가
import com.example.drawrun.ui.auth.signup.SignupActivity  // SignupActivity import 추가


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding  // binding 변수 선언

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)  // binding 초기화
        setContentView(binding.root)  // binding.root를 사용하여 레이아웃 설정

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // 회원가입 버튼 클릭 리스너
        binding.btnSignUp.setOnClickListener {
            val intent = Intent(this, SignupActivity::class.java)
            startActivity(intent)
        }
    }
}