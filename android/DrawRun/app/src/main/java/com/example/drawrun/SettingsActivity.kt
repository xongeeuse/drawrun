package com.example.drawrun

import android.os.Bundle
import android.widget.TextView
import com.example.drawrun.R
import com.example.drawrun.data.model.UserResponse
import com.example.drawrun.ui.common.BaseActivity
import com.example.drawrun.utils.MockRetrofitInstance
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SettingsActivity : BaseActivity() {

    override fun getLayoutId(): Int = R.layout.activity_settings

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val emailTextView: TextView = findViewById(R.id.emailValueTextView)
        val idTextView: TextView = findViewById(R.id.idValueTextView)

        // 사용자 데이터 가져오기
        MockRetrofitInstance.api.getUserData().enqueue(object : Callback<UserResponse> {
            override fun onResponse(call: Call<UserResponse>, response: Response<UserResponse>) {
                response.body()?.let { user ->
                    emailTextView.text = user.userEmail
                    idTextView.text = user.userId.toString()
                }
            }

            override fun onFailure(call: Call<UserResponse>, t: Throwable) {
                // 에러 처리
            }
        })
    }
}