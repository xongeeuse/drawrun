package com.example.drawrun.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.drawrun.data.model.AuthRequest
import com.example.drawrun.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AuthViewModel(private val repository: AuthRepository) : ViewModel() {

    private val _registrationState = MutableStateFlow<RegistrationState>(RegistrationState.Idle)
    val registrationState: StateFlow<RegistrationState> = _registrationState

    fun register(userId: String, email: String, password: String, userName: String, nickname: String) {
        viewModelScope.launch {
            _registrationState.value = RegistrationState.Loading
            try {
                val request = AuthRequest(userId, email, password, userName, nickname)
                val response = repository.register(request)
                if (response.isSuccess) {
                    _registrationState.value = RegistrationState.Success(response.message)
                } else {
                    _registrationState.value = RegistrationState.Error(response.message)
                }
            } catch (e: Exception) {
                _registrationState.value = RegistrationState.Error(e.message ?: "Unknown error occurred")
            }
        }
    }
}

sealed class RegistrationState {
    object Idle : RegistrationState()
    object Loading : RegistrationState()
    data class Success(val message: String) : RegistrationState()
    data class Error(val message: String) : RegistrationState()
}
