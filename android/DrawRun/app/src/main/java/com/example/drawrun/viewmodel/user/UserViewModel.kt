package com.example.drawrun.viewmodel.user

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.drawrun.data.dto.response.user.GetMyInfoResponse
import com.example.drawrun.data.dto.response.user.UserHistory
import com.example.drawrun.data.model.MypageResponse
import com.example.drawrun.data.repository.UserRepository
import kotlinx.coroutines.launch

class UserViewModel(private val repository: UserRepository) : ViewModel() {

    private val _userData = MutableLiveData<GetMyInfoResponse>()
    val userData: LiveData<GetMyInfoResponse> get() = _userData

    private val _errorState = MutableLiveData<String>()
    val errorState: LiveData<String> get() = _errorState

    private val _loadingState = MutableLiveData<Boolean>()
    val loadingState: LiveData<Boolean> get() = _loadingState

    private val _runningHistory = MutableLiveData<List<UserHistory>>()
    val runningHistory: LiveData<List<UserHistory>> get() = _runningHistory

    // API 호출을 통해 데이터를 가져옴
    /*fun fetchUserData() {
        viewModelScope.launch {
            _loadingState.postValue(true)
            try {
                val data = repository.getUserData()
                _userData.postValue(data)
            } catch (e: Exception) {
                _errorState.postValue("error")
            } finally {
                _loadingState.postValue(false)
            }
        }
    }*/

    // 사용자 유저 정보
    fun fetchUserInfo() {
        viewModelScope.launch {
            val result = repository.getUserInfo()
            result.onSuccess { response ->
                _userData.postValue(response)
            }.onFailure { error ->
                _errorState.postValue(error.message ?: "알 수 없는 오류 발생")
            }
        }
    }

    // 사용자 러닝 기록
    fun fetchRunningHistory() {
        viewModelScope.launch {
            val result = repository.getUserInfo()
            result.onSuccess { response ->
                _runningHistory.postValue(response.data.history)
            }.onFailure { error ->
                Log.e("UserViewModel", "러닝 기록 가져오기 실패: ${error.message}")
            }
        }
    }


}
