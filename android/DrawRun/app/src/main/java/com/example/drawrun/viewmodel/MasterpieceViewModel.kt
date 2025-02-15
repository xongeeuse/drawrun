package com.example.drawrun.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.drawrun.data.dto.request.masterpiece.MasterpieceJoinRequest
import com.example.drawrun.data.dto.request.masterpiece.MasterpieceSaveRequest
import com.example.drawrun.data.dto.response.masterpiece.Masterpiece
import com.example.drawrun.data.dto.response.masterpiece.MasterpieceDetailResponse
import com.example.drawrun.data.dto.response.masterpiece.SectionInfoResponse
import com.example.drawrun.data.repository.MasterpieceRepository
import kotlinx.coroutines.launch

class MasterpieceViewModel(private val repository: MasterpieceRepository) : ViewModel() {
    private val _saveMasterpieceResult = MutableLiveData<Result<Int>>()
    val saveMasterpieceResult: LiveData<Result<Int>> = _saveMasterpieceResult

    private val _masterpieceList = MutableLiveData<List<Masterpiece>>()
    val masterpieceList: LiveData<List<Masterpiece>> get() = _masterpieceList

    private val _masterpieceDetail = MutableLiveData<MasterpieceDetailResponse>()
    val masterpieceDetail: LiveData<MasterpieceDetailResponse> get() = _masterpieceDetail

    private val _sectionInfo = MutableLiveData<SectionInfoResponse>()
    val sectionInfo: LiveData<SectionInfoResponse> get() = _sectionInfo

    private val _joinMasterpieceResult = MutableLiveData<Boolean>()
    val joinMasterpieceResult: LiveData<Boolean> = _joinMasterpieceResult

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

    fun fetchMasterpieceDetail(masterpieceBoardId: Int) {
        viewModelScope.launch {
            try {
                val response = repository.getMasterpieceDetail(masterpieceBoardId)
                if (response.isSuccessful) {
                    response.body()?.let { detail ->
                        _masterpieceDetail.value = detail
                        Log.d("MasterpieceViewModel", "Fetched Detail: $detail")
                    }
                } else {
                    Log.e("MasterpieceViewModel", "Error: ${response.code()} - ${response.message()}")
                }
            } catch (e: Exception) {
                Log.e("MasterpieceViewModel", "Exception: ${e.message}")
            }
        }
    }

    fun fetchMasterpieceSectionInfo(masterpieceBoardId: Int) {
        viewModelScope.launch {
            try {
                val result = repository.getMasterpieceSectionInfo(masterpieceBoardId)
                if (result.isSuccess) {
                    result.getOrNull()?.let { sectionInfo ->
                        _sectionInfo.value = sectionInfo
                        Log.d("MasterpieceViewModel", "Fetched Section Info: $sectionInfo")
                    }
                } else {
                    Log.e("MasterpieceViewModel", "Error fetching section info: ${result.exceptionOrNull()?.message}")
                }
            } catch (e: Exception) {
                Log.e("MasterpieceViewModel", "Exception fetching section info", e)
            }
        }
    }

    fun joinMasterpiece(masterpieceSegId: Int) {
        viewModelScope.launch {
            try {
                Log.d("MasterpieceViewModel", "Joining masterpiece with segId: $masterpieceSegId")
                val request = MasterpieceJoinRequest(masterpieceSegId)
                val result = repository.joinMasterpiece(request)
                Log.d("MasterpieceViewModel", "Join request: $request")
                Log.d("MasterpieceViewModel", "Join result: $result")
                _joinMasterpieceResult.value = result.isSuccess
                if (result.isSuccess) {
                    Log.d("MasterpieceViewModel", "Join successful")
                } else {
                    Log.e("MasterpieceViewModel", "Join failed. Error: ${result.exceptionOrNull()?.message}")
                }
            } catch (e: Exception) {
                Log.e("MasterpieceViewModel", "Exception during join: ${e.message}")
                _joinMasterpieceResult.value = false
            }
        }
    }






}