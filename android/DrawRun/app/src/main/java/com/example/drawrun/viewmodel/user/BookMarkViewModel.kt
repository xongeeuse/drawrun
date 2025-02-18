package com.example.drawrun.viewmodel.user

import androidx.lifecycle.*
import com.example.drawrun.data.dto.response.user.GetBookMarkResponse
import com.example.drawrun.data.repository.UserRepository
import kotlinx.coroutines.launch

class BookMarkViewModel(private val userRepository: UserRepository) : ViewModel() {

    // ✅ LiveData: 북마크 데이터를 저장
    private val _bookmarkList = MutableLiveData<GetBookMarkResponse>()
    val bookmarkList: LiveData<GetBookMarkResponse> get() = _bookmarkList

    // ✅ LiveData: 오류 메시지 저장
    private val _errorState = MutableLiveData<String>()
    val errorState: LiveData<String> get() = _errorState

    /**
     * ✅ 북마크 데이터 가져오기 (API 호출)
     */
    fun fetchBookMarkInfo() {
        viewModelScope.launch {
            val result = userRepository.getBookmarkInfo()

            result.onSuccess { data ->
                _bookmarkList.value = data
            }.onFailure { error ->
                _errorState.value = error.message ?: "알 수 없는 오류 발생"
            }
        }
    }
}
