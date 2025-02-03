package com.example.drawrun.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.drawrun.data.model.LoginRequest
import com.example.drawrun.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class LoginViewModel(private val repository: AuthRepository) : ViewModel() {

    // 로그인 상태를 관리하는 MutableStateFlow ( UI와 데이터 상태 공유)
    private val _loginState = MutableStateFlow<LoginState>(LoginState.Idle)
    val loginState: StateFlow<LoginState> = _loginState  // 외부에서는 읽기 전용으로 제공

    fun login(userId: String, password: String) {
        viewModelScope.launch {
            _loginState.value = LoginState.Loading  // 로그인 요청 시작시 로딩 상태로 변경
            try {
                val request = LoginRequest(userId, password)  // 로그인 요청 데이터 생성
                val response = repository.login(request)    // Respository 통해 API 호출

                if (response.isSuccess) { // 로그인 성공 여부
                    val accessToken = response.data?.accessToken  // 서버로부터 받은 accessToken 추출
                    if (accessToken != null) {
                        _loginState.value = LoginState.Success(accessToken)
                        Log.d("LoginSuccess", "Token: ${response.data?.accessToken}")
                    } else {
                        _loginState.value = LoginState.Error("액세스 토큰이 없습니다.") // 토큰 누락 시 오류 처리
                        Log.e("LoginError", "Response: ${response.message}")
                    }
                } else {
                    _loginState.value = LoginState.Error(response.message)
                }
            } catch (e: Exception) {
                // 네트워크 오류 또는 예외 발생 시 오류 상태로 전환
                _loginState.value = LoginState.Error(e.message ?: "알 수 없는 오류 발생")
            }
        }
    }
}

sealed class LoginState {
    object Idle : LoginState()     //  초기 상태(아직 앙무 작업도 수행하지 않은 상태)
    object Loading : LoginState()   // 로딩 상태(로그인 요청 처리 중)
    data class Success(val accessToken: String) : LoginState()  // 로그인 성공 상태(accessToken 전달)
    data class Error(val message: String) : LoginState()    // 로그인 실패 또는 오류 발생 시 상태
}
