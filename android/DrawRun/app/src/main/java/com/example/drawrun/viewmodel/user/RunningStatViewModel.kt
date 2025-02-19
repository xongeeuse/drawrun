package com.example.drawrun.viewmodel.user

import androidx.lifecycle.*
import com.example.drawrun.data.dto.response.user.GetUserStatResponse
import com.example.drawrun.data.repository.UserRepository
import kotlinx.coroutines.launch

class RunningStatViewModel(private val userRepository: UserRepository) : ViewModel() {

    private val _runningStat = MutableLiveData<GetUserStatResponse>()
    val runningStat: LiveData<GetUserStatResponse> get() = _runningStat

    private val _errorState = MutableLiveData<String>()
    val errorState: LiveData<String> get() = _errorState

    // 러닝 통계 데이터 가져오기
    fun fetchRunningStat() {
        viewModelScope.launch {
            val result = userRepository.getUserStatInfo()
            result.onSuccess { statData ->
                _runningStat.value = statData // ✅ 성공 시 데이터 저장
            }.onFailure { error ->
                _errorState.value = error.message ?: "러닝 통계 데이터를 가져오는 중 오류 발생" // ❌ 실패 시 오류 메시지 저장
            }
        }
    }
}
