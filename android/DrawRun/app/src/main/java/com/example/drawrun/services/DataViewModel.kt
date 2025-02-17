package com.example.drawrun.services

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class DataViewModel : ViewModel() {
    private val _averageHeartRate = MutableLiveData<Float>()
    val averageHeartRate: LiveData<Float> = _averageHeartRate

    fun updateAverageHeartRate(value: Float) {
        _averageHeartRate.value = value
    }
}