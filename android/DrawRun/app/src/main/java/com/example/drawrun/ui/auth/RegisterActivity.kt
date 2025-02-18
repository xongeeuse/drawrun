package com.example.drawrun.ui.auth

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.drawrun.R
import com.example.drawrun.data.repository.AuthRepository
import com.example.drawrun.utils.RetrofitInstance
import com.example.drawrun.viewmodel.AuthViewModel
import com.example.drawrun.viewmodel.AuthViewModelFactory
import com.example.drawrun.viewmodel.RegistrationState
import kotlinx.coroutines.launch

class RegisterActivity : AppCompatActivity() {

    private lateinit var viewModel: AuthViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        // ViewModel 초기화
        val repository = AuthRepository(RetrofitInstance.api)
        val viewModelFactory = AuthViewModelFactory(repository)
        viewModel = ViewModelProvider(this, viewModelFactory)[AuthViewModel::class.java]

        val etUserId = findViewById<EditText>(R.id.etUserId)
        val etEmail = findViewById<EditText>(R.id.etEmail)
        val etPassword = findViewById<EditText>(R.id.etPassword)
        val etConfirmPassword = findViewById<EditText>(R.id.etConfirmPassword)
        val etUserName = findViewById<EditText>(R.id.etUserName)
        val etNickname = findViewById<EditText>(R.id.etNickname)
        val btnRegister = findViewById<Button>(R.id.btnRegister)

        val tvTitle = findViewById<TextView>(R.id.tvTitle)
        val customFont = ResourcesCompat.getFont(this, R.font.praise_regular)
        tvTitle.typeface = customFont

        btnRegister.setOnClickListener {
            val userId = etUserId.text.toString()
            val email = etEmail.text.toString()
            val password = etPassword.text.toString()
            val confirmPassword = etConfirmPassword.text.toString()
            val userName = etUserName.text.toString()
            val nickname = etNickname.text.toString()

            if (password == confirmPassword) {
                viewModel.register(userId, email, password, userName, nickname)
            } else {
                Toast.makeText(this, "패스워드가 일치하지 않습니다.", Toast.LENGTH_SHORT).show()
            }
        }

        lifecycleScope.launch {
            viewModel.registrationState.collect { state ->
                when (state) {
                    is RegistrationState.Loading -> {
                        // Show loading indicator
                    }
                    is RegistrationState.Success -> {
                        Toast.makeText(this@RegisterActivity, state.message, Toast.LENGTH_SHORT).show()
                        finish() // 성공 시 현재 액티비티 종료
                    }
                    is RegistrationState.Error -> {
                        Toast.makeText(this@RegisterActivity, state.message, Toast.LENGTH_SHORT).show()
                    }
                    else -> {}
                }
            }
        }
    }
}
