package com.example.drawrun.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.drawrun.data.dto.request.masterpiece.MasterpieceSaveRequest
import com.example.drawrun.data.dto.response.masterpiece.Masterpiece
import com.example.drawrun.data.repository.MasterpieceRepository
import kotlinx.coroutines.launch

class MasterpieceViewModel(private val repository: MasterpieceRepository) : ViewModel() {
    private val _saveMasterpieceResult = MutableLiveData<Result<Int>>()
    val saveMasterpieceResult: LiveData<Result<Int>> = _saveMasterpieceResult

    private val _masterpieceList = MutableLiveData<List<Masterpiece>>()
    val masterpieceList: LiveData<List<Masterpiece>> get() = _masterpieceList

    fun saveMasterpiece(request: MasterpieceSaveRequest) {
        viewModelScope.launch {
            try {
                Log.d("MasterpieceViewModel", "Saving masterpiece: $request")
                val result = repository.saveMasterpiece(request)
                _saveMasterpieceResult.value = Result.success(result)
            } catch (e: Exception) {
                Log.e("MasterpieceViewModel", "Error saving masterpiece", e)
                _saveMasterpieceResult.value = Result.failure(e)
            }
        }
    }

    fun getMasterpieceList() {
        viewModelScope.launch {
            try {
                val result = repository.getMasterpieceList()
                if (result.isSuccess) {
                    // dday가 0 이상인 데이터만 필터링
                    val filteredList = result.getOrNull()?.filter { it.dday >= 0 } ?: emptyList()
                    _masterpieceList.value = filteredList
                } else {
                    Log.e("MasterpieceViewModel", "Error fetching masterpiece list")
                }
            } catch (e: Exception) {
                Log.e("MasterpieceViewModel", "Exception fetching masterpiece list", e)
            }
        }
    }

}