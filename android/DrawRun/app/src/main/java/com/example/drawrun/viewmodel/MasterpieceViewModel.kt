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

    private val _filteredMasterpieceList = MutableLiveData<List<Masterpiece>>()
    val filteredMasterpieceList: LiveData<List<Masterpiece>> = _filteredMasterpieceList

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
                    _masterpieceList.value = result.getOrNull() ?: emptyList()
                    // 초기 필터링 (그리는 중)
                    filterMasterpieces(isInProgress = true)
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

    fun joinMasterpiece(masterpieceSegId: Int, masterpieceBoardId: Int, position: Int) {
        viewModelScope.launch {
            try {
                val request = MasterpieceJoinRequest(masterpieceSegId)
                val result = repository.joinMasterpiece(request)
                if (result.isSuccess) {
                    Log.d("MasterpieceViewModel", "Join successful")
                    // 성공 시 해당 섹션의 nickname을 "달리는 중"으로 업데이트
                    _sectionInfo.value?.let { currentSections ->
                        val updatedSections = currentSections.toMutableList()
                        updatedSections[position] = updatedSections[position].copy(nickname = "달리는 중")
                        _sectionInfo.postValue(updatedSections)
                    }
                } else {
                    Log.e("MasterpieceViewModel", "Join failed. Error: ${result.exceptionOrNull()?.message}")
                }
                _joinMasterpieceResult.postValue(result.isSuccess)
            } catch (e: Exception) {
                Log.e("MasterpieceViewModel", "Exception during join: ${e.message}")
                _joinMasterpieceResult.postValue(false)
            }
        }
    }


    fun filterMasterpieces(isInProgress: Boolean) {
        val filteredList = _masterpieceList.value?.filter { masterpiece ->
            val isCompleted = masterpiece.restrictCount == masterpiece.joinCount
            when (isInProgress) {
                true -> masterpiece.dday >= 0 && !isCompleted
                false -> isCompleted
            }
        } ?: emptyList()
        _filteredMasterpieceList.value = filteredList
    }

    fun searchMasterpieces(query: String, isInProgress: Boolean) {
        viewModelScope.launch {
            val filteredList = _masterpieceList.value?.filter {
                it.gu.contains(query, ignoreCase = true) &&
                        when (isInProgress) {
                            true -> it.dday >= 0 && it.restrictCount != it.joinCount
                            false -> it.restrictCount == it.joinCount
                        }
            } ?: emptyList()
            _filteredMasterpieceList.value = filteredList
        }
    }


}