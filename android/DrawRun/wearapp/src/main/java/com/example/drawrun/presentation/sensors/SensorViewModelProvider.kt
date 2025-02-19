package com.example.drawrun.presentation.sensors

object SensorViewModelProvider {
    private var instance: SensorViewModel? = null

    fun getInstance(sensorManagerHelper: SensorManagerHelper): SensorViewModel {
        if (instance == null) {
            instance = SensorViewModel(sensorManagerHelper)
        }
        return instance!!
    }
}