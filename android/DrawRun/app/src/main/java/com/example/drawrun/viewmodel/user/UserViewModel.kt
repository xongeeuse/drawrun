package com.example.drawrun.viewmodel.user

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.drawrun.data.model.MypageResponse
import com.example.drawrun.data.repository.UserRepository
import kotlinx.coroutines.launch

class UserViewModel(private val repository: UserRepository) : ViewModel() {

    private val _userData = MutableLiveData<MypageResponse>()
    val userData: LiveData<MypageResponse> get() = _userData

    private val _errorState = MutableLiveData<String>()
    val errorState: LiveData<String> get() = _errorState

    private val _loadingState = MutableLiveData<Boolean>()
    val loadingState: LiveData<Boolean> get() = _loadingState

    // API 호출을 통해 데이터를 가져옴
    fun fetchUserData() {
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
    }
}
