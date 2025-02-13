package com.example.drawrun.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.drawrun.data.repository.RunRecordRepository
import com.example.drawrun.utils.RetrofitInstance

class RunRecordViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val api = RetrofitInstance.RunRecordApi(context)
        val repository = RunRecordRepository(api)

        if (modelClass.isAssignableFrom(RunRecordViewModel::class.java)) {
            return RunRecordViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}