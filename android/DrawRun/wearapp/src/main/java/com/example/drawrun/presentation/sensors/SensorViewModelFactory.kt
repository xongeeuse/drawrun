package com.example.drawrun.presentation.sensors

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class SensorViewModelFactory(
    private val sensorManagerHelper: SensorManagerHelper
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SensorViewModel::class.java)) {
            return SensorViewModel(sensorManagerHelper) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}


