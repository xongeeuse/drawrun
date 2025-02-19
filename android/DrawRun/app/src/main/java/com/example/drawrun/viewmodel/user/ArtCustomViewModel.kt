package com.example.drawrun.viewmodel.user

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.drawrun.data.dto.response.user.ArtData
import com.example.drawrun.data.repository.UserRepository
import kotlinx.coroutines.launch

class ArtCustomViewModel(private val repository: UserRepository) : ViewModel() {

    private val _artCollection = MutableLiveData<List<ArtData>>()
    val artCollection: LiveData<List<ArtData>> get() = _artCollection

    private val _errorState = MutableLiveData<String>()
    val errorState: LiveData<String> get() = _errorState

    private val _loadingState = MutableLiveData<Boolean>()
    val loadingState: LiveData<Boolean> get() = _loadingState

    fun fetchMyArtCustomInfo() {
        viewModelScope.launch {
            _loadingState.postValue(true)
            try {
                val response = repository.getMyArtCustomInfo()
                _artCollection.postValue(response.data.artList)
            } catch (e: Exception) {
                _errorState.postValue("아트 컬렉션 로드 실패: ${e.message}")
            } finally {
                _loadingState.postValue(false)
            }
        }
    }
}
